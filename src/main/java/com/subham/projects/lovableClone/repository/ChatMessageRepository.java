package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.ChatMessage;
import com.subham.projects.lovableClone.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            SELECT DISTINCT m FROM ChatMessage m
            LEFT JOIN FETCH m.events e
            WHERE m.chatSession =:chatSession
            ORDER BY m.createdAt ASC
            """)
    List<ChatMessage> findByChatSession(@Param("chatSession") ChatSession chatSession);
}