package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.project.FileContentResponse;
import com.subham.projects.lovableClone.dto.project.FileNode;
import com.subham.projects.lovableClone.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }
}
