package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.project.ProjectRequest;
import com.subham.projects.lovableClone.dto.project.ProjectResponse;
import com.subham.projects.lovableClone.dto.project.ProjectSummaryResponse;
import com.subham.projects.lovableClone.entity.Project;
import com.subham.projects.lovableClone.entity.User;
import com.subham.projects.lovableClone.mapper.ProjectMapper;
import com.subham.projects.lovableClone.repository.ProjectRepository;
import com.subham.projects.lovableClone.repository.UserRepository;
import com.subham.projects.lovableClone.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummaryResponse)
//                .toList();
        return projectMapper.toListProjectSummaryResponse(projectRepository.findAllAccessibleByUser(userId));
    }

    @Override
    public ProjectResponse getUserProjectById(Long projectId, Long userId) {
        return null;
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow();
        Project project = Project.builder().name(request.name()).owner(owner).build();

        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        return null;
    }

    @Override
    public void softDelete(Long id, Long userId) {

    }
}
