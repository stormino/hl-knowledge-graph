package it.hl.neo4j.service;

import it.hl.neo4j.model.OwnershipRelationship;
import it.hl.neo4j.model.Sog;
import it.hl.neo4j.model.Ter;
import it.hl.neo4j.model.Tit;
import it.hl.neo4j.repository.SogRepository;
import it.hl.neo4j.repository.TerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatastoImportService {

    private final TerRepository terRepository;
    private final SogRepository sogRepository;

    @Transactional
    public void importCatastoData(String dataDirectory) {
        log.info("Starting import from directory: {}", dataDirectory);

        try {
            // Import terreni first
            importTerreni(dataDirectory);

            // Import soggetti
            importSoggetti(dataDirectory);

            // Import relationships (titolarità)
            importTitolarita(dataDirectory);

            log.info("Import completed successfully");

        } catch (Exception e) {
            log.error("Error during import", e);
            throw new RuntimeException("Import failed", e);
        }
    }

    private void importTerreni(String dataDirectory) throws IOException {
        Path terFile = findFileWithExtension(dataDirectory, ".ter");
        if (terFile == null) {
            log.warn("No .ter file found in directory: {}", dataDirectory);
            return;
        }

        log.info("Importing terreni from: {}", terFile);

        try (Stream<String> lines = Files.lines(terFile)) {
            List<Ter> terreni = lines
                    .map(Ter::parse)
                    .filter(Objects::nonNull)
                    .filter(ter -> "1".equals(ter.getTipoRecord())) // Only type 1 records
                    .toList();

            terRepository.saveAll(terreni);
            log.info("Imported {} terreni", terreni.size());
        }
    }

    private void importSoggetti(String dataDirectory) throws IOException {
        Path sogFile = findFileWithExtension(dataDirectory, ".sog");
        if (sogFile == null) {
            log.warn("No .sog file found in directory: {}", dataDirectory);
            return;
        }

        log.info("Importing soggetti from: {}", sogFile);

        try (Stream<String> lines = Files.lines(sogFile)) {
            List<Sog> soggetti = lines
                    .map(line -> {
                        try {
                            return Sog.parse(line);
                        } catch (Exception e) {
                            log.warn("Failed to parse sog line: {}", line, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            sogRepository.saveAll(soggetti);
            log.info("Imported {} soggetti", soggetti.size());
        }
    }

    private void importTitolarita(String dataDirectory) throws IOException {
        Path titFile = findFileWithExtension(dataDirectory, ".tit");
        if (titFile == null) {
            log.warn("No .tit file found in directory: {}", dataDirectory);
            return;
        }

        log.info("Importing titolarità from: {}", titFile);

        // First, load all existing entities
        Map<String, Ter> terreniMap = new HashMap<>();
        terRepository.findAll().forEach(ter -> terreniMap.put(ter.getIdentificativoImmobile(), ter));

        Map<String, Sog> soggettiMap = new HashMap<>();
        sogRepository.findAll().forEach(sog -> soggettiMap.put(sog.getIdentificativoSoggetto(), sog));

        try (Stream<String> lines = Files.lines(titFile)) {
            lines.forEach(line -> {
                try {
                    processTitolaritaLine(line, terreniMap, soggettiMap);
                } catch (Exception e) {
                    log.warn("Failed to process titolarità line: {}", line, e);
                }
            });
        }

        log.info("Titolarità import completed");
    }

    private void processTitolaritaLine(String line, Map<String, Ter> terreniMap, Map<String, Sog> soggettiMap) {
        Tit tit = Tit.parse(line);
        String identificativoSoggetto = Tit.extractIdentificativoSoggetto(line);
        String identificativoImmobile = Tit.extractIdentificativoImmobile(line);

        Sog soggetto = soggettiMap.get(identificativoSoggetto);
        Ter terreno = terreniMap.get(identificativoImmobile);

        if (soggetto == null) {
            log.warn("Soggetto not found: {}", identificativoSoggetto);
            return;
        }

        if (terreno == null) {
            log.warn("Terreno not found: {}", identificativoImmobile);
            return;
        }

        // Create relationship
        Sog sogWithRel = sogRepository.findById(soggetto.getIdentificativoSoggetto())
                .orElse(soggetto);

        OwnershipRelationship ownership = OwnershipRelationship.fromTit(tit);
        ownership.setTerreno(terreno);

        if (sogWithRel.getOwnerships() == null) {
            sogWithRel.setOwnerships(new HashSet<>());
        }
        sogWithRel.getOwnerships().add(ownership);

        sogRepository.save(sogWithRel);
    }

    private Path findFileWithExtension(String directory, String extension) throws IOException {
        Path dirPath = Paths.get(directory);

        try (Stream<Path> files = Files.list(dirPath)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(extension.toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Transactional(readOnly = true)
    public ImportStats getImportStats() {
        long terreniCount = terRepository.count();
        long soggettiCount = sogRepository.count();

        return new ImportStats(terreniCount, soggettiCount, 0L);
    }

    public record ImportStats(long terreniCount, long soggettiCount, long relationshipsCount) {}
}