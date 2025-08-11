package it.hl.neo4j.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@NoArgsConstructor
@Node("Terreno")
public class Ter {
    @Id
    private String identificativoImmobile;

    @Property
    private String codiceAmministrativo;
    @Property
    private String sezione;
    @Property
    private String tipoImmobile;
    @Property
    private String progressivo;
    @Property
    private String tipoRecord;
    @Property
    private String foglio;
    @Property
    private String numero;
    @Property
    private String denominatore;
    @Property
    private String subalterno;
    @Property
    private String edificabilita;
    @Property
    private String qualita;
    @Property
    private String classe;
    @Property
    private Integer ettari;
    @Property
    private Integer are;
    @Property
    private Integer centiare;
    @Property
    private String flagReddito;
    @Property
    private String flagPorzione;
    @Property
    private String flagDeduzioni;
    @Property
    private String redditoDominicaleLire;
    @Property
    private String redditoAgrarioLire;
    @Property
    private String redditoDominicaleEuro;
    @Property
    private String redditoAgrarioEuro;

    // Display property for Neo4j Browser
    @Property
    private String displayName;

    public static Ter parse(String input) {
        String[] campi = input.split("\\|", -1);
        if (campi.length < 23) return null;

        Ter ter = new Ter();
        ter.setCodiceAmministrativo(campi[0]);
        ter.setSezione(campi[1]);
        ter.setIdentificativoImmobile(campi[2]);
        ter.setTipoImmobile(campi[3]);
        ter.setProgressivo(campi[4]);
        ter.setTipoRecord(campi[5]);
        ter.setFoglio(campi[6]);
        ter.setNumero(campi[7]);
        ter.setDenominatore(campi[8]);
        ter.setSubalterno(campi[9]);
        ter.setEdificabilita(campi[10]);
        ter.setQualita(campi[11]);
        ter.setClasse(campi[12]);
        ter.setEttari(Integer.parseInt(Strings.isBlank(campi[13]) ? "0" : campi[13]));
        ter.setAre(Integer.parseInt(Strings.isBlank(campi[13]) ? "0" : campi[14]));
        ter.setCentiare(Integer.parseInt(Strings.isBlank(campi[13]) ? "0" : campi[15]));
        ter.setFlagReddito(campi[16]);
        ter.setFlagPorzione(campi[17]);
        ter.setFlagDeduzioni(campi[18]);
        ter.setRedditoDominicaleLire(campi[19]);
        ter.setRedditoAgrarioLire(campi[20]);
        ter.setRedditoDominicaleEuro(campi[21]);
        ter.setRedditoAgrarioEuro(campi[22]);

        // Set display name for better visualization
        String foglio = ter.getFoglio() != null ? ter.getFoglio() : "";
        String numero = ter.getNumero() != null ? ter.getNumero() : "";
        String qualita = ter.getQualita() != null ? ter.getQualita() : "";
        ter.setDisplayName("Foglio " + foglio + " - Part. " + numero + " (" + qualita + ")");

        return ter;
    }
}