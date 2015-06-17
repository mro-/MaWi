package parallel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Eigener Task der benötigt wird um JavaFX GUI von verschiedenen Threads aus
 * upzudaten.
 */
public class MyJFXTask extends Task<Void> {

	@Override
	public Void call() {

		// Verfügbare Prozessoren
		int numberOfProcessors = Runtime.getRuntime().availableProcessors();

		// Startzeit messen
		long startTime = System.currentTimeMillis();

		// Thread-Pooling
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				InitializeParameter.NUMBER_OF_THREADS,
				InitializeParameter.NUMBER_OF_THREADS, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(100));

		for (int n = 1; n <= InitializeParameter.N; n++) {
			// Berechnung aller Quaderfelder (Rand wird nicht verändert)
			createAndRunThreadsWithoutPool();

			// TODO ThreadPooling
			// for (int i = 0; i <
			// InitializeParameter.NUMBER_OF_THREADS; i++) {
			// executor.execute(SharedVariables.computingServices[i]);
			// }
			// try {
			// executor.awaitTermination(1, TimeUnit.SECONDS);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// executor.shutdown();

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

		return null;

	}

	/**
	 * Erzeugt Threads ohne die Nutzung eines Thread-Poolings
	 */
	private void createAndRunThreadsWithoutPool() {
		// Threads erzeugen
		Thread[] threads = new Thread[InitializeParameter.NUMBER_OF_THREADS];
		for (int i = 0; i < InitializeParameter.NUMBER_OF_THREADS; i++) {
			threads[i] = new Thread(SharedVariables.computingServices[i]);
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
	 * Aktualisiert die Temperaturen der linken Seite gemäß der Sinusfunktion.
	 */
	private void updateRTLSinus(int n) {
		for (int x = 0; x < SharedVariables.QLR; x++) {
			for (int z = 0; z < SharedVariables.QBR; z++) {
				// System.out.println("Before: " +
				// SharedVariables.u2[x][0][z]);
				// TODO SharedVariables.isu1Base muss abgefragt werden
				SharedVariables.u1[x][0][z] = (float) (SharedVariables.u1[x][0][z] + Math
						.sin(n) * 10);
				SharedVariables.u2[x][0][z] = (float) (SharedVariables.u2[x][0][z] + Math
						.sin(n) * 10);
				// System.out.println(SharedVariables.u2[x][0][z]);
			}
		}
		// TODO ColorArray für die Werte neu berechnen
	}

}
