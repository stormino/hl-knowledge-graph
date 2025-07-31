package it.hl.neo4j.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@Data
@NoArgsConstructor
@RelationshipProperties
public class OwnershipRelationship {
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

    @TargetNode
    private Ter terreno;

    public static OwnershipRelationship fromTit(Tit tit) {
        OwnershipRelationship ownership = new OwnershipRelationship();
        ownership.setCodiceAmministrativo(tit.getCodiceAmministrativo());
        ownership.setSezione(tit.getSezione());
        ownership.setTipoSoggetto(tit.getTipoSoggetto());
        ownership.setTipoImmobile(tit.getTipoImmobile());
        ownership.setCodiceDiritto(tit.getCodiceDiritto());
        ownership.setTitoloNonCodificato(tit.getTitoloNonCodificato());
        ownership.setQuotaNumeratore(tit.getQuotaNumeratore());
        ownership.setQuotaDenominatore(tit.getQuotaDenominatore());
        ownership.setRegime(tit.getRegime());
        ownership.setSoggettoDiRiferimento(tit.getSoggettoDiRiferimento());
        ownership.setDataValidita(tit.getDataValidita());
        ownership.setTipoNota(tit.getTipoNota());
        ownership.setNumeroNota(tit.getNumeroNota());
        ownership.setProgressivoNota(tit.getProgressivoNota());
        ownership.setAnnoNota(tit.getAnnoNota());
        ownership.setDataRegistrazioneAtti(tit.getDataRegistrazioneAtti());
        ownership.setPartita(tit.getPartita());
        ownership.setDataValidita2(tit.getDataValidita2());
        ownership.setTipoNota2(tit.getTipoNota2());
        ownership.setNumeroNota2(tit.getNumeroNota2());
        ownership.setProgressivoNota2(tit.getProgressivoNota2());
        ownership.setAnnoNota2(tit.getAnnoNota2());
        ownership.setDataRegistrazioneAtti2(tit.getDataRegistrazioneAtti2());
        ownership.setIdentificativoMutazioneIniziale(tit.getIdentificativoMutazioneIniziale());
        ownership.setIdentificativoMutazioneFinale(tit.getIdentificativoMutazioneFinale());
        ownership.setIdentificativoTitolarita(tit.getIdentificativoTitolarita());
        ownership.setCodiceCausaleAttoGenerante(tit.getCodiceCausaleAttoGenerante());
        ownership.setDescrizioneAttoGenerante(tit.getDescrizioneAttoGenerante());
        ownership.setCodiceCausaleAttoConclusivo(tit.getCodiceCausaleAttoConclusivo());
        ownership.setDescrizioneAttoConclusivo(tit.getDescrizioneAttoConclusivo());
        ownership.setTerreno(tit.getTerreno());
        return ownership;
    }
}