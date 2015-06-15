package parallel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Visualisierung der Wärmediffusion als 2D Querschnitt des Quaders.
 */
public class OutputJFX extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeQuader();
		createComputingServices();

		primaryStage.setTitle("Simulation Wärmediffusion");
		BorderPane root = new BorderPane();
		// TODO Scrollbar machen
		// 20 Pixel Außenrand
		Scene scene = new Scene(root, SharedVariables.QBR_2D + 20,
				SharedVariables.QLR_2D + 20, Color.WHITE);

		// Image und dessen PixelWriter ist die performanteste Methode um in
		// JavaFX Pixel darzustellen
		ImageView imageView = new ImageView();
		WritableImage image = new WritableImage(SharedVariables.QBR_2D,
				SharedVariables.QLR_2D);
		imageView.setImage(image);
		PixelWriter pixelWriter = image.getPixelWriter();

		// 1 Zelle wird als Quadrat von CELL_WIDTH Pixel visualisiert
		// Initialisierung der Fläche
		for (int x = 0; x < SharedVariables.QLR_2D; x = x
				+ InitializeParameter.CELL_WIDTH) {
			for (int y = 0; y < SharedVariables.QBR_2D; y = y
					+ InitializeParameter.CELL_WIDTH) {

				// Farbe berechnen
				Color color = computeColor(SharedVariables.u1[x
						/ InitializeParameter.CELL_WIDTH][y
						/ InitializeParameter.CELL_WIDTH][SharedVariables.Z_HALF]);

				// Color Array initialisieren
				SharedVariables.tempInColor[x / InitializeParameter.CELL_WIDTH][y
						/ InitializeParameter.CELL_WIDTH] = color;

				// Zellen können mehrere Pixel groß sein
				for (int i = x; i < x + InitializeParameter.CELL_WIDTH; i++) {
					for (int j = y; j < y + InitializeParameter.CELL_WIDTH; j++) {
						// Erst y dann x, damit linke Seite links dargestellt
						// wird
						pixelWriter.setColor(j, i, color);
					}
				}

			}
		}

		// // Farbskala ausgeben
		// for (int i = 0; i < 11; i++) {
		// Rectangle r = new Rectangle();
		// r.setWidth(10);
		// r.setHeight(10);
		// r.setFill(SharedVariables.COLORS[i]);
		// gridpane.add(r, i, InitializeParameter.QL + 3);
		// }

		root.setCenter(imageView);

		primaryStage.setScene(scene);
		primaryStage.show();

		// Verfügbare Prozessoren
		int numberOfProcessors = Runtime.getRuntime().availableProcessors();

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				// Startzeit messen
				long startTime = System.currentTimeMillis();

				// Thread-Pooling
				ThreadPoolExecutor executor = new ThreadPoolExecutor(
						InitializeParameter.NUMBER_OF_THREADS,
						InitializeParameter.NUMBER_OF_THREADS, 0,
						TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

				for (int n = 1; n <= InitializeParameter.N; n++) {
					// Berechnung aller Quaderfelder (Rand wird nicht verändert)

					createThreadsWithoutPool();

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
							// ImageView imageView2 = (ImageView) root
							// .getChildren().get(0);
							// WritableImage image2 = (WritableImage) imageView2
							// .getImage();
							// PixelWriter pixelWriter2 =
							// image2.getPixelWriter();

							// Ausgabe, alle Felder berücksichtigen
							// und Pixel entsprechend der Zellgröße anpassen
							for (int x = 0; x < SharedVariables.QLR_2D; x = x
									+ InitializeParameter.CELL_WIDTH) {
								for (int y = 0; y < SharedVariables.QBR_2D; y = y
										+ InitializeParameter.CELL_WIDTH) {

									// Farbe berechnen falls tempInColor Array
									// nicht funktioniert
									// float temperature;
									// if (SharedVariables.isu1Base) {
									// temperature = SharedVariables.u2[x / 4][y
									// / 4][SharedVariables.Z_HALF];
									// } else {
									// temperature = SharedVariables.u1[x / 4][y
									// / 4][SharedVariables.Z_HALF];
									// }
									// Color color = computeColor(temperature);

									Color color = SharedVariables.tempInColor[x
											/ InitializeParameter.CELL_WIDTH][y
											/ InitializeParameter.CELL_WIDTH];

									for (int i = x; i < x
											+ InitializeParameter.CELL_WIDTH; i++) {
										for (int j = y; j < y
												+ InitializeParameter.CELL_WIDTH; j++) {
											pixelWriter.setColor(j, i, color);
										}
									}
								}
							}

						}
					});

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

		};

		new Thread(task).start();

	}

	/**
	 * Erzeugt Threads ohne die Nutzung eines Thread-Poolings
	 */
	private void createThreadsWithoutPool() {
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
	 * Erzeugt die Berechnungsservices und teilt dazu den Quader in Scheiben
	 * auf.
	 */
	private void createComputingServices() {
		int start = 1;
		int end;
		int dataRangeQL = SharedVariables.QLR
				/ InitializeParameter.NUMBER_OF_THREADS;
		for (int i = 0; i < InitializeParameter.NUMBER_OF_THREADS; i++) {
			// Automatische Aufteilung der Daten (in Scheiben)
			if (i < InitializeParameter.NUMBER_OF_THREADS - 1) {
				end = start + dataRangeQL;
			} else {
				end = SharedVariables.QLR - 1;
			}
			SharedVariables.computingServices[i] = new ComputingAlgorithmus(
					start, end);
			start = end;
		}
	}

	/**
	 * Berechnung des Farbwertes
	 */
	private Color computeColor(float temperature) {
		// Temperatur auf 0-1 Skala mappen
		// (value-min)/(max-min)
		float mappedTemperatureF = (temperature - InitializeParameter.MIN_TEMP)
				/ (InitializeParameter.MAX_TEMP - InitializeParameter.MIN_TEMP);
		mappedTemperatureF = (mappedTemperatureF < 0) ? 0
				: ((mappedTemperatureF > 1) ? 1 : mappedTemperatureF);

		// Mappen auf 0-10 Skala
		int mappedTemperatureI = Math.round(mappedTemperatureF * 10);

		return SharedVariables.COLORS[mappedTemperatureI];
	}

	/**
	 * Alternative Farbberechnung (fließender Übergang)
	 */
	private Color computeColor2(float temperature) {
		// 0-240
		// Farben von 0-360 verfügbar
		return Color.hsb(temperature, 1, 1);
	}

	/**
	 * Initialiserung des Quaders mit Randtemperaturen und Starttemperatur.
	 */
	public void initializeQuader() {
		for (int x = 0; x < SharedVariables.QLR; x++) {
			for (int y = 0; y < SharedVariables.QBR; y++) {
				for (int z = 0; z < SharedVariables.QHR; z++) {

					// Hintere Seite mit Randtemperatur initialisieren
					if (x == 0) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTH;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTH;

						// Vordere Seite mit Randtemperatur initialisieren
					} else if (x == SharedVariables.QLR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTV;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTV;

						// Rechte Seite mit Randtemperatur initialisieren
					} else if (y == SharedVariables.QBR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTR;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTR;

						// Linke Seite mit Randtemperatur initialisieren
						// (konstate Temperatur über die gesamte Seite)
						// TODO weitere Aufgaben implementieren
					} else if (y == 0) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTL;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTL;

						// Untere Seite mit Randtemperatur initialisieren
					} else if (z == 0) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTU;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTU;

						// Obere Seite mit Randtemperatur initialisieren
					} else if (z == SharedVariables.QHR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTO;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTO;

						// Quader mit Starttemperatur initialisieren
					} else {
						SharedVariables.u1[x][y][z] = InitializeParameter.TS;
						SharedVariables.u2[x][y][z] = InitializeParameter.TS;
					}
					// System.out.print(SharedVariables_3D.u1[x][y][z] + " ");
				}
				// System.out.print("\n");
			}
			// System.out.println();
		}
	}
}
