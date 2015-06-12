package parallel;

/**
 * Modul dient der Sammlung aller gemeinsamen Variablen.
 */
public class SharedVariables {

    /**
     * Länge des Arrays inklusive der Abbildung von Randtemperaturwerten (+2).
     */
    public static final int QLR = InitializeParameter.QL + 2;

    /**
     * Breite des Arrays inklusive der Abbildung von Randtemperaturwerten (+2).
     */
    public static final int QBR = InitializeParameter.QB + 2;

    /**
     * Höhe des Arrays inklusive der Abblidung von Randtemperaturwerten (+2).
     */
    public static final int QHR = InitializeParameter.QH + 2;

    /**
     * Faktor für diskrete Gleichung.
     */
    public static final float FAKTOR = (InitializeParameter.ALPHA * InitializeParameter.T)
            / (InitializeParameter.S * InitializeParameter.S);
    /**
     * 3D Array mit folgender Koordinatenreihenfolge: [x][y][z]. Abbildung des Quaders.
     */
    public static float[][][] u1 = new float[QLR][QBR][QHR];
    /**
     * 3D Array mit folgender Koordinatenreihenfolge: [x][y][z]. Abbildung des Quaders.
     */
    public static float[][][] u2 = new float[QLR][QBR][QHR];
}
