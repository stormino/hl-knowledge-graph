package it.hl.neo4j.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
@Builder
public class GraphQueryResponse {
    private Collection<Map<String, Object>> results;
    private String cypherQuery;
    private boolean success;
    private String error;
    private int resultCount;
    private String domainClass;
    private String message; // Added for Italian responses

    public static GraphQueryResponse success(Collection<Map<String, Object>> results, String cypher, int count) {
        return GraphQueryResponse.builder()
                .results(results)
                .cypherQuery(cypher)
                .success(true)
                .resultCount(count)
                .message(count > 0 ?
                        String.format("Trovati %d risultati", count) :
                        "Nessun risultato trovato")
                .build();
    }

    public static GraphQueryResponse error(String errorMessage) {
        return GraphQueryResponse.builder()
                .success(false)
                .error(errorMessage)
                .message("Errore nell'elaborazione della richiesta")
                .build();
    }
}