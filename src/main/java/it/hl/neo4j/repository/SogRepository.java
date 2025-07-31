package it.hl.neo4j.repository;

import it.hl.neo4j.model.Sog;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SogRepository extends Neo4jRepository<Sog, String> {

    @Query("MATCH (s:Soggetto) WHERE s.codiceFiscale = $cf RETURN s")
    Optional<Sog> findByCodiceFiscale(@Param("cf") String codiceFiscale);

    @Query("MATCH (s:Soggetto) WHERE s.partitaIva = $piva RETURN s")
    Optional<Sog> findByPartitaIva(@Param("piva") String partitaIva);

    @Query("MATCH (s:Soggetto) WHERE s.identificativoFiscale = $id RETURN s")
    Optional<Sog> findByIdentificativoFiscale(@Param("id") String identificativoFiscale);

    @Query("MATCH (s:Soggetto) WHERE s.tipoSoggetto = $tipo RETURN s")
    List<Sog> findByTipoSoggetto(@Param("tipo") String tipoSoggetto);

    @Query("MATCH (s:Soggetto) WHERE s.nome CONTAINS $nome RETURN s")
    List<Sog> findByNomeContaining(@Param("nome") String nome);

    @Query("MATCH (s:Soggetto)-[r:OWNS]->(t:Terreno) WHERE s.codiceFiscale = $cf RETURN s, collect(r), collect(t)")
    Optional<Sog> findByCodiceFiscaleWithOwnerships(@Param("cf") String codiceFiscale);

    @Query("MATCH (s:Soggetto)-[r:OWNS]->(t:Terreno) WHERE s.identificativoFiscale = $id RETURN s, collect(r), collect(t)")
    Optional<Sog> findByIdentificativoFiscaleWithOwnerships(@Param("id") String identificativoFiscale);

    @Query("MATCH (s:Soggetto)-[r:OWNS]->(t:Terreno) WHERE t.identificativoImmobile = $immobileId RETURN s, collect(r), collect(t)")
    List<Sog> findOwnersByImmobileId(@Param("immobileId") String immobileId);

    @Query("MATCH (s:Soggetto)-[r:OWNS]->(t:Terreno) WHERE t.foglio = $foglio RETURN s, collect(r), collect(t)")
    List<Sog> findOwnersByFoglio(@Param("foglio") String foglio);
}