package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.member.InviteMemberRequest;
import com.subham.projects.lovableClone.dto.member.MemberResponse;
import com.subham.projects.lovableClone.dto.member.UpdateMemberRoleRequest;
import com.subham.projects.lovableClone.entity.Project;
import com.subham.projects.lovableClone.entity.ProjectMember;
import com.subham.projects.lovableClone.entity.ProjectMemberId;
import com.subham.projects.lovableClone.entity.User;
import com.subham.projects.lovableClone.error.ForbiddenException;
import com.subham.projects.lovableClone.error.ResourceNotFoundException;
import com.subham.projects.lovableClone.mapper.ProjectMemberMapper;
import com.subham.projects.lovableClone.repository.ProjectMemberRepository;
import com.subham.projects.lovableClone.repository.ProjectRepository;
import com.subham.projects.lovableClone.repository.UserRepository;
import com.subham.projects.lovableClone.service.ProjectMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;

    @Override
    public List<MemberResponse> getProjectMembers(Long projectId, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);

        return projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toMemberResponseFromMember).toList();
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);

        User invitee = userRepository.findByUsername(request.username()).orElseThrow(() -> new ResourceNotFoundException("User", request.username()));

        if (invitee.getId().equals(userId)) {
            throw new ForbiddenException("You can not invite yourself.");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new ForbiddenException("User is already a member of this project.");
        }

        ProjectMember member = ProjectMember.builder().id(projectMemberId).project(project).user(invitee).projectRole(request.role()).invitedAt(Instant.now()).build();

        projectMemberRepository.save(member);

        return projectMemberMapper.toMemberResponseFromMember(member);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow(() -> new ResourceNotFoundException("Project Member", memberId.toString()));

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toMemberResponseFromMember(projectMember);

    }

    @Override
    public void deleteProjectMember(Long projectId, Long memberId, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Project Member does not exist.");
        }

        projectMemberRepository.deleteById(projectMemberId);
    }

    // INTERNAL FUNCTIONS
    private Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleByProjectId(projectId, userId).orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
