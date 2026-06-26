package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.deploy.DeployResponse;
import com.subham.projects.lovableClone.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesDeploymentServiceImpl implements DeploymentService {

    private final KubernetesClient kubernetesClient;
    private final StringRedisTemplate redisTemplate;

    private static final String NAMESPACE = "genesis-ns";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private final Map<Long, Instant> activePreviews = new ConcurrentHashMap<>();

    @Value("${app.preview.idle-timeout:5m}")
    private Duration idleTimeout;

    @Value("${app.preview.base-domain:app.domain.com}")
    private String baseDomain;

    @Value("${app.preview.port:8090}")
    private String previewPort;

    private String getPreviewUrl(String domain) {
        if (previewPort == null || previewPort.trim().isEmpty() || "80".equals(previewPort) || "443".equals(previewPort)) {
            return "http://" + domain;
        }
        return "http://" + domain + ":" + previewPort;
    }

    @Override
    public DeployResponse deploy(Long projectId) {
        String domain = "project-" + projectId + "." + baseDomain;
        Pod existingPod = findAlreadyExistingPod(projectId);
        Boolean routeExists = Boolean.TRUE.equals(redisTemplate.hasKey("route:" + domain));

        if (existingPod != null && routeExists) {
            activePreviews.put(projectId, Instant.now());
            registerRoute(domain, existingPod);
            return new DeployResponse(getPreviewUrl(domain));
        } else if (existingPod != null) {
            log.warn(
                    "Pod exists for project {} but route is missing in Redis. Cleaning up pod to trigger a fresh deployment.",
                    projectId);
            stop(projectId);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
        }

        DeployResponse response = claimAndStartNewPod(projectId, domain);
        activePreviews.put(projectId, Instant.now());
        return response;
    }

    private DeployResponse claimAndStartNewPod(Long projectId, String domain) {
        Pod pod = kubernetesClient.pods().inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL, IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No free pods available, please scale up the runner-pool."));

        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);

        kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName).edit(p -> {
            p.getMetadata().getLabels().put(POOL_LABEL, BUSY);
            p.getMetadata().getLabels().put(PROJECT_LABEL, projectId.toString());
            return p;
        });

        // * SYNCER
        String initialSyncCommand = String.format(
                "mc mirror --overwrite cloudflareR2/lovable/%d/ /app/",
                projectId);
        execCommand(podName, SYNCER_CONTAINER, "sh", "-c", initialSyncCommand);

        String watchCommand = String.format(
                "nohup sh -c 'while true; do mc mirror --overwrite cloudflareR2/lovable/%d/ /app/; sleep 1; done' < /dev/null > /app/sync.log 2>&1 &",
                projectId);
        execCommand(podName, SYNCER_CONTAINER, "sh", "-c", watchCommand);

        // * RUNNER
        execCommand(podName, RUNNER_CONTAINER, "sh", "-c", "npm install");

        String startCmd = "nohup npm run dev -- --host 0.0.0.0 --port 5173 < /dev/null > /app/dev.log 2>&1 &";
        execCommand(podName, RUNNER_CONTAINER, "sh", "-c", startCmd);

        // * Register the route in redis
        registerRoute(domain, pod);

        log.info("Deployment successful: {}", getPreviewUrl(domain));
        return new DeployResponse(getPreviewUrl(domain));
    }

    private void execCommand(String podName, String container, String... commands) {
        log.debug("Executing in {}:{}: {}", podName, container, String.join(" ", commands));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        try {
            ExecWatch watch = kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName)
                    .inContainer(container)
                    .writingOutput(out)
                    .writingError(err)
                    .exec(commands);

            boolean isBackground = commands[commands.length - 1].trim().endsWith("&");

            if (isBackground) {
                // Wait briefly for the background process to start and make sure it doesn't
                // fail instantly
                Thread.sleep(1500);
                watch.close();
                log.debug("Background command triggered in {}. Output so far: {}", container, out.toString());
            } else {
                try (watch) {
                    // Block and wait for execution to finish
                    Integer exitCode = watch.exitCode().get(120, TimeUnit.SECONDS);
                    String stdOut = out.toString();
                    String stdErr = err.toString();

                    log.debug("Command completed in {}. StdOut: {}", container, stdOut);
                    if (stdErr != null && !stdErr.trim().isEmpty()) {
                        log.warn("Command stderr in {}: {}", container, stdErr);
                    }

                    if (exitCode != 0) {
                        throw new RuntimeException("Command exited with code " + exitCode + ". Stderr: " + stdErr);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Execution failed for command on {}:{}. Output: {}, Error: {}",
                    podName, container, out.toString(), err.toString(), e);
            throw new RuntimeException("Kubernetes exec failed on container " + container + ": " + e.getMessage(), e);
        }
    }

    private void registerRoute(String domain, Pod pod) {
        String podIp = pod.getStatus().getPodIP();
        if (podIp == null)
            throw new RuntimeException("Pod is running but has no ip.");

        redisTemplate.opsForValue().set("route:" + domain, podIp + ":5173", 5, TimeUnit.MINUTES);
    }

    @Override
    public void keepAlive(Long projectId) {
        log.debug("Heartbeat received for project {}", projectId);
        activePreviews.put(projectId, Instant.now());
        try {
            Pod pod = findAlreadyExistingPod(projectId);
            if (pod != null) {
                String domain = "project-" + projectId + "." + baseDomain;
                registerRoute(domain, pod);
            }
        } catch (Exception e) {
            log.error("Failed to refresh Redis route during heartbeat for project {}", projectId, e);
        }
    }

    @Override
    public void stop(Long projectId) {
        log.info("Request to stop preview for project {}", projectId);
        Pod pod = findAlreadyExistingPod(projectId);
        if (pod != null) {
            String podName = pod.getMetadata().getName();
            log.info("Deleting pod {} for project {}", podName, projectId);
            try {
                kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName).withGracePeriod(0).delete();
            } catch (Exception e) {
                log.error("Failed to delete pod {}", podName, e);
                throw e;
            }
        }
        activePreviews.remove(projectId);
    }

    @Override
    public boolean isDeployed(Long projectId) {
        String domain = "project-" + projectId + "." + baseDomain;
        Pod existingPod = findAlreadyExistingPod(projectId);
        Boolean routeExists = Boolean.TRUE.equals(redisTemplate.hasKey("route:" + domain));
        return existingPod != null && routeExists;
    }

    @Scheduled(fixedDelay = 10000)
    public void reapInactivePods() {
        Instant now = Instant.now();
        activePreviews.forEach((projectId, lastHeartbeat) -> {
            if (now.isAfter(lastHeartbeat.plus(idleTimeout))) {
                log.info("Project {} preview idle timeout reached. Initiating cleanup.", projectId);
                try {
                    stop(projectId);
                } catch (Exception e) {
                    log.error("Failed to stop preview pod for project {}", projectId, e);
                }
            }
        });
    }

    private Pod findAlreadyExistingPod(Long projectId) {
        try {
            return kubernetesClient.pods().inNamespace(NAMESPACE)
                    .withLabel(PROJECT_LABEL, projectId.toString())
                    .withLabel(POOL_LABEL, BUSY)
                    .list().getItems().stream()
                    .filter(pod -> {
                        String phase = pod.getStatus() != null ? pod.getStatus().getPhase() : "UNKNOWN";
                        return "Running".equalsIgnoreCase(phase);
                    })
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to query Kubernetes pods for project {}", projectId, e);
            throw e;
        }
    }
}
