package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.ChatSession;
import com.subham.projects.lovableClone.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}