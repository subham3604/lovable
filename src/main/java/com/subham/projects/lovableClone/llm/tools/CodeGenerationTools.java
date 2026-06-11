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
//        log.info("READ_FILES TOOL INVOKED");
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
