package parallel;

import parallel.computing.ComputingCallable;
import parallel.computing.ComputingRunnable;
import parallel.init.InitializeParameter;
import javafx.scene.paint.Color;

/**
 * Modul dient der Sammlung aller gemeinsamen Variablen.
 */
public class SharedVariables {

	/**
	 * X: Länge des Arrays inklusive der Abbildung von Randtemperaturwerten
	 * (+2).
	 */
	public static final int QLR = InitializeParameter.QL + 2;

	/**
	 * Y: Breite des Arrays inklusive der Abbildung von Randtemperaturwerten
	 * (+2).
	 */
	public static final int QBR = InitializeParameter.QB + 2;

	/**
	 * Z: Höhe des Arrays inklusive der Abblidung von Randtemperaturwerten (+2).
	 */
	public static final int QHR = InitializeParameter.QH + 2;

	/**
	 * Faktor für diskrete Gleichung.
	 */
	public static final float FACTOR = (InitializeParameter.ALPHA * InitializeParameter.T)
			/ (InitializeParameter.S * InitializeParameter.S);

	/**
	 * Z-Wert bei dem der Quaderquerschnitt stattfindet.
	 */
	public static final int Z_HALF = SharedVariables.QHR / 2;

	/**
	 * Durchschnittliche Randtemperatur der linken Randtemperaturen berechnen
	 */
	public static final float AVERAGE_BORDER_TEMP_LEFT_SIDE = (InitializeParameter.RTO
			+ InitializeParameter.RTU + InitializeParameter.RTV + InitializeParameter.RTH) / 4;

	/**
	 * Länge der 2D Fläche zur Visualisierung.
	 */
	public static final int QLR_2D = SharedVariables.QLR
			* InitializeParameter.CELL_WIDTH;

	/**
	 * Breite der 2D Fläche zur Visualisierung.
	 */
	public static final int QBR_2D = SharedVariables.QBR
			* InitializeParameter.CELL_WIDTH;

	/**
	 * 3D Array mit folgender Koordinatenreihenfolge: [x][y][z]. Abbildung des
	 * Quaders.
	 */
	public static float[][][] u1 = new float[QLR][QBR][QHR];

	/**
	 * 3D Array mit folgender Koordinatenreihenfolge: [x][y][z]. Abbildung des
	 * Quaders.
	 */
	public static float[][][] u2 = new float[QLR][QBR][QHR];

	/**
	 * Enthält u1 die Ausgangswerte zur Berechnung? Falls nicht enthält u2 die
	 * alten Werte. Zustand wird mit dieser Variablen festgehalten, damit man
	 * sich das Kopieren des Arrays spart.
	 */
	public static boolean isu1Base = true;

	/**
	 * Array, das die berechneten Farben für die darzustellende Fläche
	 * beinhaltet.
	 */
	public static Color[][] tempInColor = new Color[QLR][QBR];

	/**
	 * Array, das die Berechnungsservices beinhaltet. Berechnung erfolgt mit
	 * immer neu angelegten Threads. (Aufteilung in Scheiben)
	 */
	public static ComputingRunnable[] computingRunnable = new ComputingRunnable[InitializeParameter.NUMBER_OF_THREADS];

	/**
	 * Array, das die Berechnungsservices beinhaltet. Berechnung erfolgt mit
	 * einem Threadpool. (Aufteilung in Scheiben)
	 */
	public static ComputingCallable[] computingCallable = new ComputingCallable[InitializeParameter.NUMBER_OF_DATA_AREAS_THREADPOOL];

}
