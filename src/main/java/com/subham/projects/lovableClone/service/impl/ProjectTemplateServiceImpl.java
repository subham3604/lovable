package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.entity.Project;
import com.subham.projects.lovableClone.entity.ProjectFile;
import com.subham.projects.lovableClone.error.ResourceNotFoundException;
import com.subham.projects.lovableClone.repository.ProjectFileRepository;
import com.subham.projects.lovableClone.repository.ProjectRepository;
import com.subham.projects.lovableClone.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private static final String TEMPLATE_BUCKET = "project-starter-files";
    private static final String TARGET_BUCKET = "lovable";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter-main";

    @Override
    public void initializeProjectFromTemplate(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        String sourcePrefix = TEMPLATE_NAME + "/";
        String targetPrefix = projectId + "/";

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(sourcePrefix)
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>();

            for (Result<Item> result : results) {
                Item item = result.get();

                if (item.isDir()) {
                    continue;
                }

                String sourceObject = item.objectName();

                String relativePath =
                        sourceObject.substring(sourcePrefix.length());

                String targetObject =
                        targetPrefix + relativePath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(targetObject)
                                .source(
                                        SourceObject.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceObject)
                                                .build()
                                )
                                .build()
                );

                ProjectFile projectFile = ProjectFile.builder()
                        .project(project)
                        .path(relativePath)
                        .minioObjectKey(targetObject)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(projectFile);
            }

            projectFileRepository.saveAll(filesToSave);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize project from template",
                    e
            );
        }
    }
}
