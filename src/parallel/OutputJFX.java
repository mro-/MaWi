package parallel;

import javafx.application.Application;
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

	private static PixelWriter pixelWriter;

	public static void main(String[] args) {
		Application.launch(args);
	}

	private int partials;

	@Override
	public void start(Stage primaryStage) {
		// Ausgabefenster initialisieren
		initializeOutputWindow(primaryStage);

		// Initialisierung des Quaders
		initializeQuader();

		// Initialisierung des Farbarrays
		initializeColorArray();

		// Berechnungsservices anlegen
		createComputingServices();

		// Fläche einfärben
		updatePixelInView();

		// Ausgabefenster anzeigen
		primaryStage.show();

		// Ausgabefenster aktualisieren
		Thread mainThread = new Thread(new ControlUnit());
		mainThread.start();
	}

	/**
	 * Einfärbung der angezeigten Fläche, anhand der Farben des Color Arrays. <br>
	 * 
	 * 1 Zelle wird als Quadrat von CELL_WIDTH Pixel visualisiert
	 */
	public static void updatePixelInView() {
		// Ausgabe, alle Felder berücksichtigen (inklusive Rand)
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

				// und Pixel entsprechend der Zellgröße anpassen
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
	}

	/**
	 * Berechnung des Farbwertes und schreiben in das Array, das die Farbwerte
	 * für die 2D Darstellung enthält. <br>
	 * FIXME: Ich habe die Methode von private zu public static geändert, um aus
	 * der ControlUnit darauf zugreifen zu können. Spricht was dagegen? <br>
	 */
	public static void computeAndSetColor(float temperature, int x, int y) {
		// Temperatur auf 0-1 Skala mappen
		// (value-min)/(max-min)
		float mappedTemperatureF = (temperature - InitializeParameter.MIN_TEMP)
				/ (InitializeParameter.MAX_TEMP - InitializeParameter.MIN_TEMP);
		mappedTemperatureF = (mappedTemperatureF < 0) ? 0
				: ((mappedTemperatureF > 1) ? 1 : mappedTemperatureF);

		// Mappen auf 0-10 Skala
		int mappedTemperatureI = Math.round(mappedTemperatureF * 10);

		// synchronized Block eingetlich nicht nötig, da nicht in gleiche Felder
		// geschrieben wird und das lesen erst nach dem Schreiben stattfindet.
		synchronized (SharedVariables.tempInColor) {
			SharedVariables.tempInColor[x][y] = SharedVariables.COLORS[mappedTemperatureI];
		}
	}

	/**
	 * Alternative Farbberechnung (fließender Übergang)
	 */
	private void computeAndSetColor2(float temperature, int x, int y) {
		SharedVariables.tempInColor[x][y] = Color.hsb(temperature, 1, 1);
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
	 * Initialiserung des Quaders mit Randtemperaturen und Starttemperatur.
	 * FIXME: Für die zentrale Wärmequelle müssen hier alle Randtemparaturen
	 * noch gleichgesetzt werden
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
					} else if (y == 0) {
						SharedVariables.u1[x][y][z] = getTemperatureForHeatMode(
								x, z);
						SharedVariables.u2[x][y][z] = getTemperatureForHeatMode(
								x, z);
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

		switch (InitializeParameter.HEAT_MODE) {
		case 2:
			updateRTlinksForCentralHeatMode();
			// calcCentralHeat();
		}
	}

	/**
	 * RTlinks berechnen für die zentrale Hitzequelle mit absteigenden
	 * Temperaturen <br>
	 * Idee: Temperatur um den Kern herum vergrößern <br>
	 * FIXME Running with errors!
	 * 
	 * @deprecated
	 */
	private void calcCentralHeat() {
		// Zentrale Wärmequelle setzen
		setCentreHeatSourceOnRTlinks();

		// Breitere Seite ermitteln
		boolean xIsWider = true;
		if (SharedVariables.QHR > SharedVariables.QLR) {
			xIsWider = false;
		}

		// Betriebsparameter für schmale und breite Seite ermitteln
		int wideHalf = SharedVariables.X_HALF;
		int wideLength = SharedVariables.QLR;
		int narrowHalf = SharedVariables.Z_HALF;
		int narrowLength = SharedVariables.QHR;
		if (!xIsWider) {
			wideHalf = SharedVariables.Z_HALF;
			wideLength = SharedVariables.QHR;
			narrowHalf = SharedVariables.X_HALF;
			narrowLength = SharedVariables.QLR;
		}
		// Betriebsparameter für schmale und breite Seite ermitteln
		int upperSideWideCore = wideHalf
				+ (InitializeParameter.CENTRE_SIZE / 2);
		int lowerSideWideCore = wideHalf
				- (InitializeParameter.CENTRE_SIZE / 2);
		int upperSideNarrowCore = narrowHalf
				+ (InitializeParameter.CENTRE_SIZE / 2);
		int lowerSideNarrowCore = narrowHalf
				- (InitializeParameter.CENTRE_SIZE / 2);

		float newTemp = InitializeParameter.RTL
				- (InitializeParameter.RTL * 0.01f);

		// Von der Mitte nach links gehen
		for (int wideToLeft = lowerSideWideCore - 1; wideToLeft > 0; wideToLeft--) {
			// Von der Mitte nach oben und unten gehen
			for (int narrowToTop = upperSideNarrowCore; narrowToTop > lowerSideNarrowCore; narrowToTop--) {
				updateTemp(wideToLeft, narrowToTop, newTemp, xIsWider);
			}
			newTemp = newTemp - (InitializeParameter.RTL * 0.01f);
			// Core vergrößern
			upperSideNarrowCore++;
			lowerSideNarrowCore--;
		}

		// Zurücksetzen der uppeSideNarrow und lowerSideNarrow
		upperSideNarrowCore = narrowHalf
				+ (InitializeParameter.CENTRE_SIZE / 2);
		lowerSideNarrowCore = narrowHalf
				- (InitializeParameter.CENTRE_SIZE / 2);
		newTemp = InitializeParameter.RTL - (InitializeParameter.RTL * 0.01f);

		// Von der Mitte nach rechts gehen
		for (int wideToRight = upperSideWideCore; wideToRight < wideLength; wideToRight++) {
			// Von der Mitte nach oben und unten gehen
			for (int narrowToTop = upperSideNarrowCore - 1; narrowToTop > lowerSideNarrowCore; narrowToTop--) {
				updateTemp(wideToRight, narrowToTop, newTemp, xIsWider);
			}
			newTemp = newTemp - (InitializeParameter.RTL * 0.01f);
			// Core vergrößern
			upperSideNarrowCore++;
			lowerSideNarrowCore--;
		}
	}

	/**
	 * Aktualisiert die Temparatur in Abhängigkeit der breiteren und schmaleren
	 * Zeite
	 * 
	 * @param wide
	 * @param narrow
	 * @param newTemp
	 * @param xIsWide
	 * @deprecated
	 */
	private void updateTemp(int wide, int narrow, float newTemp, boolean xIsWide) {
		int x = wide, z = narrow;
		if (!xIsWide) {
			x = narrow;
			z = wide;
		}
		SharedVariables.u2[x][0][z] = newTemp;
		SharedVariables.u1[x][0][z] = newTemp;
		System.out.println(SharedVariables.u1[x][0][z]);
	}

	/**
	 * Initialisiert die zentrale Wärmequelle
	 * 
	 * @deprecated
	 */
	private void setCentreHeatSourceOnRTlinks() {
		for (int x = SharedVariables.X_HALF
				- (InitializeParameter.CENTRE_SIZE / 2); x < SharedVariables.X_HALF
				+ (InitializeParameter.CENTRE_SIZE / 2); x++) {
			// Z-Werte von der Array-Mitte um halbe Zetrum-Größe nach oben
			// und unten ablaufen
			for (int z = SharedVariables.Z_HALF
					- (InitializeParameter.CENTRE_SIZE / 2); z < SharedVariables.Z_HALF
					+ (InitializeParameter.CENTRE_SIZE / 2); z++) {
				// In der Mitte die heißeste Stelle setzen
				SharedVariables.u2[x][0][z] = InitializeParameter.RTL;
				SharedVariables.u1[x][0][z] = InitializeParameter.RTL;
			}
		}
	}

	/**
	 * Berechnung der Randtemperaturen bei zentraler Wärmequelle <br>
	 * RTlinks verändet sich bei zentrale Wärmequelle über die Zeit nicht <br>
	 * 
	 * FIXME Verschiedene Quaderlängen berücksichtigen
	 * 
	 */
	private void updateRTlinksForCentralHeatMode() {
		// Temperaturen bestimmen
		float tempZone1 = InitializeParameter.RTL * 0.15f;
		float tempZone2 = InitializeParameter.RTL * 0.2f;
		float tempZone3 = InitializeParameter.RTL * 0.3f;
		float tempZone4 = InitializeParameter.RTL * 0.4f;

		// Sicherheitsabfrage
		if (SharedVariables.Z_HALF > InitializeParameter.CENTRE_SIZE
				&& SharedVariables.X_HALF > InitializeParameter.CENTRE_SIZE) {
			/**
			 * Alte Berechnung
			 */
			// // X-Werte von der Array-Mitte um halbe Zetrum-Größe nach oben
			// und
			// // unten ablaufen
			// for (int x = SharedVariables.X_HALF
			// - InitializeParameter.CENTRE_SIZE / 2; x < SharedVariables.X_HALF
			// + InitializeParameter.CENTRE_SIZE / 2; x++) {
			// // Z-Werte von der Array-Mitte um halbe Zetrum-Größe nach oben
			// // und unten ablaufen
			// for (int z = SharedVariables.Z_HALF
			// - InitializeParameter.CENTRE_SIZE / 2; z < SharedVariables.Z_HALF
			// + InitializeParameter.CENTRE_SIZE / 2; z++) {
			// // In der Mitte die heißeste Stelle setzen
			// SharedVariables.u2[x][0][z] = InitializeParameter.RTL;
			// SharedVariables.u1[x][0][z] = InitializeParameter.RTL;
			// }
			// }

			/**
			 * Größe der Array-Bereiche bestimmen <br>
			 * Feste Anzahl von Ingesamt 10 Chunks, abzüglich des Zentrums
			 */
			int numOfFieldsWithoutCentreX = SharedVariables.QLR
					- InitializeParameter.CENTRE_SIZE - 2;
			int numOfChunksX = (int) Math
					.ceil((double) numOfFieldsWithoutCentreX / 8);
			int numOfFieldsWithoutCentreZ = SharedVariables.QHR
					- InitializeParameter.CENTRE_SIZE - 2;
			int numOfChunksZ = (int) Math
					.ceil((double) numOfFieldsWithoutCentreZ / 8);

			// Gesamtes Array durchlaufen
			for (int x = 1; x < SharedVariables.QLR - 1; x++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					// Oberen Bereich durchlaufen
					if ((x >= 0 && x < numOfChunksX)
							|| (z >= 0 && z < numOfChunksZ)) {
						SharedVariables.u2[x][0][z] = tempZone1;
						SharedVariables.u1[x][0][z] = tempZone1;
					} else if ((x >= numOfChunksX && x < numOfChunksX * 2)
							|| (z >= numOfChunksZ && z < numOfChunksZ * 2)) {
						SharedVariables.u2[x][0][z] = tempZone2;
						SharedVariables.u1[x][0][z] = tempZone2;
					} else if ((x >= numOfChunksX * 2 && x < numOfChunksX * 3)
							|| (z >= numOfChunksZ * 2 && z < numOfChunksZ * 3)) {
						SharedVariables.u2[x][0][z] = tempZone3;
						SharedVariables.u1[x][0][z] = tempZone3;
					} else if ((x >= numOfChunksX * 3 && x < numOfChunksX * 4)
							|| (z >= numOfChunksZ * 3 && z < numOfChunksZ * 4)) {
						SharedVariables.u2[x][0][z] = tempZone4;
						SharedVariables.u1[x][0][z] = tempZone4;
					}

					// Zentrum berechnen
					else if ((x >= SharedVariables.X_HALF
							- InitializeParameter.CENTRE_SIZE / 2 - 1 && x < SharedVariables.X_HALF
							+ InitializeParameter.CENTRE_SIZE / 2 + 1)
							&& (z >= SharedVariables.Z_HALF
									- InitializeParameter.CENTRE_SIZE / 2 - 1 && z < SharedVariables.Z_HALF
									+ InitializeParameter.CENTRE_SIZE / 2 + 1)) {
						SharedVariables.u2[x][0][z] = InitializeParameter.RTL;
						SharedVariables.u1[x][0][z] = InitializeParameter.RTL;
					}

					// Andere Seite berechnen
					else if ((x >= numOfChunksX * 4
							+ InitializeParameter.CENTRE_SIZE && x < numOfChunksX
							* 5 + InitializeParameter.CENTRE_SIZE + 2)
							|| (z >= numOfChunksZ * 4
									+ InitializeParameter.CENTRE_SIZE && z < numOfChunksZ
									* 5 + InitializeParameter.CENTRE_SIZE + 2)) {
						SharedVariables.u2[x][0][z] = tempZone4;
						SharedVariables.u1[x][0][z] = tempZone4;
					} else if ((x >= numOfChunksX * 5
							+ InitializeParameter.CENTRE_SIZE + 2 && x < numOfChunksX
							* 6 + InitializeParameter.CENTRE_SIZE + 2)
							|| (z >= numOfChunksZ * 5
									+ InitializeParameter.CENTRE_SIZE + 2 && z < numOfChunksZ
									* 6 + InitializeParameter.CENTRE_SIZE + 2)) {
						SharedVariables.u2[x][0][z] = tempZone3;
						SharedVariables.u1[x][0][z] = tempZone3;
					} else if ((x >= numOfChunksX * 6
							+ InitializeParameter.CENTRE_SIZE + 2 && x < numOfChunksX
							* 7 + InitializeParameter.CENTRE_SIZE + 2)
							|| (z >= numOfChunksZ * 6
									+ InitializeParameter.CENTRE_SIZE + 2 && z < numOfChunksZ
									* 7 + InitializeParameter.CENTRE_SIZE + 2)) {
						SharedVariables.u2[x][0][z] = tempZone2;
						SharedVariables.u1[x][0][z] = tempZone2;
					} else if ((x >= numOfChunksX * 7
							+ InitializeParameter.CENTRE_SIZE + 2 && x < numOfChunksX
							* 8 + InitializeParameter.CENTRE_SIZE)
							|| (z >= numOfChunksZ * 7
									+ InitializeParameter.CENTRE_SIZE + 2 && z < numOfChunksZ
									* 8 + InitializeParameter.CENTRE_SIZE)) {
						SharedVariables.u2[x][0][z] = tempZone1;
						SharedVariables.u1[x][0][z] = tempZone1;
					}

				}
				// System.out
				// .println(SharedVariables.u2[x][0][SharedVariables.Z_HALF]);

			}
			for (int z = 0; z < SharedVariables.QHR; z++) {
				System.out
						.println(SharedVariables.u1[SharedVariables.X_HALF][0][z]);
			}
		}
	}

	/**
	 * Initialisiert die linke Seite je nach Hitzequelle. <br>
	 * 1: Konstante Temperatur der gesamten Fläche <br>
	 * 2: Wärmequelle in der Mitte der Fläche <br>
	 * 3: Sinus
	 */
	private float getTemperatureForHeatMode(int x, int z) {
		switch (InitializeParameter.HEAT_MODE) {
		case 1:
			// Fläche entspricht ingesamt der linken Randtemperatur
			return InitializeParameter.RTL;
		case 2:
			// Wärmequelle ist in der Mitte
			return InitializeParameter.TS;
		case 3:
			// Fläche entspricht ingesamt der linken Randtemperatur
			return InitializeParameter.RTL;
		}
		return 0;
	}

	/**
	 * Initialisierung des Farbarrays anhand der vorgegebenen
	 * Initialisierungstemperaturen.
	 */
	private void initializeColorArray() {
		for (int x = 0; x < SharedVariables.QLR_2D; x = x
				+ InitializeParameter.CELL_WIDTH) {
			for (int y = 0; y < SharedVariables.QBR_2D; y = y
					+ InitializeParameter.CELL_WIDTH) {

				// Auf x und y Quaderzellen runterrechnen
				int xCell = x / InitializeParameter.CELL_WIDTH;
				int yCell = y / InitializeParameter.CELL_WIDTH;

				float temperature = SharedVariables.u1[xCell][yCell][SharedVariables.Z_HALF];

				// Color Array initialisieren
				computeAndSetColor(temperature, xCell, yCell);
			}
		}
	}

	/**
	 * Initialisiert das Ausgabefenster für die Simulation.
	 */
	private void initializeOutputWindow(Stage primaryStage) {
		primaryStage.setTitle("Simulation Wärmediffusion");
		BorderPane root = new BorderPane();
		// TODO Scrollbar machen
		// 20 Pixel Außenrand
		Scene scene = new Scene(root, SharedVariables.QBR_2D,
				SharedVariables.QLR_2D, Color.WHITE);

		// Image und dessen PixelWriter ist die performanteste Methode um in
		// JavaFX Pixel darzustellen
		ImageView imageView = new ImageView();
		WritableImage image = new WritableImage(SharedVariables.QBR_2D,
				SharedVariables.QLR_2D);
		imageView.setImage(image);
		pixelWriter = image.getPixelWriter();
		root.setCenter(imageView);
		primaryStage.setScene(scene);
	}
}
