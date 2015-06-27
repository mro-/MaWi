package parallel;

/**
 * Modul dient der Festlegung aller Konfigurationsparameter.
 */
public class InitializeParameter {

	/**
	 * X: Physikalische Ausdehnung des Quaders, in der Länge (Angabe in Anzahl
	 * der Zellen, Wert >> 1).
	 */
	public static final int QL = 100;

	/**
	 * Y: Physikalische Ausdehnung des Quaders, in der Breite (Angabe in Anzahl
	 * der Zellen, Wert >> 1).
	 */
	public static final int QB = 100;

	/**
	 * Z: Physikalische Ausdehnung des Quaders, in der Höhe (Angabe in Anzahl
	 * der Zellen, Wert >> 1).
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
	 * Diskreter Zeitschritt t.
	 */
	public static final float T = 1f;

	/**
	 * Anzahl der Iterationsschritte.
	 */
	public static final int N = 300;

	/**
	 * Randtemperatur der linken Quaderseite.
	 */
	public static final float RTL = 500;

	/**
	 * Randtemperatur der rechten Quaderseite.
	 */
	public static final float RTR = 0;

	/**
	 * Randtemperatur der vorderen Quaderseite.
	 */
	public static final float RTV = 100;

	/**
	 * Randtemperatur der hinteren Quaderseite.
	 */
	public static final float RTH = 100;

	/**
	 * Randtemperatur der unteren Quaderseite.
	 */
	public static final float RTU = 0;

	/**
	 * Randtemperatur der oberen Quaderseite.
	 */
	public static final float RTO = 0;

	/**
	 * Temperatur des Quaders zum Startzeitpunkt t=0.
	 */
	public static final float TS = 100;

	/**
	 * Festlegung der minimalen Temperatur (Simulationsfarbe blau).
	 */
	public static final float MIN_TEMP = 0;

	/**
	 * Festlegung der maximalen Temperatur (Simulationsfarbe rot).
	 */
	public static final float MAX_TEMP = 300;

	/**
	 * Festlegung, wie viele Pixel für die Visualisierung einer Zelle benutzt
	 * werden sollen. z.B. Wert 4: 4x4 Pixel => 1 Quaderzelle
	 */
	public static final int CELL_WIDTH = 5;

	/**
	 * Werden die Threads mittels Threadpool verwaltet? Bei false werden immer
	 * neue Threads angelegt.
	 */
	public static final boolean THREAD_POOL = true;

	/**
	 * Gibt die Anzahl der Threads an. Konfigurierbar für die Leistungsmessung
	 * (Wert >> 0).
	 */
	public static final int NUMBER_OF_THREADS = 4;

	/**
	 * Gibt an in wieviele Scheiben der Quader aufgeteilt werden sollen (Wert >>
	 * 0). Funktioniert nur im Zusammenhang mit der Thread-Pool Nutzung. Ohne
	 * Thread-Pool muss die Anzahl so groß sein, wie NUMBER_OF_THREADS.
	 * Ansonsten passt die Berechnung nicht und es kommt zur Fehlermeldung. <br>
	 * Konfigurierbar für die Leistungsmessung. <br>
	 */
	public static final int NUMBER_OF_DATA_AREAS_THREADPOOL = 4;

	/**
	 * Festlegung der Hitzequelle der linken Seite<br>
	 * 1: Konstante Temperatur der gesamten Fläche <br>
	 * 2: Wärmequelle in der Mitte der Fläche <br>
	 * 3: Sinusfunktion, Änderung im zeitlichen Verlauf
	 */
	public static final int HEAT_MODE = 1;

	/**
	 * Sollen die Zwischenschritte und das Endergebnis visualisiert werden?
	 */
	public static final boolean VISUALIZATION = true;

}
