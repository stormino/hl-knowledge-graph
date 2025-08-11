package it.hl.neo4j.service;

import it.hl.neo4j.model.*;
import it.hl.neo4j.repository.FabRepository;
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
    private final FabRepository fabRepository;

    @Transactional
    public void importCatastoData(String dataDirectory) {
        log.info("Starting import from directory: {}", dataDirectory);

        try {
            // Import terreni first
            importTerreni(dataDirectory);

            // Import fabbricati
            importFabbricati(dataDirectory);

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
        Path fabFile = findFileWithExtension(dataDirectory, ".fab");
        Path terFile = findFileWithExtension(dataDirectory, ".ter");

        if (titFile == null) {
            log.warn("No .tit file found in directory: {}", dataDirectory);
            return;
        }

        log.info("Importing titolarità from: {}", titFile);

        // First, load all existing entities
        Map<String, Ter> terreniMap = new HashMap<>();
        terRepository.findAll().forEach(ter -> terreniMap.put(ter.getIdentificativoImmobile(), ter));

        Map<String, Fab> fabbricatiMap = new HashMap<>();
        fabRepository.findAll().forEach(fab -> fabbricatiMap.put(fab.getIdentificativoImmobile(), fab));

        Map<String, Sog> soggettiMap = new HashMap<>();
        sogRepository.findAll().forEach(sog -> soggettiMap.put(sog.getIdentificativoSoggetto(), sog));

        try (Stream<String> lines = Files.lines(titFile)) {
            lines.forEach(line -> {
                try {
                    if (fabFile != null) {
                        processTitolaritaFabLine(line, fabbricatiMap, soggettiMap);
                    }
                    if (terFile != null) {
                        processTitolaritaTerLine(line, terreniMap, soggettiMap);
                    }
                } catch (Exception e) {
                    log.warn("Failed to process titolarità line: {}", line, e);
                }
            });
        }

        log.info("Titolarità import completed");
    }

    private void processTitolaritaTerLine(String line, Map<String, Ter> terreniMap, Map<String, Sog> soggettiMap) {
        TitTer tit = TitTer.parse(line);
        String identificativoSoggetto = TitTer.extractIdentificativoSoggetto(line);
        String identificativoImmobile = TitTer.extractIdentificativoImmobile(line);

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
        tit.setTerreno(terreno);

        if (sogWithRel.getTitTers() == null) {
            sogWithRel.setTitTers(new HashSet<>());
        }
        sogWithRel.getTitTers().add(tit);

        sogRepository.save(sogWithRel);
    }

    private void processTitolaritaFabLine(String line, Map<String, Fab> fabbricatiMap, Map<String, Sog> soggettiMap) {
        TitFab tit = TitFab.parse(line);
        String identificativoSoggetto = TitFab.extractIdentificativoSoggetto(line);
        String identificativoImmobile = TitFab.extractIdentificativoImmobile(line);

        Sog soggetto = soggettiMap.get(identificativoSoggetto);
        Fab fabbricato = fabbricatiMap.get(identificativoImmobile);

        if (soggetto == null) {
            log.warn("Soggetto not found: {}", identificativoSoggetto);
            return;
        }

        if (fabbricato == null) {
            log.warn("Fabbricato not found: {}", identificativoImmobile);
            return;
        }

        // Create relationship
        Sog sogWithRel = sogRepository.findById(soggetto.getIdentificativoSoggetto())
                .orElse(soggetto);
        tit.setFabbricato(fabbricato);

        if (sogWithRel.getTitFabs() == null) {
            sogWithRel.setTitFabs(new HashSet<>());
        }
        sogWithRel.getTitFabs().add(tit);

        sogRepository.save(sogWithRel);
    }

    private void importFabbricati(String dataDirectory) throws IOException {
        Path fabFile = findFileWithExtension(dataDirectory, ".fab");
        if (fabFile == null) {
            log.warn("No .fab file found in directory: {}", dataDirectory);
            return;
        }

        log.info("Importing fabbricati from: {}", fabFile);

        // Group records by composite key (first 5 fields)
        Map<String, List<String>> recordGroups = new HashMap<>();

        try (Stream<String> lines = Files.lines(fabFile)) {
            lines.forEach(line -> {
                String[] fields = line.split("\\|");
                if (fields.length >= 6) {
                    String compositeKey = String.join("|",
                            fields[0], fields[1], fields[2], fields[3], fields[4]);
                    recordGroups.computeIfAbsent(compositeKey, k -> new ArrayList<>()).add(line);
                }
            });
        }

        log.info("Found {} unique fabbricati composite keys", recordGroups.size());

        List<Fab> fabbricati = new ArrayList<>();
        int processedGroups = 0;

        for (Map.Entry<String, List<String>> entry : recordGroups.entrySet()) {
            Fab fab = processFabbricatoGroup(entry.getValue());
            if (fab != null) {
                fabbricati.add(fab);
                processedGroups++;

                // Batch save every 1000 records to avoid memory issues
                if (fabbricati.size() >= 1000) {
                    fabRepository.saveAll(fabbricati);
                    log.info("Saved batch of {} fabbricati. Total processed groups: {}",
                            fabbricati.size(), processedGroups);
                    fabbricati.clear();
                }
            }
        }

        // Save remaining records
        if (!fabbricati.isEmpty()) {
            fabRepository.saveAll(fabbricati);
        }

        log.info("Imported {} fabbricati from {} record groups", processedGroups, recordGroups.size());
    }

    /**
     * Process a group of records that belong to the same fabbricato
     */
    private Fab processFabbricatoGroup(List<String> records) {
        Fab fab = null;

        for (String record : records) {
            String[] fields = record.split("\\|");
            if (fields.length < 6) continue;

            String tipoRecord = fields[5]; // Position 5 is TIPO RECORD

            try {
                switch (tipoRecord) {
                    case "1" -> {
                        // Main characteristics - this creates the base object
                        fab = Fab.parseType1(record);
                    }
                    case "2" -> {
                        // Identificativi
                        if (fab != null) {
                            fab.addIdentificativi(record);
                        }
                    }
                    case "3" -> {
                        // Indirizzi
                        if (fab != null) {
                            fab.addIndirizzi(record);
                        }
                    }
                    case "4" -> {
                        // Utilità comuni
                        if (fab != null) {
                            fab.addUtilitaComuni(record);
                        }
                    }
                    case "5" -> {
                        // Riserve
                        if (fab != null) {
                            fab.addRiserve(record);
                        }
                    }
                    default -> log.debug("Unknown record type: {} for record: {}", tipoRecord, record);
                }
            } catch (Exception e) {
                log.warn("Error processing record type {} for record: {}. Error: {}",
                        tipoRecord, record, e.getMessage());
            }
        }

        if (fab != null) {
            fab.updateDisplayName();
        }

        return fab;
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
}