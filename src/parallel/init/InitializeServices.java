package parallel.init;

import parallel.SharedVariables;
import parallel.computing.ComputingCallable;
import parallel.computing.ComputingRunnable;
import parallel.visualization.ColorService;

/**
 * Methoden für die Initialisierung.
 *
 */
public class InitializeServices {

	/**
	 * Erzeugt die initialien Berechnungsservices und teilt dazu den Quader in
	 * Scheiben auf entlang der x-Achse.
	 */
	public static void createComputingServices() {
		int start = 1;
		int end;
		int numberOfAreas;
		// Ja nach Thread Varinate die Unterteilung vornehmen
		if (InitializeParameter.THREAD_POOL) {
			numberOfAreas = InitializeParameter.NUMBER_OF_DATA_AREAS_THREADPOOL;
		} else {
			numberOfAreas = InitializeParameter.NUMBER_OF_THREADS;
		}

		int dataRangeQL = SharedVariables.QLR / numberOfAreas;
		for (int i = 0; i < numberOfAreas; i++) {
			// Automatische Aufteilung der Daten (in Scheiben)
			if (i < numberOfAreas - 1) {
				end = start + dataRangeQL;
			} else {
				end = SharedVariables.QLR - 1;
			}
			// Je nach Threaderzeugung initialisieren
			if (InitializeParameter.THREAD_POOL) {
				SharedVariables.computingCallable[i] = new ComputingCallable(
						start, end);
			} else {
				SharedVariables.computingRunnable[i] = new ComputingRunnable(
						start, end);
			}
			start = end;
		}
	}

	/**
	 * Initialisierung des Farbarrays anhand der vorgegebenen
	 * Initialisierungstemperaturen.
	 */
	public static void initializeColorArray() {
		for (int x = 0; x < SharedVariables.QLR_2D; x = x
				+ InitializeParameter.CELL_WIDTH) {
			for (int y = 0; y < SharedVariables.QBR_2D; y = y
					+ InitializeParameter.CELL_WIDTH) {

				// Auf x und y Quaderzellen runterrechnen
				int xCell = x / InitializeParameter.CELL_WIDTH;
				int yCell = y / InitializeParameter.CELL_WIDTH;

				float temperature = SharedVariables.u1[xCell][yCell][SharedVariables.Z_HALF];

				// Color Array initialisieren
				ColorService.computeAndSetColor(temperature, xCell, yCell);
			}
		}
	}

}
