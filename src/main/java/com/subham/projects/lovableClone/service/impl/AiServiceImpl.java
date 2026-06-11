package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.entity.*;
import com.subham.projects.lovableClone.enums.ChatEventType;
import com.subham.projects.lovableClone.enums.MessageRole;
import com.subham.projects.lovableClone.error.ResourceNotFoundException;
import com.subham.projects.lovableClone.llm.LlmResponseParser;
import com.subham.projects.lovableClone.llm.PromptUtil;
import com.subham.projects.lovableClone.llm.advisors.FileTreeContextAdvisor;
import com.subham.projects.lovableClone.llm.tools.CodeGenerationTools;
import com.subham.projects.lovableClone.repository.*;
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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {
    private final ChatEventRepository chatEventRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(userId, projectId);

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

//                    log.info("STREAM CHUNK:\n{}", chatContent);
                    if (chatContent != null) {
                        chatResponseBuilder.append(chatContent);
                    }

                })
                .doOnComplete(() ->
                                Schedulers.boundedElastic().schedule(() ->
                                                finaliseChats(userMessage, chatSession, chatResponseBuilder.toString(), projectId)
//                                parseAndSaveFiles(chatResponseBuilder.toString(), projectId)
                                )
                )
                .doOnError
                        (error -> log.error("Error during streaming for projectId: {}", projectId, error))
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return text == null ? "" : text;
                })
                .filter(text -> !text.isBlank());
    }

    private void finaliseChats(String userMessage, ChatSession chatSession, String fullText, Long projectId) {
//        log.info("Starting finaliseChats");
        try {
            // * Save the user message
            log.info("Saving USER message: {}", userMessage);
            chatMessageRepository.save(
                    ChatMessage.builder()
                            .chatSession(chatSession)
                            .role(MessageRole.USER)
                            .content(userMessage)
                            .build()
            );
            log.info("Saved USER message.");
            ChatMessage assistantMessage =
                    chatMessageRepository.save(
                            ChatMessage.builder()
                                    .chatSession(chatSession)
                                    .role(MessageRole.ASSISTANT)
                                    .content(fullText)
                                    .build()
                    );

            List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantMessage);

            //* Add logging
            log.info(
                    "Parsed {} events",
                    chatEventList.size()
            );

            chatEventList.stream()
                    .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                    .forEach(e -> projectFileService.saveFile(e.getFilePath(), e.getContent(), projectId));

            chatEventList.forEach(e ->
                    log.info(
                            "Type={}, File={}, MessageId={}",
                            e.getType(),
                            e.getFilePath(),
                            e.getChatMessage().getId()
                    )
            );

            chatEventRepository.saveAll(chatEventList);
        } catch (Exception e) {
            log.error("Failed to finalise chats", e);
        }
    }

    private ChatSession createChatSessionIfNotExists(Long userId, Long projectId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if (chatSession == null) {
            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                    new ResourceNotFoundException("Project", projectId.toString()));
            User user = userRepository.findById(userId).orElseThrow(() ->
                    new ResourceNotFoundException("User", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }

        return chatSession;
    }
}
