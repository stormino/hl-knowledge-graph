package it.hl.neo4j.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroqRequest {
    private String model;
    private List<GroqMessage> messages;
    private double temperature;
    @JsonProperty("max_tokens")
    private int maxTokens;
}
