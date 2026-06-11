package com.subham.projects.lovableClone.dto.project;

public record FileNode(
        String path
) {
    @Override
    public String toString() {
        return path;
    }
}
