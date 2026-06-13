package com.subham.projects.lovableClone.service;

import reactor.core.publisher.Flux;

public interface AiService {
    Flux<String> streamResponse(String message, Long projectId);
}
