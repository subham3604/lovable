package com.subham.projects.lovableClone.dto.chat;

import com.subham.projects.lovableClone.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
