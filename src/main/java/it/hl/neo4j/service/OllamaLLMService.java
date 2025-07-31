package it.hl.neo4j.service;

import it.hl.neo4j.dto.OllamaRequest;
import it.hl.neo4j.dto.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class OllamaLLMService {

    @Value("${ollama.model}")
    String ollamaModel;

    private final WebClient webClient;

    private static final String CYPHER_SYSTEM_PROMPT_IT = """
        Sei un generatore di query Cypher per Neo4j. Converti il linguaggio naturale italiano in query Cypher valide.
        
        Schema:
        - Nodi Soggetto: proprietà (identificativoSoggetto, nome, displayName, tipoSoggetto, cognome, nomePersonaFisica, codiceFiscale, denominazione, sede, partitaIva, identificativoFiscale)
        - Nodi Terreno: proprietà (identificativoImmobile, displayName, foglio, numero, qualita, classe, ettari, are, centiare, redditoDominicaleEuro, redditoAgrarioEuro)
        - Relazione OWNS: (Soggetto)-[OWNS]->(Terreno) con proprietà (quotaNumeratore, quotaDenominatore, codiceDiritto, regime, dataValidita)
        
        Importante:
        - Usa le etichette esatte dei nodi: "Soggetto" e "Terreno"
        - Per cercare persone, usa displayName
        - Per cercare aziende, usa denominazione o displayName
        - Per cercare terreni, usa foglio, numero, qualita
        - Restituisci solo il tipo di nodo principale ricercato
        
        Esempi:
        "Trova tutti i terreni di proprietà di Mario" -> MATCH (s:Soggetto)-[:OWNS]->(t:Terreno) WHERE toLower(s.nome) CONTAINS toLower('Mario') RETURN t
        "Mostrami i soggetti che possiedono terreni nel foglio 5" -> MATCH (s:Soggetto)-[:OWNS]->(t:Terreno) WHERE t.foglio = '5' RETURN s
        "Tutti i terreni con qualità SEMIN" -> MATCH (t:Terreno) WHERE toLower(t.qualita) CONTAINS toLower('SEMIN') RETURN t
        "Trova persona con codice fiscale ABCD123" -> MATCH (s:Soggetto) WHERE s.codiceFiscale = 'ABCD123' RETURN s
        "Aziende a Milano" -> MATCH (s:Soggetto) WHERE s.tipoSoggetto = 'G' AND toLower(s.sede) CONTAINS toLower('Milano') RETURN s
        
        Converti questa richiesta (restituisci solo Cypher, nessuna spiegazione):
        """;

    public OllamaLLMService(WebClient ollamaWebClient) {
        this.webClient = ollamaWebClient;
    }

    public String translateToCypher(String naturalQuery) {
        String fullPrompt = CYPHER_SYSTEM_PROMPT_IT + naturalQuery;

        OllamaRequest request = new OllamaRequest(ollamaModel, fullPrompt);

        try {
            OllamaResponse response = webClient.post()
                    .uri("/api/generate")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OllamaResponse.class)
//                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response != null && response.getResponse() != null) {
                return cleanCypherQuery(response.getResponse());
            }

            throw new RuntimeException("Risposta vuota da Ollama");

        } catch (Exception e) {
            log.error("Errore nella chiamata a Ollama: {}", e.getMessage());
            throw new RuntimeException("Impossibile generare la query Cypher", e);
        }
    }

    private String cleanCypherQuery(String rawQuery) {
        return rawQuery.trim()
                .replaceAll("```cypher", "")
                .replaceAll("```", "")
                .replaceAll("(?i)^(cypher:|query:)", "")
                .trim();
    }
}