package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.ProjectMember;
import com.subham.projects.lovableClone.entity.ProjectMemberId;
import com.subham.projects.lovableClone.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findByIdProjectId(Long projectId);

    @Query("SELECT pm.projectRole FROM ProjectMember pm" + " WHERE pm.id.projectId =:projectId AND pm.id.userId=:userId")
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}