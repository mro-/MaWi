package parallel.computing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import javafx.application.Platform;
import parallel.InitializeParameter;
import parallel.init.InitializeServices;
import parallel.init.SharedVariables;
import parallel.visualization.ColorService;
import parallel.visualization.OutputJFX;

/**
 * Main Runnable, dass für die Aktualisierung des Ausgabefensters zuständig ist.
 * Alle weiteren Threads werden von hier angestoßen.
 */
public class ControlUnit implements Runnable {

	@Override
	public void run() {
		float computingTime = 0;
		// Iterationsschritte durchführen
		for (int n = 1; n <= InitializeParameter.N; n++) {
			// Berechnung aller Quaderfelder (Rand wird nicht verändert)
			// Parallele Berechnungszeit messen
			long computingStartTime = System.currentTimeMillis();
			if (InitializeParameter.THREAD_POOL) {
				// Mittels Thread-Pool
				runThreadsWithPool(SharedVariables.executor);
			} else {
				// Mit einzelnen Threads
				createAndRunThreadsWithoutPool();
			}
			long computingEndTime = System.currentTimeMillis();
			computingTime += (computingEndTime - computingStartTime);

			boolean lastStepVisualization = !InitializeParameter.VISUALIZATION
					&& n == InitializeParameter.N;
			// Sollen die Zwischenschritte visualisiert werden? Ansonsten wird
			// nur noch die letzte Ausgabe visualisiert.
			if (InitializeParameter.VISUALIZATION || lastStepVisualization) {
				// Berechnung der Farben für den letzten Schritt
				if (lastStepVisualization) {
					InitializeServices.initializeColorArray();
				}

				// Fläche neu einfärben
				final FutureTask<Void> updateOutputWindowTask = new FutureTask<Void>(
						new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								OutputJFX.updatePixelInView();
								return null;
							}
						});
				Platform.runLater(updateOutputWindowTask);
				// Warten bis zeichnen fertig ist und erst dann weiter machen
				try {
					updateOutputWindowTask.get();
				} catch (InterruptedException | ExecutionException e1) {
					e1.printStackTrace();
				}
			}

			// Merker setzen, welches Array die Ausgangslage für den
			// nächsten Iterationsschritt enthält
			if (SharedVariables.isu1Base) {
				SharedVariables.isu1Base = false;
			} else {
				SharedVariables.isu1Base = true;
			}

			// Randtemperatur der linken Seite aktualisieren, falls Temperatur
			// sich sinusförmige ändern soll
			if (InitializeParameter.HEAT_MODE == 3) {
				updateRTLSinus(n);
			}
		}
		// Berechnungszeit ausrechnen
		computingTime = computingTime / 1000f;
		System.out.println("Berechnungszeit: " + computingTime);

		// Gesamtzeit messen
		long endTime = System.currentTimeMillis();
		float time = (endTime - SharedVariables.startTime) / 1000f;
		System.out.println("Gesamtzeit: " + time);
	}

	/**
	 * Erzeugt Threads ohne die Nutzung eines Thread-Poolings.
	 */
	private void createAndRunThreadsWithoutPool() {
		// Threads erzeugen
		Thread[] threads = new Thread[InitializeParameter.NUMBER_OF_THREADS];
		for (int i = 0; i < InitializeParameter.NUMBER_OF_THREADS; i++) {
			threads[i] = new Thread(SharedVariables.computingRunnable[i]);
			threads[i].start();
		}

		// Synchronisation der Threads
		for (int i = 0; i < InitializeParameter.NUMBER_OF_THREADS; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Erzeugt Threads mitteln Nutzung eines Thread-Pools.
	 */
	private void runThreadsWithPool(ThreadPoolExecutor executor) {
		List<Future<Void>> futures = new ArrayList<Future<Void>>(
				InitializeParameter.NUMBER_OF_DATA_AREAS_THREADPOOL);
		// threads aus Thread-Pool starten
		for (int i = 0; i < InitializeParameter.NUMBER_OF_DATA_AREAS_THREADPOOL; i++) {
			futures.add(executor.submit(SharedVariables.computingCallable[i]));
		}
		// Nötig, damit erst weiter gemacht wird wenn alle Threads fertig sein
		// (join).
		for (Future<Void> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Aktualisiert die Temperaturen der linken Seite gemäß der Sinusfunktion. <br>
	 */
	private void updateRTLSinus(int n) {
		// n als double, damit mit Nachkommastellen gerechnet wird
		float sinusvalue = ((float) n) / 3;
		// Berechnung der Temeratur mittels Sinus, Temperatur schwankt um die
		// angegebenene Ausgangstemperatur der linken Seite
		float newSinusTemperature = (float) ((Math.sin(sinusvalue) * 100) + InitializeParameter.RTL);

		// Neue Temperatur im Quader der linken Seite zuordnen
		if (SharedVariables.isu1Base) {
			for (int x = 0; x < SharedVariables.QLR; x++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					SharedVariables.u1[x][0][z] = newSinusTemperature;
				}
			}
		} else {
			for (int x = 0; x < SharedVariables.QLR; x++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					SharedVariables.u2[x][0][z] = newSinusTemperature;
				}
			}
		}

		// Color Array: linke Seite updaten
		for (int x = 0; x < SharedVariables.QLR; x++) {
			ColorService.computeAndSetColor(newSinusTemperature, x, 0);
		}
	}
}
