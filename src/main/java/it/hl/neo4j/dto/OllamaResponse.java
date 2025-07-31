package it.hl.neo4j.dto;

import lombok.Data;

@Data
public class OllamaResponse {
    private String model;
    private String response;
    private boolean done;
}
