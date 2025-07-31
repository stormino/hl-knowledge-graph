package it.hl.neo4j.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
public class OllamaRequest {
    private String model;
    private String prompt;
    private boolean stream = false;
    private Map<String, Object> options;

    public OllamaRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
        this.options = Map.of(
                "temperature", 0.1,
                "top_p", 0.9,
                "num_predict", 200
        );
    }
}
