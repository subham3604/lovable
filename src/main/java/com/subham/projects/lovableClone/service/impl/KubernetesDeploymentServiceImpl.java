package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.deploy.DeployResponse;
import com.subham.projects.lovableClone.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private static final String NAMESPACE = "genesis-ns";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_PORT = "8090";

    private final Map<Long, Instant> activePreviews = new ConcurrentHashMap<>();

    @Value("${app.preview.idle-timeout:5m}")
    private Duration idleTimeout;

    @Override
    public DeployResponse deploy(Long projectId) {
        String domain = "project-" + projectId + ".app.domain.com";
        Pod existingPod = findAlreadyExistingPod(projectId);

        if (existingPod != null) {
            activePreviews.put(projectId, Instant.now());
            return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
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

        //* SYNCER
        String initialSyncCommand = String.format(
                "mc mirror --overwrite myminio/lovable/%d/ /app/",
                projectId);
        execCommand(podName, SYNCER_CONTAINER, "sh", "-c", initialSyncCommand);

        String watchCommand = String.format(
                "nohup mc mirror --overwrite --watch myminio/lovable/%d/ /app/ < /dev/null > /app/sync.log 2>&1 &", projectId
        );
        execCommand(podName, SYNCER_CONTAINER, "sh", "-c", watchCommand);


        //* RUNNER
        execCommand(podName, RUNNER_CONTAINER, "sh", "-c", "npm install");

        String startCmd = "nohup npm run dev -- --host 0.0.0.0 --port 5173 < /dev/null > /app/dev.log 2>&1 &";
        execCommand(podName, RUNNER_CONTAINER, "sh", "-c", startCmd);

        log.info("Deployment successful http://{}:{}", domain, REVERSE_PROXY_PORT);
        return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
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
                // Wait briefly for the background process to start and make sure it doesn't fail instantly
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

    @Override
    public void keepAlive(Long projectId) {
        log.debug("Heartbeat received for project {}", projectId);
        activePreviews.put(projectId, Instant.now());
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
