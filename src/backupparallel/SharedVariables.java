package backupparallel;

import javafx.scene.paint.Color;

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
	 * Z-Wert bei dem der Quaderquerschnitt stattfindet.
	 */
	public static final int Z_HALF = SharedVariables.QHR / 2;

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
	 * 11 Farben (müssen insgesamt 11 sein!!!) zur Darstellung. Von blau bis
	 * rot.
	 */
	public static final Color[] COLORS = { Color.rgb(0, 0, 255), // blau
			Color.rgb(137, 216, 230), // hell blau
			Color.rgb(200, 255, 200), // blau grün
			Color.rgb(220, 255, 150), // gelb grün
			Color.rgb(255, 255, 100, 0.8), // hell gelb
			Color.rgb(255, 255, 0), // gelb
			Color.rgb(255, 201, 14), // gelb orange
			Color.rgb(255, 130, 0), // orange
			Color.rgb(255, 80, 0), // orange rot
			Color.rgb(255, 0, 0), // rot
			Color.rgb(190, 1, 0) // dunkel rot
	};

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
	 * Array, das die Berechnungsservices beinhaltet. (Aufteilung in Scheiben)
	 */
	public static ComputingService[] computingServices = new ComputingService[InitializeParameter.NUMBER_OF_THREADS];

}
