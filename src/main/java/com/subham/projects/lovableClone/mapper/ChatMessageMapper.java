package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.chat.ChatResponse;
import com.subham.projects.lovableClone.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    List<ChatResponse> toChatResponseList(List<ChatMessage> chatMessages);
}
