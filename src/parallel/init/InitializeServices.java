package parallel.init;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import parallel.InitializeParameter;
import parallel.computing.ComputingCallable;
import parallel.computing.ComputingRunnable;
import parallel.visualization.ColorService;

/**
 * Methoden für die Initialisierung.
 *
 */
public class InitializeServices {

	/**
	 * Erzeugt die initialien Berechnungsservices und teilt dazu den Quader
	 * entlang der X-Achse in Scheiben auf. <br>
	 * Die Aufteilung ist davon abhängig, ob ein der Thread-Pool aktiviert ist
	 * oder nicht. Bei Aktivierung kann sich die Anzahl der Scheiben von der
	 * Anzahl der Threads unterscheiden. Bei Deaktivierung wird die Anzahl an
	 * Scheiben mit der Anzahl an Threads gleich gesetzt. Das bedeutet, dass
	 * nicht mehr Threads als Zellen auf der X-Achse initialisiert werden (auch
	 * wenn die Größe eines finiten Elementes > 2 ist).
	 */
	public static void createComputingServices() {
		int start = 1;
		int end;
		// Je nach Thread Variante die Unterteilung vornehmen
		if (InitializeParameter.THREAD_POOL) {
			SharedVariables.numberOfAreas = InitializeParameter.NUMBER_OF_DATA_AREAS_THREADPOOL;
		} else {
			SharedVariables.numberOfAreas = InitializeParameter.NUMBER_OF_THREADS;
		}

		if (SharedVariables.numberOfAreas > InitializeParameter.QL) {
			SharedVariables.numberOfAreas = InitializeParameter.QL;
			System.out
					.println("Die Anzahl der Scheiben (NUMBER_OF_THREADS bzw. NUMBER_OF_DATA_AREAS_THREADPOOL) darf nicht größer als die QL sein");
		}

		int dataRangeQL = SharedVariables.QLR / SharedVariables.numberOfAreas;
		for (int i = 0; i < SharedVariables.numberOfAreas; i++) {
			// Automatische Aufteilung der Daten in Scheiben
			if (i < SharedVariables.numberOfAreas - 1) {
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
		for (int x = 0; x < SharedVariables.QLR; x++) {
			for (int y = 0; y < SharedVariables.QBR; y++) {
				float temperature = SharedVariables.u1[x][y][SharedVariables.Z_HALF];

				// Color Array initialisieren
				ColorService.computeAndSetColor(temperature, x, y);
			}
		}
	}

	/**
	 * Initialisierung des Thread-Pools mittels {@link ThreadPoolExecutor}
	 */
	public static void initializeThreadPool() {
		SharedVariables.executor = new ThreadPoolExecutor(
				InitializeParameter.NUMBER_OF_THREADS,
				InitializeParameter.NUMBER_OF_THREADS, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(
						InitializeParameter.NUMBER_OF_DATA_AREAS_THREADPOOL));
	}

}
