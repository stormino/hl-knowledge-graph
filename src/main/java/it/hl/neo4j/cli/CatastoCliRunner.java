package it.hl.neo4j.cli;

import it.hl.neo4j.service.CatastoImportService;
import it.hl.neo4j.service.CatastoQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatastoCliRunner implements ApplicationRunner {

    private final CatastoImportService importService;
    private final CatastoQueryService queryService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.getNonOptionArgs().isEmpty()) {
            printHelp();
            return;
        }

        String command = args.getNonOptionArgs().get(0).toLowerCase();

        switch (command) {
            case "help", "-h", "--help" -> printHelp();
            case "import" -> handleImport(args);
            case "titolari" -> handleTitolari(args);
            case "stats" -> handleStats();
            default -> {
                System.out.println("Comando sconosciuto: " + command);
                printHelp();
            }
        }
    }

    private void handleImport(ApplicationArguments args) {
        if (args.getNonOptionArgs().size() < 2) {
            System.out.println("Uso: import <directory>");
            return;
        }

        String directory = args.getNonOptionArgs().get(1);

        try {
            System.out.println("Importando dati da: " + directory);
            importService.importCatastoData(directory);

            CatastoImportService.ImportStats stats = importService.getImportStats();
            System.out.printf("Import completato:%n");
            System.out.printf("- Terreni: %d%n", stats.terreniCount());
            System.out.printf("- Soggetti: %d%n", stats.soggettiCount());
            System.out.printf("- Relazioni: %d%n", stats.relationshipsCount());

        } catch (Exception e) {
            System.err.println("Errore durante l'import: " + e.getMessage());
            log.error("Import error", e);
        }
    }

    private void handleTitolari(ApplicationArguments args) {
        if (args.getNonOptionArgs().size() < 2) {
            System.out.println("Uso: titolari <codice_fiscale>");
            return;
        }

        String codiceFiscale = args.getNonOptionArgs().get(1);

        queryService.findOwnershipsByCodiceFiscale(codiceFiscale)
                .ifPresentOrElse(
                        this::printOwnershipReport,
                        () -> System.out.println("Nessun risultato trovato per: " + codiceFiscale)
                );
    }

    private void handleStats() {
        CatastoImportService.ImportStats stats = importService.getImportStats();
        System.out.printf("Statistiche database:%n");
        System.out.printf("- Terreni: %d%n", stats.terreniCount());
        System.out.printf("- Soggetti: %d%n", stats.soggettiCount());
        System.out.printf("- Relazioni di proprietà: %d%n", stats.relationshipsCount());
    }

    private void printOwnershipReport(CatastoQueryService.OwnershipReport report) {
        var soggetto = report.soggetto();

        System.out.printf("=== REPORT PROPRIETÀ ===%n");
        System.out.printf("Soggetto: %s%n", soggetto.getNome());
        System.out.printf("Tipo: %s%n", "P".equals(soggetto.getTipoSoggetto()) ? "Persona Fisica" : "Persona Giuridica");
        System.out.printf("Codice Fiscale/P.IVA: %s%n", soggetto.getIdentificativoFiscale());
        System.out.printf("Numero proprietà: %d%n%n", report.totalOwnerships());

        if (report.ownerships() != null) {
            int i = 1;
            for (var ownership : report.ownerships()) {
                System.out.printf("--- Proprietà %d ---%n", i++);
                var terreno = ownership.getTerreno();
                if (terreno != null) {
                    System.out.printf("Foglio: %s, Particella: %s%n",
                            terreno.getFoglio(), terreno.getNumero());
                    System.out.printf("Qualità: %s, Classe: %s%n",
                            terreno.getQualita(), terreno.getClasse());
                    System.out.printf("Superficie: %s ha %s are %s ca%n",
                            terreno.getEttari(), terreno.getAre(), terreno.getCentiare());
                }
                System.out.printf("Quota: %s/%s%n",
                        ownership.getQuotaNumeratore(), ownership.getQuotaDenominatore());
                System.out.printf("Diritto: %s%n", ownership.getCodiceDiritto());
                System.out.println();
            }
        }
    }

    private void printHelp() {
        System.out.println("=== HL Neo4j ===");
        System.out.println("Comandi disponibili:");
        System.out.println("  import <directory>     - Importa i dati catastali da directory");
        System.out.println("  titolari <cf>         - Cerca proprietà per codice fiscale");
        System.out.println("  stats                 - Mostra statistiche database");
        System.out.println("  help                  - Mostra questo messaggio");
        System.out.println();
        System.out.println("Esempi:");
        System.out.println("  java -jar app.jar import /path/to/data");
        System.out.println("  java -jar app.jar titolari RSSMRA80A01H501Z");
        System.out.println();
        System.out.println("API REST disponibile su: http://localhost:8080/api/catasto");
    }
}
