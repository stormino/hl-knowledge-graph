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
