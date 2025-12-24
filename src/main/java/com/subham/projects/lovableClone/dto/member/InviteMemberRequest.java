package com.subham.projects.lovableClone.dto.member;

import com.subham.projects.lovableClone.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
