package com.subham.projects.lovableClone.service;
import com.subham.projects.lovableClone.dto.chat.ChatResponse;
import java.util.List;

public interface ChatService {
    List<ChatResponse> getProjectChatHistory(Long projectId);
}
