package it.hl.neo4j.controller;

import it.hl.neo4j.model.Sog;
import it.hl.neo4j.model.Ter;
import it.hl.neo4j.service.CatastoImportService;
import it.hl.neo4j.service.CatastoQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catasto")
@RequiredArgsConstructor
@Slf4j
public class CatastoController {

    private static final String DATA_PREFIX = "data";

    private final CatastoImportService importService;
    private final CatastoQueryService queryService;

    @PostMapping("/import")
    public ResponseEntity<String> importData(@RequestParam String dataDirectory) {
        try {
            importService.importCatastoData(DATA_PREFIX + "/" + dataDirectory);
            return ResponseEntity.ok("Import completed successfully");
        } catch (Exception e) {
            log.error("Import failed", e);
            return ResponseEntity.badRequest()
                    .body("Import failed: " + e.getMessage());
        }
    }

    @GetMapping("/soggetti/search")
    public ResponseEntity<List<Sog>> searchSoggetti(@RequestParam String nome) {
        List<Sog> soggetti = queryService.findSoggettiByNome(nome);
        return ResponseEntity.ok(soggetti);
    }

    @GetMapping("/soggetti/cf/{codiceFiscale}")
    public ResponseEntity<Sog> findSoggettoByCodiceFiscale(@PathVariable String codiceFiscale) {
        Optional<Sog> soggetto = queryService.findSoggettoByCodiceFiscale(codiceFiscale);
        return soggetto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/terreni/foglio/{foglio}")
    public ResponseEntity<List<Ter>> findTerreniByFoglio(@PathVariable String foglio) {
        List<Ter> terreni = queryService.findTerreniByFoglio(foglio);
        return ResponseEntity.ok(terreni);
    }

    @GetMapping("/terreni/qualita/{qualita}")
    public ResponseEntity<List<Ter>> findTerreniByQualita(@PathVariable String qualita) {
        List<Ter> terreni = queryService.findTerreniByQualita(qualita);
        return ResponseEntity.ok(terreni);
    }
}