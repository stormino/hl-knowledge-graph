package it.hl.neo4j.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum CodiciDiritto {
    PROPRIETA("10", "Proprietà"),
    PROPRIETA_SUPERFICIARIA("1s", "Proprietà superficiaria"),
    PROPRIETA_PER_AREA("1t", "Proprietà per l'area"),
    NUDA_PROPRIETA("20", "Nuda proprietà"),
    NUDA_PROPRIETA_SUPERFICIARIA("2s", "Nuda proprietà superficiaria"),
    ABITAZIONE("30", "Abitazione"),
    COMPROPRIETARIO("3", "Comproprietario"),
    ABITAZIONE_SU_PROPRIETA_SUPERFICIARIA("3s", "Abitazione su proprietà superficiaria"),
    DIRITTO_DEL_CONCEDENTE("40", "Diritto del concedente"),
    COMPROPRIETARIO_PER("4", "Comproprietario per"),
    ENFITEUSI("50", "Enfiteusi"),
    SUPERFICIE("60", "Superficie"),
    USO("70", "Uso"),
    COMPROPRIETARIO_DEL_FABBRICATO("7", "Comproprietario del fabbricato"),
    USO_PROPRIETA_SUPERFICIARIA("7s", "Uso proprietà superficiaria"),
    USUFRUTTO("80", "Usufrutto"),
    USUFRUTTO_CON_DIRITTO_ACCRESCIMENTO("8a", "Usufrutto con diritto di accrescimento"),
    USUFRUTTO_SU_ENFITEUSI("8e", "Usufrutto su enfiteusi"),
    USUFRUTTO_SU_PROPRIETA_SUPERFICIARIA("8s", "Usufrutto su proprietà superficiaria"),
    COMPROPRIETARIO_PER_AREA("8", "Comproprietario per l'area"),
    SERVITU("90", "Servità"),
    ONERI("100", "Oneri"),
    CONCEDENTE_IN_PARTE("12", "Concedente in parte"),
    LIVELLARIO_PARZIALE_PER("14", "Livellario parziale per"),
    USUFRUTTUARIO_PARZIALE_PER("15", "Usufruttuario parziale per"),
    LIVELLARIO("20", "Livellario"),
    LIVELLARIO_PER("21", "Livellario per"),
    LIVELLARIO_IN_PARTE("22", "Livellario in parte"),
    ENFITEUTA_IN_PARTE("25", "Enfiteuta in parte"),
    COLONO_PERPETUO("26", "Colono perpetuo"),
    COLONO_PERPETUO_PER("27", "Colono perpetuo per"),
    COLONO_PERPETUO_IN_PARTE("28", "Colono perpetuo in parte"),
    USUFRUTTUARIO_PARZIALE("30", "Usufruttuario parziale"),
    COUSUFRUTTUARIO_GENERALE("33", "Cousufruttuario generale"),
    USUFRUTTUARIO_GENERALE_DI_LIVELLO("36", "Usufruttuario generale di livello"),
    USUFRUTTUARIO_PARZIALE_DI_LIVELLO("37", "Usufruttuario parziale di livello"),
    USUFRUTTUARIO_PARZIALE_DI_ENFITEUSI("39", "Usufruttuario parziale di enfiteusi"),
    USUFRUTTUARIO_GENERALE_DI_COLONIA("40", "Usufruttuario generale di colonia"),
    USUFRUTTUARIO_PARZIALE_DI_COLONIA("41", "Usufruttuario parziale di colonia"),
    USUFRUTTUARIO_GENERALE_DI_DOMINIO_DIRETTO("42", "Usufruttuario generale di dominio diretto"),
    USUFRUTTUARIO_PARZIALE_DI_DOMINIO_DIRETTO("43", "Usufruttuario parziale di dominio diretto"),
    COUSUFRUTTUARIO_PER("50", "Cousufruttuario per"),
    USUARIO_PERPETUO("52", "Usuario perpetuo"),
    USUARIO_A_TEMPO_DETERMINATO("53", "Usuario a tempo determinato"),
    COUSUFRUTTUARIO_DI_LIVELLO("60", "Cousufruttuario di livello"),
    COUSUFRUTTUARIO_GENERALE_DI_LIVELLO("61", "Cousufruttuario generale di livello"),
    USUFRUTTUARIO_DI_LIVELLO_DI("62", "Usufruttuario di livello di"),
    COMPROPRIETARIO_PER_PARTE_DI("64", "Comproprietario per parte di"),
    USUFRUTTUARIO_DI_COLONIA_PER("70", "Usufruttuario di colonia per"),
    USUFRUTTUARIO_DI_DOMINIO_DIRETTO_PER("71", "Usufruttuario di dominio diretto per"),
    COUSUFRUTTUARIO_GENERALE_CON_DIRITTO_DI("72", "Cousufruttuario generale con diritto di"),
    UTILISTA_DELLA_SUPERFICIE("16", "Utilista della superficie"),
    UTILISTA_DELLA_SUPERFICIE_PER("17", "Utilista della superficie per"),
    BENEFICIARIO("35", "Beneficiario"),
    BENEFICIARIO_PER("65", "Beneficiario per"),
    BENEFICIARIO_DI_DOMINIO_DIRETTO("54", "Beneficiario di dominio diretto"),
    POSSESSORE("46", "Possessore"),
    POSSESSORE_PER("47", "Possessore per"),
    COMPOSSESSORE("48", "Compossessore"),
    COMPOSSESSORE_PER("49", "Compossessore per"),
    CONTESTATARIO("55", "Contestatario"),
    CONTESTATARIO_PER("56", "Contestatario per"),
    CONTESTATARIO_PER_USUFRUTTO("57", "Contestatario per usufrutto"),
    PRESENZA_TITOLO_NON_CODIFICATO("99", "Presenza di titolo non codificato"),
    PRESENZA_TITOLO_NON_CODIFICATO_990("990", "Presenza di titolo non codificato"),
    ASSENZA_TITOLO("0", "Assenza di titolo");

    private final String codice;
    private final String descrizione;

    CodiciDiritto(String codice, String descrizione) {
        this.codice = codice;
        this.descrizione = descrizione;
    }

    /**
     * Find enum by code value
     * @param codice the code to search for
     * @return the matching CodiciDiritto or null if not found
     */
    public static CodiciDiritto fromCodice(String codice) {
        for (CodiciDiritto diritto : values()) {
            if (diritto.codice.equals(codice)) {
                return diritto;
            }
        }
        return null;
    }

    /**
     * Find enum by code value with Optional return
     * @param codice the code to search for
     * @return Optional containing the matching CodiciDiritto
     */
    public static Optional<CodiciDiritto> findByCodice(String codice) {
        return Optional.ofNullable(fromCodice(codice));
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", descrizione, codice);
    }
}