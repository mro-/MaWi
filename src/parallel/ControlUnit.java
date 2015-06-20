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
	 * 
	 */
	private void updateRTLSinus(int n) {
		if (SharedVariables.isu1Base) {
			float temp = SharedVariables.u2[SharedVariables.QLR / 2][0][SharedVariables.QHR / 2]
					+ Math.round((float) Math.sin(n) * 100);

			for (int x = 1; x < SharedVariables.QLR - 1; x++) {
				for (int z = 1; z < SharedVariables.QBR - 1; z++) {
					SharedVariables.u1[x][0][z] = temp;
				}
			}
		} else {
			float temp = SharedVariables.u1[SharedVariables.QLR / 2][0][SharedVariables.QHR / 2]
					+ Math.round((float) Math.sin(n) * 100);

			System.out.println(temp);
			for (int x = 1; x < SharedVariables.QLR - 1; x++) {
				for (int z = 1; z < SharedVariables.QBR - 1; z++) {
					SharedVariables.u2[x][0][z] = temp;
				}
			}
		}
	}
}
