package com.subham.projects.lovableClone.dto.member;

import com.subham.projects.lovableClone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(@NotNull ProjectRole role) {
}
