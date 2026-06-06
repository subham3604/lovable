package com.subham.projects.lovableClone.service;

import aj.org.objectweb.asm.commons.Remapper;
import reactor.core.publisher.Flux;

public interface AiService {
    Flux<String> streamResponse(String message, Long projectId);
}
