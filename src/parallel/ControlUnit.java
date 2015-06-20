package parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Main Runnable, dass für die Aktualisierung des Ausgabefensters zuständig ist.
 * Alle weiteren Threads werden von hier angestoßen.
 */
public class ControlUnit implements Runnable {

	@Override
	public void run() {
		// Verfügbare Prozessoren
		int numberOfProcessors = Runtime.getRuntime().availableProcessors();

		// Startzeit messen
		long startTime = System.currentTimeMillis();

		// Thread-Pooling
		ThreadPoolExecutor executor;
		if (InitializeParameter.THREAD_POOL) {
			executor = new ThreadPoolExecutor(numberOfProcessors,
					InitializeParameter.NUMBER_OF_THREADS, 0, TimeUnit.SECONDS,
					new ArrayBlockingQueue<>(
							InitializeParameter.NUMBER_OF_THREADS));
		}

		// Iterationsschritte durchführen
		for (int n = 1; n <= InitializeParameter.N; n++) {
			// Berechnung aller Quaderfelder (Rand wird nicht verändert)
			if (InitializeParameter.THREAD_POOL) {
				// Mittels Thread-Pool
				createAndRunThreadsWithPool(executor);
			} else {
				// Mit einzelnen Threads
				createAndRunThreadsWithoutPool();
			}

			// Fläche neu einfärben
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					OutputJFX.updatePixelInView();
				}
			});

			// Randtemperatur der linken Seite aktualisieren
			if (InitializeParameter.HEAT_MODE == 3) {
				updateRTLSinus(n);
			}

			// Merker setzen, welches Array die Ausgangslage für den
			// nächsten Iterationsschritt enthält
			if (SharedVariables.isu1Base) {
				SharedVariables.isu1Base = false;
			} else {
				SharedVariables.isu1Base = true;
			}
		}
		// Endzeit messen
		long endTime = System.currentTimeMillis();
		float time = (endTime - startTime) / 1000.0f;
		System.out.println("Zeit: " + time);

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
	private void createAndRunThreadsWithPool(ThreadPoolExecutor executor) {
		List<Future<Void>> futures = new ArrayList<Future<Void>>(
				InitializeParameter.NUMBER_OF_THREADS);
		for (int i = 0; i < InitializeParameter.NUMBER_OF_THREADS; i++) {
			futures.add(executor.submit(SharedVariables.computingCallable[i]));
		}
		for (Future<Void> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Aktualisiert die Temperaturen der linken Seite gemäß der Sinusfunktion. <br>
	 * FIXME: Kann hier nicht auch mit Array-Referenzen gearbeitet werden, um
	 * nicht immer auf isu1Base abzufragen und den Körper nur für ein Array
	 * ausprogrammieren zu müssen? Nur als Idee <br>
	 * FIXME Prüfen: Temperatur für eine Zelle berechnen und dann auf alle
	 * Zellen anwenden. Müsste ja über die Fäche konstant sein
	 * 
	 */
	private void updateRTLSinus(int n) {
		for (int x = 1; x < SharedVariables.QLR - 1; x++) {
			for (int z = 1; z < SharedVariables.QBR - 1; z++) {
				if (SharedVariables.isu1Base) {
					SharedVariables.u1[x][0][z] = (SharedVariables.u1[x][0][z] + Math
							.round((float) Math.sin(n) * 100));
					OutputJFX.computeAndSetColor(SharedVariables.u1[x][0][z],
							x, 0);
				} else {
					SharedVariables.u2[x][0][z] = (SharedVariables.u2[x][0][z] + Math
							.round((float) Math.sin(n) * 100));
					OutputJFX.computeAndSetColor(SharedVariables.u2[x][0][z],
							x, 0);
				}
			}
		}
		// System.out.println(SharedVariables.u2[5][0][5]);
		// FIXME Für eine Spalte werden die Werte nicht korrekt dargestellt,
		// wenn man als Ausgangstemparatur für RTL
		// niedrige Temparaturen berechnet. Evtl. eine Optimierung vornehmen,
		// dass die Farben des gesamten Arrays
		// gebündelt neu berechnet werden und nicht für jedes Feld einzeln
	}

}
