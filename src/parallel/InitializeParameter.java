package parallel;

/**
 * Modul dient der Festlegung aller Konfigurationsparameter.
 */
public class InitializeParameter {

	/**
	 * Physikalische Ausdehnung des Quaders, in der Länge (Angabe in Anzahl der
	 * Zellen, Wert >> 1).
	 */
	public static final int QL = 100;

	/**
	 * Physikalische Ausdehnung des Quaders, in der Breite (Angabe in Anzahl der
	 * Zellen, Wert >> 1).
	 */
	public static final int QB = 100;

	/**
	 * Physikalische Ausdehnung des Quaders, in der Höhe (Angabe in Anzahl der
	 * Zellen, Wert >> 1).
	 */
	public static final int QH = 100;

	/**
	 * Kantenlänge des finiten Elementes (einer Zelle).
	 */
	public static final float S = 1f;

	/**
	 * Temperaturleitfähigkeit des Quaders.
	 */
	public static final float ALPHA = 0.1f;

	/**
	 * Diskreter Zeitschritt t. Immer 1.
	 */
	public static final float T = 1f;

	/**
	 * Anzahl der Iterationsschritte.
	 */
	public static final int N = 100;

	/**
	 * Randtemperatur der linken Quaderseite.
	 */
	public static final float RTL = 100;

	/**
	 * Randtemperatur der rechten Quaderseite.
	 */
	public static final float RTR = 30;

	/**
	 * Randtemperatur der vorderen Quaderseite.
	 */
	public static final float RTV = 30;

	/**
	 * Randtemperatur der hinteren Quaderseite.
	 */
	public static final float RTH = 30;

	/**
	 * Randtemperatur der unteren Quaderseite.
	 */
	public static final float RTU = 30;

	/**
	 * Randtemperatur der oberen Quaderseite.
	 */
	public static final float RTO = 30;

	/**
	 * Temperatur des Quaders zum Startzeitpunkt t=0.
	 */
	public static final float TS = 30;

	/**
	 * Festlegung der minimalen Temperatur (Simulationsfarbe blau).
	 */
	public static final float MIN_TEMP = 0;

	/**
	 * Festlegung der maximalen Temperatur (Simulationsfarbe rot).
	 */
	public static final float MAX_TEMP = 100;

	/**
	 * Festlegung, wie viele Pixel für die Visualisierung einer Zelle benutzt
	 * werden sollen. z.B. Wert 4: 4x4 Pixel => 1 Quaderzelle
	 */
	public static final int CELL_WIDTH = 4;

}
