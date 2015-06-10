package parallel;

/**
 * Modul dient der Festlegung aller Konfigurationsparameter.
 */
public final class InitializeParameter {

    /**
     * Physikalische Ausdehnung des Quaders, in der Länge (Angabe in Anzahl der Zellen, Wert >> 1).
     */
    public static final int QL = 40;

    /**
     * Physikalische Ausdehnung des Quaders, in der Breite (Angabe in Anzahl der Zellen, Wert >> 1).
     */
    public static final int QB = 40;

    /**
     * Physikalische Ausdehnung des Quaders, in der Höhe (Angabe in Anzahl der Zellen, Wert >> 1).
     */
    public static final int QH = 40;

    /**
     * Kantenlänge des finiten Elementes (einer Zelle).
     */
    public static final float S = 0.1f;

    /**
     * Temperaturleitfähigkeit des Quaders.
     */
    public static final float ALPHA = 0.01f;

    /**
     * Anzahl der Iterationsschritte.
     */
    public static final int N = 50;

    /**
     * Randtemperatur der linken Quaderseite.
     */
    public static final float RT_L = 50;

    /**
     * Randtemperatur aller Quaderseiten, bis auf die linke Seite.
     */
    public static final float RT_OTHER = 20;

    /**
     * Temperatur des Quaders zum Startzeitpunkt t=0.
     */
    public static final float TS = 20;

    /**
     * Festlegung der minimalen Temperatur (Simulationsfarbe blau).
     */
    public static final float MIN_TEMP = 0;

    /**
     * Festlegung der maximalen Temperatur (Simulationsfarbe rot).
     */
    public static final float MAX_TEMP = 50;

    // TODO
    public static float[][] u1 = new float[QL][QB];

    public static float[][] u2 = new float[QL][QB];

}
