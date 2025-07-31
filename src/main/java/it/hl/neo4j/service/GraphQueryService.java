package it.hl.neo4j.service;

import it.hl.neo4j.dto.GraphQueryResponse;
import it.hl.neo4j.model.Sog;
import it.hl.neo4j.model.Ter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GraphQueryService {

    private final Neo4jTemplate neo4jTemplate;
    private final OllamaLLMService llmService;

    public GraphQueryService(Neo4jTemplate neo4jTemplate, OllamaLLMService llmService) {
        this.neo4jTemplate = neo4jTemplate;
        this.llmService = llmService;
    }

    public GraphQueryResponse processNaturalLanguageQuery(String userQuery) {
        try {
            // Generate Cypher
            String cypherQuery = llmService.translateToCypher(userQuery);
            log.info("Generated Cypher: {}", cypherQuery);

            // Validate basic Cypher syntax
            validateCypher(cypherQuery);

            // Determine which domain class to use based on query content
            Class<?> domainClass = determineDomainClass(userQuery, cypherQuery);

            // Execute query using find method
            Collection<?> results = neo4jTemplate.find(domainClass)
                    .matching(cypherQuery)
                    .all();

            return GraphQueryResponse.builder()
                    .results(convertResultsToMaps(results))
                    .cypherQuery(cypherQuery)
                    .success(true)
                    .resultCount(results.size())
                    .domainClass(domainClass.getSimpleName())
                    .build();

        } catch (Exception e) {
            log.error("Error processing query: {}", e.getMessage());
            return GraphQueryResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }

    // Specific methods for each domain class
    public List<Sog> findSoggetti(String naturalQuery) {
        try {
            String cypherQuery = llmService.translateToCypher(naturalQuery);
            validateCypher(cypherQuery);

            return neo4jTemplate.find(Sog.class)
                    .matching(cypherQuery)
                    .all()
                    .stream()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error finding Soggetti: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Ter> findTerreni(String naturalQuery) {
        try {
            String cypherQuery = llmService.translateToCypher(naturalQuery);
            validateCypher(cypherQuery);

            return neo4jTemplate.find(Ter.class)
                    .matching(cypherQuery)
                    .all()
                    .stream()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error finding Terreni: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private Class<?> determineDomainClass(String userQuery, String cypherQuery) {
        String lowerQuery = userQuery.toLowerCase();
        String lowerCypher = cypherQuery.toLowerCase();

        String returnClause = extractReturnClause(lowerCypher);
        if (returnClause != null) {
            if (returnClause.matches(".*\\bt\\b.*") && lowerCypher.contains("t:terreno")) {
                return Ter.class;
            }
            if (returnClause.matches(".*\\bs\\b.*") && lowerCypher.contains("s:soggetto")) {
                return Sog.class;
            }
            // Check for explicit node references in RETURN
            if (returnClause.contains("terreno")) {
                return Ter.class;
            }
            if (returnClause.contains("soggetto")) {
                return Sog.class;
            }
        }

        // Method 2: Analyze query intent by examining WHERE clauses and properties
        Class<?> intentClass = analyzeQueryIntent(lowerCypher);
        if (intentClass != null) {
            return intentClass;
        }

        // Method 3: Check primary node in MATCH pattern
        Class<?> primaryNode = analyzePrimaryMatchNode(lowerCypher);
        if (primaryNode != null) {
            return primaryNode;
        }

        // Method 4: Fallback to original keyword detection with broader scope
        return analyzeKeywords(userQuery.toLowerCase(), lowerCypher);
    }

    private String extractReturnClause(String cypherQuery) {
        int returnIndex = cypherQuery.lastIndexOf("return");
        if (returnIndex == -1) return null;

        String returnPart = cypherQuery.substring(returnIndex + 6).trim();
        // Remove any ORDER BY, LIMIT, etc.
        String[] stopWords = {"order by", "limit", "skip", "union"};
        for (String stop : stopWords) {
            int stopIndex = returnPart.indexOf(stop);
            if (stopIndex != -1) {
                returnPart = returnPart.substring(0, stopIndex).trim();
            }
        }
        return returnPart;
    }

    private Class<?> analyzeQueryIntent(String cypherQuery) {
        // Property-based analysis
        Map<String, Integer> terreniProperties = Map.of(
                "foglio", 3, "numero", 3, "qualita", 3, "classe", 2,
                "ettari", 2, "are", 2, "centiare", 2, "reddito", 2,
                "identificativoimmobile", 3
        );

        Map<String, Integer> soggettiProperties = Map.of(
                "nome", 3, "cognome", 3, "denominazione", 3, "codicefiscale", 3,
                "partitaiva", 3, "sede", 2, "identificativosoggetto", 3,
                "tiposoggetto", 2
        );

        int terreniScore = 0;
        int soggettiScore = 0;

        // Score based on property usage in WHERE clauses
        for (Map.Entry<String, Integer> entry : terreniProperties.entrySet()) {
            if (cypherQuery.contains(entry.getKey())) {
                terreniScore += entry.getValue();
            }
        }

        for (Map.Entry<String, Integer> entry : soggettiProperties.entrySet()) {
            if (cypherQuery.contains(entry.getKey())) {
                soggettiScore += entry.getValue();
            }
        }

        // Strong preference if score difference is significant
        if (terreniScore > soggettiScore + 2) {
            return Ter.class;
        } else if (soggettiScore > terreniScore + 2) {
            return Sog.class;
        }

        return null; // Inconclusive
    }

    private Class<?> analyzePrimaryMatchNode(String cypherQuery) {
        // Extract first MATCH pattern to find primary node
        Pattern matchPattern = Pattern.compile("match\\s*\\(([^)]+)\\)");
        Matcher matcher = matchPattern.matcher(cypherQuery);

        if (matcher.find()) {
            String firstNode = matcher.group(1).toLowerCase();

            // Check node label in first match
            if (firstNode.contains(":terreno")) {
                return Ter.class;
            } else if (firstNode.contains(":soggetto")) {
                return Sog.class;
            }

            // Check variable names that suggest node type
            if (firstNode.matches(".*\\bt\\b.*") && cypherQuery.contains("t:terreno")) {
                return Ter.class;
            } else if (firstNode.matches(".*\\bs\\b.*") && cypherQuery.contains("s:soggetto")) {
                return Sog.class;
            }
        }

        return null;
    }

    private Class<?> analyzeKeywords(String userQuery, String cypherQuery) {
        // Enhanced keyword analysis with context
        Set<String> terreniKeywords = Set.of(
                "terreni", "terreno", "foglio", "particella", "qualità", "qualita",
                "ettari", "reddito", "catastale", "catasto", "seminativo", "pascolo"
        );

        Set<String> soggettiKeywords = Set.of(
                "soggetti", "soggetto", "proprietario", "proprietari", "persona",
                "azienda", "ditta", "fiscale", "partita", "cognome", "nome",
                "denominazione", "società", "societa"
        );

        long terreniMatches = terreniKeywords.stream()
                .mapToLong(keyword -> countOccurrences(userQuery + " " + cypherQuery, keyword))
                .sum();

        long soggettiMatches = soggettiKeywords.stream()
                .mapToLong(keyword -> countOccurrences(userQuery + " " + cypherQuery, keyword))
                .sum();

        if (terreniMatches > soggettiMatches) {
            return Ter.class;
        } else if (soggettiMatches > terreniMatches) {
            return Sog.class;
        }

        // Default to Sog for ambiguous cases
        return Sog.class;
    }

    private long countOccurrences(String text, String keyword) {
        return text.split("\\b" + keyword + "\\b", -1).length - 1;
    }

    private Collection<Map<String, Object>> convertResultsToMaps(Collection<?> results) {
        return results.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertToMap(Object entity) {
        Map<String, Object> map = new HashMap<>();

        if (entity instanceof Sog sog) {
            map.put("type", "Soggetto");
            map.put("identificativoSoggetto", sog.getIdentificativoSoggetto());
            map.put("nome", sog.getNome());
            map.put("displayName", sog.getDisplayName());
            map.put("tipoSoggetto", sog.getTipoSoggetto());
            map.put("identificativoFiscale", sog.getIdentificativoFiscale());
            if ("P".equals(sog.getTipoSoggetto())) {
                map.put("cognome", sog.getCognome());
                map.put("nomePersonaFisica", sog.getNomePersonaFisica());
                map.put("codiceFiscale", sog.getCodiceFiscale());
            } else if ("G".equals(sog.getTipoSoggetto())) {
                map.put("denominazione", sog.getDenominazione());
                map.put("sede", sog.getSede());
                map.put("partitaIva", sog.getPartitaIva());
            }
        } else if (entity instanceof Ter ter) {
            map.put("type", "Terreno");
            map.put("identificativoImmobile", ter.getIdentificativoImmobile());
            map.put("displayName", ter.getDisplayName());
            map.put("foglio", ter.getFoglio());
            map.put("numero", ter.getNumero());
            map.put("qualita", ter.getQualita());
            map.put("classe", ter.getClasse());
            map.put("ettari", ter.getEttari());
            map.put("are", ter.getAre());
            map.put("centiare", ter.getCentiare());
            map.put("redditoDominicaleEuro", ter.getRedditoDominicaleEuro());
            map.put("redditoAgrarioEuro", ter.getRedditoAgrarioEuro());
        }

        return map;
    }

    private void validateCypher(String cypher) {
        if (!cypher.toUpperCase().contains("MATCH") && !cypher.toUpperCase().contains("CREATE")) {
            throw new IllegalArgumentException("Invalid Cypher query: must contain MATCH or CREATE");
        }

        String upperCypher = cypher.toUpperCase();
        if (upperCypher.contains("DELETE") || upperCypher.contains("DETACH")) {
            throw new SecurityException("DELETE operations not allowed");
        }
    }
}