package it.hl.neo4j.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;
import java.util.Set;

@Data
@NoArgsConstructor
@Node("Soggetto")
public class Sog {
    @Id
    private String identificativoSoggetto;

    @Property
    private String codiceAmministrativo;
    @Property
    private String sezione;
    @Property
    private String tipoSoggetto; // "P" o "G"
    @Property
    private String nome;
    @Property
    private String identificativoFiscale;

    // Display property for Neo4j Browser
    @Property
    private String displayName;

    // Campi specifici per persona fisica (P)
    @Property
    private String cognome;
    @Property
    private String nomePersonaFisica;
    @Property
    private String sesso;
    @Property
    private String dataNascita;
    @Property
    private String luogoNascita;
    @Property
    private String codiceFiscale;
    @Property
    private String indicazioniSupplementari;

    // Campi specifici per persona giuridica (G)
    @Property
    private String denominazione;
    @Property
    private String sede;
    @Property
    private String partitaIva;

    @Relationship(type = "POSSIEDE_TERRENO", direction = Relationship.Direction.OUTGOING)
    private Set<TitTer> titTers;

    @Relationship(type = "POSSIEDE_FABBRICATO", direction = Relationship.Direction.OUTGOING)
    private Set<TitFab> titFabs;

    public static Sog parse(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 5)
            throw new IllegalArgumentException("Stringa input non valida.");

        Sog sog = new Sog();
        sog.setCodiceAmministrativo(campi[0]);
        sog.setSezione(campi[1]);
        sog.setIdentificativoSoggetto(campi[2]);
        sog.setTipoSoggetto(campi[3]);

        if ("P".equals(sog.getTipoSoggetto())) {
            // Persona fisica
            sog.setCognome(campi.length > 4 ? campi[4] : null);
            sog.setNomePersonaFisica(campi.length > 5 ? campi[5] : null);
            sog.setSesso(campi.length > 6 ? campi[6] : null);
            sog.setDataNascita(campi.length > 7 ? campi[7] : null);
            sog.setLuogoNascita(campi.length > 8 ? campi[8] : null);
            sog.setCodiceFiscale(campi.length > 9 ? campi[9] : null);
            sog.setIndicazioniSupplementari(campi.length > 10 ? campi[10] : null);

            sog.setNome((sog.getNomePersonaFisica() + " " + sog.getCognome()).trim());
            sog.setIdentificativoFiscale(sog.getCodiceFiscale() != null ? sog.getCodiceFiscale() : "");
        } else if ("G".equals(sog.getTipoSoggetto())) {
            // Persona giuridica
            sog.setDenominazione(campi.length > 4 ? campi[4] : null);
            sog.setSede(campi.length > 5 ? campi[5] : null);
            sog.setPartitaIva(campi.length > 6 ? campi[6] : null);

            sog.setNome(sog.getDenominazione() != null ? sog.getDenominazione() : "");
            sog.setIdentificativoFiscale(sog.getPartitaIva() != null ? sog.getPartitaIva() : "");
        } else {
            throw new IllegalArgumentException("Tipo soggetto non riconosciuto.");
        }

        // Set display name for better visualization
        sog.setDisplayName(sog.getNome() + " (" + sog.getIdentificativoFiscale() + ")");

        return sog;
    }
}