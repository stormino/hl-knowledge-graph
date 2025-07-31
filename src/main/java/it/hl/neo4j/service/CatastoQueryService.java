package it.hl.neo4j.service;

import it.hl.neo4j.model.OwnershipRelationship;
import it.hl.neo4j.model.Sog;
import it.hl.neo4j.model.Ter;
import it.hl.neo4j.repository.SogRepository;
import it.hl.neo4j.repository.TerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CatastoQueryService {

    private final TerRepository terRepository;
    private final SogRepository sogRepository;

    public Optional<OwnershipReport> findOwnershipsByCodiceFiscale(String codiceFiscale) {
        log.info("Searching ownerships for codice fiscale: {}", codiceFiscale);

        return sogRepository.findByCodiceFiscaleWithOwnerships(codiceFiscale)
                .map(this::createOwnershipReport);
    }

    public Optional<OwnershipReport> findOwnershipsByIdentificativoFiscale(String identificativoFiscale) {
        log.info("Searching ownerships for identificativo fiscale: {}", identificativoFiscale);

        return sogRepository.findByIdentificativoFiscaleWithOwnerships(identificativoFiscale)
                .map(this::createOwnershipReport);
    }

    public List<CoOwnershipReport> findCoOwnersByImmobileId(String immobileId) {
        log.info("Searching co-owners for immobile: {}", immobileId);

        return sogRepository.findOwnersByImmobileId(immobileId)
                .stream()
                .map(this::createCoOwnershipReport)
                .toList();
    }

    public List<CoOwnershipReport> findOwnersByFoglio(String foglio) {
        log.info("Searching owners by foglio: {}", foglio);

        return sogRepository.findOwnersByFoglio(foglio)
                .stream()
                .map(this::createCoOwnershipReport)
                .toList();
    }

    public Optional<Sog> findSoggettoByCodiceFiscale(String codiceFiscale) {
        return sogRepository.findByCodiceFiscale(codiceFiscale);
    }

    public List<Sog> findSoggettiByNome(String nome) {
        return sogRepository.findByNomeContaining(nome);
    }

    public List<Ter> findTerreniByFoglio(String foglio) {
        return terRepository.findByFoglio(foglio);
    }

    public List<Ter> findTerreniByQualita(String qualita) {
        return terRepository.findByQualita(qualita);
    }

    private OwnershipReport createOwnershipReport(Sog sog) {
        return new OwnershipReport(
                sog,
                sog.getOwnerships() != null ? sog.getOwnerships().size() : 0,
                sog.getOwnerships()
        );
    }

    private CoOwnershipReport createCoOwnershipReport(Sog sog) {
        return new CoOwnershipReport(
                sog.getIdentificativoSoggetto(),
                sog.getNome(),
                sog.getTipoSoggetto(),
                sog.getIdentificativoFiscale(),
                sog.getOwnerships()
        );
    }

    public record OwnershipReport(
            Sog soggetto,
            int totalOwnerships,
            java.util.Set<OwnershipRelationship> ownerships
    ) {}

    public record CoOwnershipReport(
            String identificativoSoggetto,
            String nome,
            String tipoSoggetto,
            String identificativoFiscale,
            java.util.Set<OwnershipRelationship> ownerships
    ) {}
}