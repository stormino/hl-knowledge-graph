package it.hl.neo4j.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroqResponse {
    private List<GroqChoice> choices;
}
