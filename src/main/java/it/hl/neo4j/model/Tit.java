package it.hl.neo4j.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@Data
@NoArgsConstructor
@RelationshipProperties
public class Tit {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String codiceAmministrativo;
    @Property
    private String sezione;
    @Property
    private String tipoSoggetto;
    @Property
    private String tipoImmobile;
    @Property
    private String codiceDiritto;
    @Property
    private String titoloNonCodificato;
    @Property
    private String quotaNumeratore;
    @Property
    private String quotaDenominatore;
    @Property
    private String regime;
    @Property
    private String soggettoDiRiferimento;
    @Property
    private String dataValidita;
    @Property
    private String tipoNota;
    @Property
    private String numeroNota;
    @Property
    private String progressivoNota;
    @Property
    private String annoNota;
    @Property
    private String dataRegistrazioneAtti;
    @Property
    private String partita;
    @Property
    private String dataValidita2;
    @Property
    private String tipoNota2;
    @Property
    private String numeroNota2;
    @Property
    private String progressivoNota2;
    @Property
    private String annoNota2;
    @Property
    private String dataRegistrazioneAtti2;
    @Property
    private String identificativoMutazioneIniziale;
    @Property
    private String identificativoMutazioneFinale;
    @Property
    private String identificativoTitolarita;
    @Property
    private String codiceCausaleAttoGenerante;
    @Property
    private String descrizioneAttoGenerante;
    @Property
    private String codiceCausaleAttoConclusivo;
    @Property
    private String descrizioneAttoConclusivo;
    @Property
    private String displayName;

    @TargetNode
    private Ter terreno;

    public static Tit parse(String input) {
        String[] fields = input.split("\\|");
        if (fields.length < 32)
            throw new IllegalArgumentException("La stringa di input non contiene tutti i campi richiesti.");

        Tit tit = new Tit();
        tit.setCodiceAmministrativo(fields[0]);
        tit.setSezione(fields[1]);
        // Skip identificativoSoggetto (fields[2]) - handled by relationship
        tit.setTipoSoggetto(fields[3]);
        // Skip identificativoImmobile (fields[4]) - handled by relationship
        tit.setTipoImmobile(fields[5]);
        tit.setCodiceDiritto(fields[6]);
        tit.setTitoloNonCodificato(fields[7]);
        tit.setQuotaNumeratore(fields[8]);
        tit.setQuotaDenominatore(fields[9]);
        tit.setRegime(fields[10]);
        tit.setSoggettoDiRiferimento(fields[11]);
        tit.setDataValidita(fields[12]);
        tit.setTipoNota(fields[13]);
        tit.setNumeroNota(fields[14]);
        tit.setProgressivoNota(fields[15]);
        tit.setAnnoNota(fields[16]);
        tit.setDataRegistrazioneAtti(fields[17]);
        tit.setPartita(fields[18]);
        tit.setDataValidita2(fields[19]);
        tit.setTipoNota2(fields[20]);
        tit.setNumeroNota2(fields[21]);
        tit.setProgressivoNota2(fields[22]);
        tit.setAnnoNota2(fields[23]);
        tit.setDataRegistrazioneAtti2(fields[24]);
        tit.setIdentificativoMutazioneIniziale(fields[25]);
        tit.setIdentificativoMutazioneFinale(fields[26]);
        tit.setIdentificativoTitolarita(fields[27]);
        tit.setCodiceCausaleAttoGenerante(fields[28]);
        tit.setDescrizioneAttoGenerante(fields[29]);
        tit.setCodiceCausaleAttoConclusivo(fields[30]);
        tit.setDescrizioneAttoConclusivo(fields[31]);

        // Set display name for better visualization
        if (tit.quotaDenominatore.compareTo("0") != 0 && tit.quotaNumeratore.compareTo("0") != 0) {
            tit.setDisplayName(CodiciDiritto.fromCodice(tit.codiceDiritto).getDescrizione() + " (" + tit.quotaNumeratore + "/" + tit.quotaDenominatore + ")");
        } else {
            tit.setDisplayName(CodiciDiritto.fromCodice(tit.codiceDiritto).getDescrizione());
        }
        return tit;
    }

    // Helper method to extract identificativoSoggetto from raw input
    public static String extractIdentificativoSoggetto(String input) {
        String[] fields = input.split("\\|");
        return fields.length > 2 ? fields[2] : null;
    }

    // Helper method to extract identificativoImmobile from raw input
    public static String extractIdentificativoImmobile(String input) {
        String[] fields = input.split("\\|");
        return fields.length > 4 ? fields[4] : null;
    }
}