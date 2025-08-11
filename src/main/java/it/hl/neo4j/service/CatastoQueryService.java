package it.hl.neo4j.service;

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
}