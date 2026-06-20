package com.subham.projects.lovableClone.dto.project;

import com.subham.projects.lovableClone.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        ProjectRole projectRole) {
}
