package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    Optional<ProjectFile> findByProjectIdAndPath(Long projectId, String cleanfilePath);

    List<ProjectFile> findByProjectId(Long projectId);
}