package it.hl.neo4j.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Node("Fabbricato")
public class Fab {
    @Id
    private String identificativoImmobile;

    // Common fields for all record types
    @Property
    private String codiceAmministrativo;
    @Property
    private String sezione;
    @Property
    private String tipoImmobile;
    @Property
    private String progressivo;

    // Record Type 1 - Caratteristiche dell'unità immobiliare
    @Property
    private String zona;
    @Property
    private String categoria;
    @Property
    private String classe;
    @Property
    private String consistenza;
    @Property
    private String superficie;
    @Property
    private String renditaLire;
    @Property
    private String renditaEuro;

    // Ubicazione nel fabbricato
    @Property
    private String lotto;
    @Property
    private String edificio;
    @Property
    private String scala;
    @Property
    private String interno1;
    @Property
    private String interno2;
    @Property
    private String piano1;
    @Property
    private String piano2;
    @Property
    private String piano3;
    @Property
    private String piano4;

    // Atto generante
    @Property
    private String dataEfficaciaGenerante;
    @Property
    private String dataRegistrazioneGenerante;
    @Property
    private String tipoNotaGenerante;
    @Property
    private String numeroNotaGenerante;
    @Property
    private String progressivoNotaGenerante;
    @Property
    private String annoNotaGenerante;

    // Atto conclusivo
    @Property
    private String dataEfficaciaConclusiva;
    @Property
    private String dataRegistrazioneConclusiva;
    @Property
    private String tipoNotaConclusiva;
    @Property
    private String numeroNotaConclusiva;
    @Property
    private String progressivoNotaConclusiva;
    @Property
    private String annoNotaConclusiva;

    // Additional fields
    @Property
    private String partita;
    @Property
    private String annotazione;
    @Property
    private String identificativoMutazioneIniziale;
    @Property
    private String identificativoMutazioneFinale;
    @Property
    private String protocolloNotifica;
    @Property
    private String dataNotifica;
    @Property
    private String codiceCausaleAttoGenerante;
    @Property
    private String descrizioneAttoGenerante;
    @Property
    private String codiceCausaleAttoConclusivo;
    @Property
    private String descrizioneAttoConclusivo;
    @Property
    private String flagClassamento;

    // Record Type 2 - Identificativi (stored as lists)
    @Property
    private List<String> sezioniUrbane = new ArrayList<>();
    @Property
    private List<String> fogli = new ArrayList<>();
    @Property
    private List<String> numeri = new ArrayList<>();
    @Property
    private List<String> denominatori = new ArrayList<>();
    @Property
    private List<String> subalerni = new ArrayList<>();
    @Property
    private List<String> edificialita = new ArrayList<>();

    // Record Type 3 - Indirizzi (stored as lists)
    @Property
    private List<String> toponimi = new ArrayList<>();
    @Property
    private List<String> indirizzi = new ArrayList<>();
    @Property
    private List<String> civici1 = new ArrayList<>();
    @Property
    private List<String> civici2 = new ArrayList<>();
    @Property
    private List<String> civici3 = new ArrayList<>();
    @Property
    private List<String> codiciStrada = new ArrayList<>();

    // Record Type 4 - Utilità comuni (stored as lists)
    @Property
    private List<String> utilitaSezioniUrbane = new ArrayList<>();
    @Property
    private List<String> utilitaFogli = new ArrayList<>();
    @Property
    private List<String> utilitaNumeri = new ArrayList<>();
    @Property
    private List<String> utilitaDenominatori = new ArrayList<>();
    @Property
    private List<String> utilitaSubalerni = new ArrayList<>();

    // Record Type 5 - Riserve (stored as lists)
    @Property
    private List<String> codiciRiserva = new ArrayList<>();
    @Property
    private List<String> partiteIscrizioneRiserva = new ArrayList<>();

    // Display property for Neo4j Browser
    @Property
    private String displayName;

    /**
     * Parses a Type 1 record (main characteristics)
     */
    public static Fab parseType1(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 20) return null;

        Fab fab = new Fab();

        // Common fields
        fab.setCodiceAmministrativo(getField(campi, 0));
        fab.setSezione(getField(campi, 1));
        fab.setIdentificativoImmobile(getField(campi, 2));
        fab.setTipoImmobile(getField(campi, 3));
        fab.setProgressivo(getField(campi, 4));
        // Skip tipo record (position 5)

        // Classamento
        fab.setZona(getField(campi, 6));
        fab.setCategoria(getField(campi, 7));
        fab.setClasse(getField(campi, 8));
        fab.setConsistenza(getField(campi, 9));
        fab.setSuperficie(getField(campi, 10));
        fab.setRenditaLire(getField(campi, 11));
        fab.setRenditaEuro(getField(campi, 12));

        // Ubicazione
        fab.setLotto(getField(campi, 13));
        fab.setEdificio(getField(campi, 14));
        fab.setScala(getField(campi, 15));
        fab.setInterno1(getField(campi, 16));
        fab.setInterno2(getField(campi, 17));
        fab.setPiano1(getField(campi, 18));
        fab.setPiano2(getField(campi, 19));
        fab.setPiano3(getField(campi, 20));
        fab.setPiano4(getField(campi, 21));

        // Atto generante
        fab.setDataEfficaciaGenerante(getField(campi, 22));
        fab.setDataRegistrazioneGenerante(getField(campi, 23));
        fab.setTipoNotaGenerante(getField(campi, 24));
        fab.setNumeroNotaGenerante(getField(campi, 25));
        fab.setProgressivoNotaGenerante(getField(campi, 26));
        fab.setAnnoNotaGenerante(getField(campi, 27));

