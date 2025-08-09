package it.hl.neo4j.service;

public interface LLMService {
    String translateToCypher(String naturalQuery);
    boolean isAvailable();
}
