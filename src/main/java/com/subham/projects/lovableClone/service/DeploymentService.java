package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.deploy.DeployResponse;

public interface DeploymentService {
    DeployResponse deploy(Long projectId);
    void keepAlive(Long projectId);
    void stop(Long projectId);
    boolean isDeployed(Long projectId);
}
