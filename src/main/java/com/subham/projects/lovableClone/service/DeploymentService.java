package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.deploy.DeployResponse;

public interface DeploymentService {
    DeployResponse deploy(Long projectId);
}
