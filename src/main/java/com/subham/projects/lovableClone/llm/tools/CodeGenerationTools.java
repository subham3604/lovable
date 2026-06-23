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
<<<<<<< Updated upstream
    private final Set<String> alreadyReadFiles = new HashSet<>();

    public CodeGenerationTools(ProjectFileService projectFileService, Long projectId) {
        this.projectFileService = projectFileService;
        this.projectId = projectId;
    }
=======
    private final Set<String> readFilesThisRequest = new HashSet<>();
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
            if (alreadyReadFiles.contains(cleanPath)) {
                log.warn("Circuit Breaker: File {} has already been read in this request.", cleanPath);
                result.add(String.format(
                        "FILE: %s\n\n[Warning: You have already read this file in this chat turn. Do not call read_files on it again. Please proceed to write your planned changes using <file> tags directly.]",
=======
            if (readFilesThisRequest.contains(cleanPath)) {
                log.warn("Soft circuit breaker triggered for file: {}", cleanPath);
                result.add(String.format(
                        """
                        FILE: %s
                        
                        [CIRCUIT BREAKER] You have already read the contents of this file during this tool execution loop.
                        Do not request to read this file again. Use the previously returned contents of this file.
                        """,
>>>>>>> Stashed changes
                        cleanPath
                ));
                continue;
            }

<<<<<<< Updated upstream
            alreadyReadFiles.add(cleanPath);
=======
            readFilesThisRequest.add(cleanPath);

>>>>>>> Stashed changes
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
