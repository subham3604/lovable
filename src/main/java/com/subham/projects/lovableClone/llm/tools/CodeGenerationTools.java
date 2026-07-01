package com.subham.projects.lovableClone.llm.tools;


import com.subham.projects.lovableClone.service.ProjectFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CodeGenerationTools {

    private final ProjectFileService projectFileService;
    private final Long projectId;
    private final Set<String> alreadyReadFiles = new HashSet<>();

    public CodeGenerationTools(ProjectFileService projectFileService, Long projectId) {
        this.projectFileService = projectFileService;
        this.projectId = projectId;
    }

    @Tool(
            name = "read_files",
            description = """
                    Reads the contents of files.
                    
                    IMPORTANT:
                    When you need file contents, ALWAYS call this tool.
                    Do not pretend to read files.
                    Do not describe reading files.
                    Invoke the tool directly.
                    """)
    public List<String> readFiles(
            @ToolParam(description = "List of relative paths. e.g, ['src/App.tsx']")
            List<String> paths) {
        List<String> result = new ArrayList<>();

        for (String path : paths) {
            log.info("TOOL EXECUTED: read_files {}", path);
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            if (alreadyReadFiles.contains(cleanPath)) {
                log.warn("Circuit Breaker: File {} has already been read in this request.", cleanPath);
                result.add(String.format(
                        "FILE: %s\n\n[Warning: You have already read this file in this chat turn. Do not call read_files on it again. Please proceed to write your planned changes using <file> tags directly.]",
                        cleanPath
                ));
                continue;
            }

            alreadyReadFiles.add(cleanPath);
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
