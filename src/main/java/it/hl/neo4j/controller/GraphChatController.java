package it.hl.neo4j.controller;

import it.hl.neo4j.dto.ChatRequest;
import it.hl.neo4j.dto.GraphQueryResponse;
import it.hl.neo4j.service.GraphQueryService;
import it.hl.neo4j.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
@Slf4j
public class GraphChatController {

    private final GraphQueryService queryService;
    private final LLMService llmService;

    public GraphChatController(GraphQueryService queryService, LLMService llmService) {
        this.queryService = queryService;
        this.llmService = llmService;
    }

    @PostMapping("/chat")
    public ResponseEntity<GraphQueryResponse> chat(@RequestBody ChatRequest request) {
        GraphQueryResponse response = queryService.processNaturalLanguageQuery(
                request.getMessage().trim()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "llm_provider", llmService.getClass().getSimpleName(),
                "llm_available", llmService.isAvailable(),
                "timestamp", Instant.now()
        ));
    }
}