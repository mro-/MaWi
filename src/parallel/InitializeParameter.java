package parallel;

/**
 * Modul dient der Festlegung aller Konfigurationsparameter.
 */
public class InitializeParameter {

    /**
     * Physikalische Ausdehnung des Quaders, in der Länge (Angabe in Anzahl der Zellen, Wert >> 1).
     */
    public static final int QL = 10;

    /**
     * Physikalische Ausdehnung des Quaders, in der Breite (Angabe in Anzahl der Zellen, Wert >> 1).
     */
    public static final int QB = 10;

    /**
     * Physikalische Ausdehnung des Quaders, in der Höhe (Angabe in Anzahl der Zellen, Wert >> 1).
     */
    public static final int QH = 1;

    /**
     * Kantenlänge des finiten Elementes (einer Zelle).
     */
    public static final float S = 1f;

    /**
     * Temperaturleitfähigkeit des Quaders.
     */
    public static final float ALPHA = 0.01f;

    /**
     * Diskreter Zeitschritt t. Immer 1.
     */
    public static final float T = 1f;

    /**
     * Anzahl der Iterationsschritte.
     */
    public static final int N = 30;

    /**
     * Randtemperatur der linken Quaderseite.
     */
    public static final float RTL = 100;

    /**
     * Randtemperatur der rechten Quaderseite.
     */
    public static final float RTR = 80;

    /**
     * Randtemperatur der vorderen Quaderseite.
     */
    public static final float RTV = 50;

    /**
     * Randtemperatur der hinteren Quaderseite.
     */
    public static final float RTH = 60;

    /**
     * Randtemperatur der unteren Quaderseite.
     */
    public static final float RTU = 20;

    /**
     * Randtemperatur der oberen Quaderseite.
     */
    public static final float RTO = 20;

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
    public static final float MAX_TEMP = 100;

}
