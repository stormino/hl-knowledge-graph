package it.hl.neo4j.service;

import it.hl.neo4j.dto.GroqMessage;
import it.hl.neo4j.dto.GroqRequest;
import it.hl.neo4j.dto.GroqResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@ConditionalOnProperty(name = "llm.provider", havingValue = "groq")
@Slf4j
public class GroqLLMService implements LLMService {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public GroqLLMService(@Value("${groq.api.key}") String apiKey,
                          @Value("${groq.model:llama3-8b-8192}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private static final String SYSTEM_MESSAGE = """
        Sei un generatore di query Cypher per Neo4j. Converti il linguaggio naturale italiano in query Cypher valide.
        
        Schema:
        - Nodi Soggetto: proprietà (identificativoSoggetto, nome, displayName, tipoSoggetto, cognome, nomePersonaFisica, codiceFiscale, denominazione, sede, partitaIva, identificativoFiscale)
        - Nodi Terreno: proprietà (identificativoImmobile, displayName, foglio, numero, qualita, classe, ettari, are, centiare, redditoDominicaleEuro, redditoAgrarioEuro)
        - Nodi Fabbricato: proprietà (identificativoImmobile, displayName, codiceAmministrativo, sezione, tipoImmobile, progressivo, zona, categoria, classe, consistenza, superficie, renditaLire, renditaEuro, lotto, edificio, scala, interno1, interno2, piano1, piano2, piano3, piano4, dataEfficaciaGenerante, dataRegistrazioneGenerante, tipoNotaGenerante, numeroNotaGenerante, progressivoNotaGenerante, annoNotaGenerante, dataEfficaciaConclusiva, dataRegistrazioneConclusiva, tipoNotaConclusiva, numeroNotaConclusiva, progressivoNotaConclusiva, annoNotaConclusiva, partita, annotazione, identificativoMutazioneIniziale, identificativoMutazioneFinale, protocolloNotifica, dataNotifica, codiceCausaleAttoGenerante, descrizioneAttoGenerante, codiceCausaleAttoConclusivo, descrizioneAttoConclusivo, flagClassamento, sezioniUrbane, fogli, numeri, denominatori, subalerni, edificialita, toponimi, indirizzi, civici1, civici2, civici3, codiciStrada, utilitaSezioniUrbane, utilitaFogli, utilitaNumeri, utilitaDenominatori, utilitaSubalerni, codiciRiserva, partiteIscrizioneRiserva)
        - Relazione POSSIEDE_TERRENO: (Soggetto)-[POSSIEDE_TERRENO]->(Terreno)
        - Relazione POSSIEDE_FABBRICATO: (Soggetto)-[POSSIEDE_FABBRICATO]->(Fabbricato)
        
        Importante:
        - Usa sempre le etichette esatte: "Soggetto", "Terreno", "Fabbricato"
        - Quando possibile, restituisci entità correlate: RETURN s, t, f
        - Restituisci solo la query Cypher, nessuna spiegazione
        
        Esempi:
        "Trova tutti i terreni di Mario" -> MATCH (s:Soggetto)-[:POSSIEDE_TERRENO]->(t:Terreno) WHERE toLower(s.nome) CONTAINS toLower('Mario') RETURN s, t
        "Trova tutti i soggetti che posseggono sia terreni che fabbricati" -> MATCH (s:Soggetti)-[:POSSIEDE_TERRENI]->(:Terreni) MATCH (s)-[:POSSIEDE_FABBRICATI]->(:Fabbricati) RETURN DISTINCT s
        """;

    @Override
    public String translateToCypher(String naturalQuery) {
        GroqRequest request = GroqRequest.builder()
                .model(model)
                .messages(List.of(
                        new GroqMessage("system", SYSTEM_MESSAGE),
                        new GroqMessage("user", naturalQuery)
                ))
                .temperature(0.1)
                .maxTokens(1000)
                .build();

        try {
            GroqResponse response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response != null && !response.getChoices().isEmpty()) {
                String cypherQuery = response.getChoices().get(0).getMessage().getContent();
                return cleanCypherQuery(cypherQuery);
            }

            throw new RuntimeException("Risposta vuota da Groq");

        } catch (Exception e) {
            log.error("Errore nella chiamata a Groq: {}", e.getMessage());
            throw new RuntimeException("Impossibile generare la query Cypher", e);
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            webClient.get()
                    .uri("/models")
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return true;
        } catch (Exception e) {
            log.warn("Groq non disponibile: {}", e.getMessage());
            return false;
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
