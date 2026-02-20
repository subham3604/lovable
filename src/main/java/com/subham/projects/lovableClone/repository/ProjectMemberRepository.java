package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.ProjectMember;
import com.subham.projects.lovableClone.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findByIdProjectId(Long projectId);
}