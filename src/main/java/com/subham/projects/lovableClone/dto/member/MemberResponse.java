package com.subham.projects.lovableClone.dto.member;

import com.subham.projects.lovableClone.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
