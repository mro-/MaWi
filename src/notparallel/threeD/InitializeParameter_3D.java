package notparallel.threeD;

/**
 * Modul dient der Festlegung aller Konfigurationsparameter.
 */
public class InitializeParameter_3D {

	/**
	 * Physikalische Ausdehnung des Quaders, in der Länge (Angabe in Anzahl der
	 * Zellen, Wert >> 1).
	 */
	public static final int QL = 10;

	/**
	 * Physikalische Ausdehnung des Quaders, in der Breite (Angabe in Anzahl der
	 * Zellen, Wert >> 1).
	 */
	public static final int QB = 10;

	/**
	 * Physikalische Ausdehnung des Quaders, in der Höhe (Angabe in Anzahl der
	 * Zellen, Wert >> 1).
	 */
	public static final int QH = 10;

	/**
	 * Kantenlänge des finiten Elementes (einer Zelle).
	 */
	public static final float S = 1f;

	/**
	 * Temperaturleitfähigkeit des Quaders.
	 */
	public static final float ALPHA = 0.1f;

	/**
	 * Diskreter Zeitschritt t. Immer 1?
	 */
	public static final float T = 1f;

	/**
	 * Anzahl der Iterationsschritte.
	 */
	// 180
	public static final int N = 10;

	/**
	 * Randtemperatur der linken Quaderseite.
	 */
	public static final float RTL = 100f;

	/**
	 * Randtemperatur der rechten Quaderseite.
	 */
	public static final float RTR = 30f;

	/**
	 * Randtemperatur der vorderen Quaderseite.
	 */
	public static final float RTV = 30f;

	/**
	 * Randtemperatur der hinteren Quaderseite.
	 */
	public static final float RTH = 30f;

	/**
	 * Randtemperatur der unteren Quaderseite.
	 */
	public static final float RTU = 30f;

	/**
	 * Randtemperatur der oberen Quaderseite.
	 */
	public static final float RTO = 30f;

	/**
	 * Temperatur des Quaders zum Startzeitpunkt t=0.
	 */
	public static final float TS = 30f;

	/**
	 * Festlegung der minimalen Temperatur (Simulationsfarbe blau).
	 */
	public static final float MIN_TEMP = 0f;

	/**
	 * Festlegung der maximalen Temperatur (Simulationsfarbe rot).
	 */
	public static final float MAX_TEMP = 100f;

}
