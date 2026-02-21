package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.project.ProjectRequest;
import com.subham.projects.lovableClone.dto.project.ProjectResponse;
import com.subham.projects.lovableClone.dto.project.ProjectSummaryResponse;
import com.subham.projects.lovableClone.entity.Project;
import com.subham.projects.lovableClone.entity.ProjectMember;
import com.subham.projects.lovableClone.entity.ProjectMemberId;
import com.subham.projects.lovableClone.entity.User;
import com.subham.projects.lovableClone.enums.ProjectRole;
import com.subham.projects.lovableClone.error.ResourceNotFoundException;
import com.subham.projects.lovableClone.mapper.ProjectMapper;
import com.subham.projects.lovableClone.repository.ProjectMemberRepository;
import com.subham.projects.lovableClone.repository.ProjectRepository;
import com.subham.projects.lovableClone.repository.UserRepository;
import com.subham.projects.lovableClone.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    ProjectMemberRepository projectMemberRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        return projectMapper.toListProjectSummaryResponse(projectRepository.findAllAccessibleByUser(userId));
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", userId.toString())
        );

        Project project = Project.builder().name(request.name()).build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .project(project)
                .invitedAt(Instant.now())
                .acceptedAt(Instant.now())
                .build();

        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse getUserProjectById(Long projectId, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);
        return projectMapper.toProjectResponse(project);

    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectRequest request, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);
        project.setName(request.name());
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {
        Project project = getAccessibleProjectById(id, userId);
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }


    // INTERNAL FUNCTIONS
    private Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleByProjectId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
