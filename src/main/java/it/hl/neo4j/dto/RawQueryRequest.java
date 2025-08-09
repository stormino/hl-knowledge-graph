package it.hl.neo4j.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RawQueryRequest {

    String cypherQuery;
    Map<String, Object> parameters;
}
