package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.project.FileNode;
import com.subham.projects.lovableClone.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {
    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
