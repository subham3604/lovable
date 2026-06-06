package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.project.FileContentResponse;
import com.subham.projects.lovableClone.dto.project.FileNode;

import java.util.List;

public interface ProjectFileService {
    List<FileNode> getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(String filePath, String fileContent, Long projectId);
}
