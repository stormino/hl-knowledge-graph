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

    @GetMapping("/import/stats")
    public ResponseEntity<CatastoImportService.ImportStats> getImportStats() {
        return ResponseEntity.ok(importService.getImportStats());
    }

    @GetMapping("/titolari/cf/{codiceFiscale}")
    public ResponseEntity<CatastoQueryService.OwnershipReport> findByCodiceFiscale(
            @PathVariable String codiceFiscale) {

        Optional<CatastoQueryService.OwnershipReport> result =
                queryService.findOwnershipsByCodiceFiscale(codiceFiscale);

        return result.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/titolari/piva/{partitaIva}")
    public ResponseEntity<CatastoQueryService.OwnershipReport> findByPartitaIva(
            @PathVariable String partitaIva) {

        Optional<CatastoQueryService.OwnershipReport> result =
                queryService.findOwnershipsByIdentificativoFiscale(partitaIva);

        return result.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/immobile/{immobileId}/comproprietari")
    public ResponseEntity<List<CatastoQueryService.CoOwnershipReport>> findCoOwners(
            @PathVariable String immobileId) {

        List<CatastoQueryService.CoOwnershipReport> coOwners =
                queryService.findCoOwnersByImmobileId(immobileId);

        return ResponseEntity.ok(coOwners);
    }

    @GetMapping("/foglio/{foglio}/proprietari")
    public ResponseEntity<List<CatastoQueryService.CoOwnershipReport>> findOwnersByFoglio(
            @PathVariable String foglio) {

        List<CatastoQueryService.CoOwnershipReport> owners =
                queryService.findOwnersByFoglio(foglio);

        return ResponseEntity.ok(owners);
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