package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.project.FileContentResponse;
import com.subham.projects.lovableClone.dto.project.FileNode;

import java.util.List;

public interface FileService {
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, String path, Long userId);
}
