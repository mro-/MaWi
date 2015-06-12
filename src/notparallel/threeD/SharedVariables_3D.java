package notparallel.threeD;

/**
 * Modul dient der Sammlung aller gemeinsamen Variablen.
 */
public class SharedVariables_3D {

    /**
     * Länge des Arrays inklusive der Abbildung von Randtemperaturwerten (+2).
     */
    public static final int QLR = InitializeParameter_3D.QL + 2;

    /**
     * Breite des Arrays inklusive der Abbildung von Randtemperaturwerten (+2).
     */
    public static final int QBR = InitializeParameter_3D.QB + 2;

    /**
     * Höhe des Arrays inklusive der Abblidung von Randtemperaturwerten (+2).
     */
    public static final int QHR = InitializeParameter_3D.QH + 2;

    /**
     * Faktor für diskrete Gleichung.
     */
    public static final float FAKTOR = (InitializeParameter_3D.ALPHA * InitializeParameter_3D.T)
            / (InitializeParameter_3D.S * InitializeParameter_3D.S);
    /**
     * 3D Array mit folgender Koordinatenreihenfolge: [x][y][z]. Abbildung des Quaders.
     */
    public static float[][][] u1 = new float[QLR][QBR][QHR];
    /**
     * 3D Array mit folgender Koordinatenreihenfolge: [x][y][z]. Abbildung des Quaders.
     */
    public static float[][][] u2 = new float[QLR][QBR][QHR];
}
