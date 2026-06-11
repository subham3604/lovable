package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.chat.ChatResponse;
import com.subham.projects.lovableClone.entity.ChatMessage;
import com.subham.projects.lovableClone.entity.ChatSession;
import com.subham.projects.lovableClone.entity.ChatSessionId;
import com.subham.projects.lovableClone.mapper.ChatMessageMapper;
import com.subham.projects.lovableClone.repository.ChatMessageRepository;
import com.subham.projects.lovableClone.repository.ChatSessionRepository;
import com.subham.projects.lovableClone.security.AuthUtil;
import com.subham.projects.lovableClone.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(new ChatSessionId(projectId, userId));

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatSession(chatSession);

        return chatMessageMapper.toChatResponseList(chatMessages);
    }
}
