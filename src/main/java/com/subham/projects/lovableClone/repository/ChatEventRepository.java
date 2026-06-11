package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}