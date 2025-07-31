package it.hl.neo4j.controller;

import it.hl.neo4j.dto.ChatRequest;
import it.hl.neo4j.dto.GraphQueryResponse;
import it.hl.neo4j.model.Sog;
import it.hl.neo4j.model.Ter;
import it.hl.neo4j.service.GraphQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graph")
@Slf4j
public class GraphChatController {

    private final GraphQueryService queryService;

    public GraphChatController(GraphQueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/chat")
    public ResponseEntity<GraphQueryResponse> chat(@RequestBody ChatRequest request) {
        GraphQueryResponse response = queryService.processNaturalLanguageQuery(
                request.getMessage().trim()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat/soggetti")
    public ResponseEntity<List<Sog>> chatSoggetti(@RequestBody ChatRequest request) {
        List<Sog> results = queryService.findSoggetti(request.getMessage());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/chat/terreni")
    public ResponseEntity<List<Ter>> chatTerreni(@RequestBody ChatRequest request) {
        List<Ter> results = queryService.findTerreni(request.getMessage());
        return ResponseEntity.ok(results);
    }
}