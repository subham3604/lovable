package com.subham.projects.lovableClone.llm.tools;


import com.subham.projects.lovableClone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTools {

    private final ProjectFileService projectFileService;
    private final Long projectId;

    @Tool(
            name = "read_files",
            description = "Read the content of the files. Only input the file names present inside the FILE_TREE. Do not input any path that is not present inside the FILE_TREE."
    )
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths. e.g, ['src/App.tsx']")
            List<String> paths) {

        List<String> result = new ArrayList<>();

        for (String path : paths) {
            log.info("TOOL EXECUTED: read_files {}", paths);
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            log.info("Requested file: {}", cleanPath);
            String content = projectFileService.getFileContent(projectId, cleanPath).content();
            result.add(String.format(
                    """
                    FILE: %s
                    
                    
                    %s
                    """,
                    cleanPath,
                    content
            ));
            log.info("TOOL RETURNING {} chars", content.length());
        }
        return result;
    }
}
