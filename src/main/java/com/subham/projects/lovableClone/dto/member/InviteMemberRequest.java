package com.subham.projects.lovableClone.dto.member;

import com.subham.projects.lovableClone.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(@Email @NotBlank String email, @NotNull ProjectRole role) {
}
