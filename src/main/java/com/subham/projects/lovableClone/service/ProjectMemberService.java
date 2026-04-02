package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.member.InviteMemberRequest;
import com.subham.projects.lovableClone.dto.member.MemberResponse;
import com.subham.projects.lovableClone.dto.member.UpdateMemberRoleRequest;
import com.subham.projects.lovableClone.entity.ProjectMember;

import java.lang.reflect.Member;
import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void deleteProjectMember(Long projectId, Long memberId);
}
