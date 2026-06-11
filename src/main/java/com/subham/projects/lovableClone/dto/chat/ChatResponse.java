package com.subham.projects.lovableClone.dto.chat;

import com.subham.projects.lovableClone.entity.ChatEvent;
import com.subham.projects.lovableClone.entity.ChatSession;
import com.subham.projects.lovableClone.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        ChatSession chatSession,
        List<ChatEvent> chatEvents,
        String content,
        MessageRole role, // USER, ASSISTANT
        Integer tokensUsed,
        Instant createdAt
) {
}
