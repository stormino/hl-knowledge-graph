package it.hl.neo4j.repository;

import it.hl.neo4j.model.Fab;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabRepository extends Neo4jRepository<Fab, String> {

    /**
     * Find by categoria
     */
    List<Fab> findByCategoria(String categoria);

    /**
     * Find by zona
     */
    List<Fab> findByZona(String zona);

    /**
     * Find by codice amministrativo
     */
    List<Fab> findByCodiceAmministrativo(String codiceAmministrativo);

    /**
     * Find by foglio and numero (using the first entry in the lists)
     */
    @Query("MATCH (f:Fabbricato) WHERE $foglio IN f.fogli AND $numero IN f.numeri RETURN f")
    List<Fab> findByFoglioAndNumero(@Param("foglio") String foglio, @Param("numero") String numero);

    /**
     * Find by indirizzo (partial match)
     */
    @Query("MATCH (f:Fabbricato) WHERE ANY(ind IN f.indirizzi WHERE ind CONTAINS $indirizzo) RETURN f")
    List<Fab> findByIndirizzoContaining(@Param("indirizzo") String indirizzo);

    /**
     * Find by rendita range (in Euro)
     */
    @Query("MATCH (f:Fabbricato) WHERE toFloat(f.renditaEuro) >= $minRendita AND toFloat(f.renditaEuro) <= $maxRendita RETURN f")
    List<Fab> findByRenditaEuroRange(@Param("minRendita") Double minRendita, @Param("maxRendita") Double maxRendita);

    /**
     * Count by categoria
     */
    @Query("MATCH (f:Fabbricato) WHERE f.categoria = $categoria RETURN count(f)")
    Long countByCategoria(@Param("categoria") String categoria);

    /**
     * Get statistics by categoria
     */
    @Query("MATCH (f:Fabbricato) WHERE f.categoria IS NOT NULL " +
            "RETURN f.categoria as categoria, count(f) as count, " +
            "avg(toFloat(f.renditaEuro)) as avgRendita " +
            "ORDER BY count DESC")
    List<CategoriaStats> getStatisticsByCategoria();

    /**
     * Find fabbricati with specific utilità comuni
     */
    @Query("MATCH (f:Fabbricato) WHERE $foglio IN f.utilitaFogli AND $numero IN f.utilitaNumeri RETURN f")
    List<Fab> findByUtilitaComuni(@Param("foglio") String foglio, @Param("numero") String numero);

    /**
     * Interface for categoria statistics projection
     */
    interface CategoriaStats {
        String getCategoria();
        Long getCount();
        Double getAvgRendita();
    }
}
