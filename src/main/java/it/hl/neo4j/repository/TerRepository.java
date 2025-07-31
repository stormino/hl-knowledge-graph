package it.hl.neo4j.repository;

import it.hl.neo4j.model.Ter;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TerRepository extends Neo4jRepository<Ter, String> {

    @Query("MATCH (t:Terreno) WHERE t.foglio = $foglio RETURN t")
    List<Ter> findByFoglio(@Param("foglio") String foglio);

    @Query("MATCH (t:Terreno) WHERE t.qualita = $qualita RETURN t")
    List<Ter> findByQualita(@Param("qualita") String qualita);

    @Query("MATCH (t:Terreno) WHERE t.codiceAmministrativo = $codice RETURN t")
    List<Ter> findByCodiceAmministrativo(@Param("codice") String codice);
}
