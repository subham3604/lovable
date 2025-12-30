package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.project.ProjectResponse;
import com.subham.projects.lovableClone.dto.project.ProjectSummaryResponse;
import com.subham.projects.lovableClone.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponse toProjectResponse(Project project);
    ProjectSummaryResponse toProjectSummaryResponse(Project project);
    List<ProjectSummaryResponse> toListProjectSummaryResponse(List<Project> projects);
}
