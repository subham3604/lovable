package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.llm.PromptUtil;
import com.subham.projects.lovableClone.llm.advisors.FileTreeContextAdvisor;
import com.subham.projects.lovableClone.llm.tools.CodeGenerationTools;
import com.subham.projects.lovableClone.security.AuthUtil;
import com.subham.projects.lovableClone.service.AiService;
import com.subham.projects.lovableClone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {
    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        createChatSessionIfNotExists(userId, projectId);

        Map<String, Object> advisorParams = Map.of("userId", userId, "projectId", projectId);
        StringBuilder chatResponseBuilder = new StringBuilder();


        /*
            The file tree could have been added to the system prompt by just doing
            return chatClient.prompt().system(PromptUtil.CODE_GENERATION_SYSTEM_PROMPT + FileTreeService.getFiletree())

            but just to keep up with the spring ai documentation and the best practice that the system prompt should be
            mutated with the help of advisors.
        * */

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        log.info("Tool instance created: {}", codeGenerationTools);
        return chatClient.prompt()
                .system(PromptUtil.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String chatContent = response.getResult().getOutput().getText();

                    if (chatContent != null) {
                        chatResponseBuilder.append(chatContent);
                    }
                })
                .doOnComplete(() -> Schedulers.boundedElastic().schedule(() -> parseAndSaveFiles(chatResponseBuilder.toString(), projectId)))
                .doOnError
                        (error -> log.error("Error during streaming for projectId: {}", projectId, error))
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return text == null ? "" : text;
                })
                .filter(text -> !text.isBlank());
    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();
            projectFileService.saveFile(filePath, fileContent, projectId);
        }
    }

    private void createChatSessionIfNotExists(Long userId, Long projectId) {
    }
}
