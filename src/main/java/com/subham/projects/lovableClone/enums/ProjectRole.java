package com.subham.projects.lovableClone.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.subham.projects.lovableClone.enums.ProjectPermissions.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    EDITOR(Set.of(VIEW, EDIT, DELETE, VIEW_MEMBERS)),
    VIEWER(Set.of(VIEW, VIEW_MEMBERS)),
    OWNER(Set.of(VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS));

    ProjectRole(ProjectPermissions... projectPermissions) {
        this.permissions = Set.of(projectPermissions);
    }

    private final Set<ProjectPermissions> permissions;
}