        // Atto conclusivo
        fab.setDataEfficaciaConclusiva(getField(campi, 28));
        fab.setDataRegistrazioneConclusiva(getField(campi, 29));
        fab.setTipoNotaConclusiva(getField(campi, 30));
        fab.setNumeroNotaConclusiva(getField(campi, 31));
        fab.setProgressivoNotaConclusiva(getField(campi, 32));
        fab.setAnnoNotaConclusiva(getField(campi, 33));

        // Additional fields
        fab.setPartita(getField(campi, 34));
        fab.setAnnotazione(getField(campi, 35));
        fab.setIdentificativoMutazioneIniziale(getField(campi, 36));
        fab.setIdentificativoMutazioneFinale(getField(campi, 37));
        fab.setProtocolloNotifica(getField(campi, 38));
        fab.setDataNotifica(getField(campi, 39));
        fab.setCodiceCausaleAttoGenerante(getField(campi, 40));
        fab.setDescrizioneAttoGenerante(getField(campi, 41));
        fab.setCodiceCausaleAttoConclusivo(getField(campi, 42));
        fab.setDescrizioneAttoConclusivo(getField(campi, 43));
        fab.setFlagClassamento(getField(campi, 44));

        // Set display name
        fab.updateDisplayName();

        return fab;
    }

    /**
     * Adds identificativi from Type 2 record
     */
    public void addIdentificativi(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 6) return;

        // Skip first 6 fields (common key), parse table of identificativi
        int pos = 6;
        while (pos + 5 < campi.length) {
            String sezioneUrbana = getField(campi, pos);
            String foglio = getField(campi, pos + 1);
            String numero = getField(campi, pos + 2);
            String denominatore = getField(campi, pos + 3);
            String subalterno = getField(campi, pos + 4);
            String edif = getField(campi, pos + 5);

            if (!Strings.isBlank(sezioneUrbana) || !Strings.isBlank(foglio) || !Strings.isBlank(numero)) {
                this.sezioniUrbane.add(sezioneUrbana);
                this.fogli.add(foglio);
                this.numeri.add(numero);
                this.denominatori.add(denominatore);
                this.subalerni.add(subalterno);
                this.edificialita.add(edif);
            }
            pos += 6;
        }
    }

    /**
     * Adds indirizzi from Type 3 record
     */
    public void addIndirizzi(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 6) return;

        // Skip first 6 fields (common key), parse table of indirizzi
        int pos = 6;
        while (pos + 5 < campi.length) {
            String toponimo = getField(campi, pos);
            String indirizzo = getField(campi, pos + 1);
            String civico1 = getField(campi, pos + 2);
            String civico2 = getField(campi, pos + 3);
            String civico3 = getField(campi, pos + 4);
            String codiceStrada = getField(campi, pos + 5);

            if (!Strings.isBlank(indirizzo)) {
                this.toponimi.add(toponimo);
                this.indirizzi.add(indirizzo);
                this.civici1.add(civico1);
                this.civici2.add(civico2);
                this.civici3.add(civico3);
                this.codiciStrada.add(codiceStrada);
            }
            pos += 6;
        }
    }

    /**
     * Adds utilità comuni from Type 4 record
     */
    public void addUtilitaComuni(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 6) return;

        // Skip first 6 fields (common key), parse table of utilità comuni
        int pos = 6;
        while (pos + 4 < campi.length) {
            String sezioneUrbana = getField(campi, pos);
            String foglio = getField(campi, pos + 1);
            String numero = getField(campi, pos + 2);
            String denominatore = getField(campi, pos + 3);
            String subalterno = getField(campi, pos + 4);

            if (!Strings.isBlank(foglio) || !Strings.isBlank(numero)) {
                this.utilitaSezioniUrbane.add(sezioneUrbana);
                this.utilitaFogli.add(foglio);
                this.utilitaNumeri.add(numero);
                this.utilitaDenominatori.add(denominatore);
                this.utilitaSubalerni.add(subalterno);
            }
            pos += 5;
        }
    }

    /**
     * Adds riserve from Type 5 record
     */
    public void addRiserve(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 6) return;

        // Skip first 6 fields (common key), parse table of riserve
        int pos = 6;
        while (pos + 1 < campi.length) {
            String codiceRiserva = getField(campi, pos);
            String partitaIscrizione = getField(campi, pos + 1);

            if (!Strings.isBlank(codiceRiserva)) {
                this.codiciRiserva.add(codiceRiserva);
                this.partiteIscrizioneRiserva.add(partitaIscrizione);
            }
            pos += 2;
        }
    }

    /**
     * Updates display name based on current data
     */
    public void updateDisplayName() {
        StringBuilder sb = new StringBuilder();

        if (!Strings.isBlank(categoria)) {
            sb.append("Cat. ").append(categoria);
        }

        if (!fogli.isEmpty() && !numeri.isEmpty()) {
            sb.append(" - Fg. ").append(fogli.get(0))
                    .append(" Part. ").append(numeri.get(0));
        }

        if (!subalerni.isEmpty() && !Strings.isBlank(subalerni.get(0))) {
            sb.append(" Sub. ").append(subalerni.get(0));
        }

        if (!indirizzi.isEmpty() && !Strings.isBlank(indirizzi.get(0))) {
            sb.append(" - ").append(indirizzi.get(0));
        }

        this.displayName = sb.toString().trim();
        if (this.displayName.startsWith("-")) {
            this.displayName = this.displayName.substring(1).trim();
        }
    }

    /**
     * Creates a composite key for grouping records
     */
    public String getCompositeKey() {
        return codiceAmministrativo + "|" + sezione + "|" + identificativoImmobile + "|" + tipoImmobile + "|" + progressivo;
    }

    /**
     * Safely gets field from array
     */
    private static String getField(String[] campi, int index) {
        return index < campi.length ? campi[index].trim() : "";
    }
}
