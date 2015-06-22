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
	 * Erzeugt die Berechnungsservices und teilt dazu den Quader in Scheiben
	 * auf.
	 */
	private void createComputingServices() {
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
	 * Initialiserung des Quaders mit Randtemperaturen und Starttemperatur.
	 */
	public void initializeQuader() {

		for (int x = 0; x < SharedVariables.QLR; x++) {
			for (int y = 0; y < SharedVariables.QBR; y++) {
				for (int z = 0; z < SharedVariables.QHR; z++) {

					// Linke Seite mit Randtemperatur initialisieren
					if (y == 0) {
						SharedVariables.u1[x][y][z] = getTemperatureForHeatMode(
								x, z);
						SharedVariables.u2[x][y][z] = getTemperatureForHeatMode(
								x, z);

						// Rechte Seite mit Randtemperatur initialisieren
					} else if (y == SharedVariables.QBR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTR;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTR;

						// Hintere Seite mit Randtemperatur initialisieren
					} else if (x == 0) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTH;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTH;

						// Vordere Seite mit Randtemperatur initialisieren
					} else if (x == SharedVariables.QLR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTV;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTV;

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
				}
			}
		}

		// Temperaturen der linken Seite berechnen, falls Wärmequelle in der
		// Mitte ist
		if (InitializeParameter.HEAT_MODE == 2) {
			updateRTlinksForCentralHeatMode();
		}
	}

	/**
	 * Berechnung der Temperaturen der linken Seite, wenn die Wärmequelle in der
	 * Mitte ist. Berechnung geht kreisförmig vor. Die Randtemperatur wird an
	 * den vier Seiten gleich der Durchschnittstemperatur der Ränder gesetzt.
	 */
	private void updateRTlinksForCentralHeatMode() {
		// Ausdehnung der Hitzequelle in der Mitte
		int xStartIndex;
		int xEndIndex;
		int zStartIndex;
		int zEndIndex;

		// Ist die Kante der x-Länge oder z-Höhe länger?
		boolean xLenghtGreaterThanZ = SharedVariables.QLR > SharedVariables.QHR;

		// Ausdehnung der Hitzequelle bestimmen. Variiert je nach Länge der
		// Seiten von 2x2 bis 3x3 Quaderzellen.
		// Quaderlänge (x-Achse) gerade -> Hitzequelle 2 Zellen breit
		if (SharedVariables.QLR % 2 == 0) {
			xEndIndex = SharedVariables.QLR / 2;
			xStartIndex = xEndIndex - 1;
			// Quaderlänge (x-Achse) ungerade -> Hitzequelle 3 Zellen breit
		} else {
			xEndIndex = ((SharedVariables.QLR - 1) / 2) + 1;
			xStartIndex = xEndIndex - 2;
		}
		// Quaderhöhe (z-Achse) gerade -> Hitzequelle 2 Zellen hoch
		if (SharedVariables.QHR % 2 == 0) {
			zEndIndex = SharedVariables.QHR / 2;
			zStartIndex = zEndIndex - 1;
			// Quaderhöhe (z-Achse) ungerade -> Hitzequelle 3 Zellen hoch
		} else {
			zEndIndex = ((SharedVariables.QHR - 1) / 2) + 1;
			zStartIndex = zEndIndex - 2;
		}

		// Wärmequelle in der Mitte der initialisieren
		for (int x = xStartIndex; x <= xEndIndex; x++) {
			for (int z = zStartIndex; z <= zEndIndex; z++) {
				SharedVariables.u1[x][0][z] = InitializeParameter.RTL;
				SharedVariables.u2[x][0][z] = InitializeParameter.RTL;
			}
		}

		// Faktor berechnen um den die Temperatur von der Mitte zum Rand linear
		// abnimmt (von der längeren Seite ausgehen).
		float factorPerCell;
		if (xLenghtGreaterThanZ) {
			factorPerCell = (InitializeParameter.RTL - SharedVariables.AVERAGE_BORDER_TEMP_LEFT_SIDE)
					/ (SharedVariables.QLR / 2);
		} else {
			factorPerCell = (InitializeParameter.RTL - SharedVariables.AVERAGE_BORDER_TEMP_LEFT_SIDE)
					/ (SharedVariables.QHR / 2);
		}

		// Kreis um eine Einheit vergrößern
		xStartIndex--;
		xEndIndex++;
		zStartIndex--;
		zEndIndex++;

		// Weitere Ausdehnung von der Mitte ausgehend berechnen (kreisförmig,
		// quadratisch).
		int counter;
		for (counter = 1; xStartIndex > 0 && zStartIndex > 0
				&& xEndIndex < SharedVariables.QLR - 1
				&& zEndIndex < SharedVariables.QHR - 1; counter++) {

			// nach links und rechts, ausgehen von der Mitte Temperaturen
			// berechnen
			for (int x = xStartIndex; x <= xEndIndex; x++) {
				for (int z = zStartIndex; z <= zEndIndex; z++) {
					if (x == xStartIndex || x == xEndIndex) {
						SharedVariables.u1[x][0][z] = InitializeParameter.RTL
								- (counter * factorPerCell);
						SharedVariables.u2[x][0][z] = InitializeParameter.RTL
								- (counter * factorPerCell);
					}
				}
			}
			// nach oben und unten, ausgehen von der Mitte Temperaturen
			// berechnen
			for (int z = zStartIndex; z <= zEndIndex; z++) {
				for (int x = xStartIndex; x <= xEndIndex; x++) {
					if (z == zStartIndex || z == zEndIndex) {
						SharedVariables.u1[x][0][z] = InitializeParameter.RTL
								- (counter * factorPerCell);
						SharedVariables.u2[x][0][z] = InitializeParameter.RTL
								- (counter * factorPerCell);
					}
				}
			}

			// Kreis um eine Einheit vergrößern
			xStartIndex--;
			xEndIndex++;
			zStartIndex--;
			zEndIndex++;
		}

		// Wenn die linke Seite rechteckig ist, müssen darüber hinaus noch
		// weitere Initialisierungen vorgenommen werden für die restlichen
		// Flächen (Temperatur weiterhin linear absteigend zum Rand).
		int startCounter = counter;
		if (xLenghtGreaterThanZ) {
			// Bereich links der Mitte
			for (int x = xStartIndex; x > 0; x--) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					SharedVariables.u1[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
					SharedVariables.u2[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
				}
				counter++;
			}
			counter = startCounter;
			// Bereich rechts der Mitte
			for (int x = xEndIndex; x < SharedVariables.QLR - 1; x++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					SharedVariables.u1[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
					SharedVariables.u2[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
				}
				counter++;
			}
		} else {
			// Bereich unterhalb der Mitte
			for (int z = zStartIndex; z > 0; z--) {
				for (int x = 1; x < SharedVariables.QLR - 1; x++) {
					SharedVariables.u1[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
					SharedVariables.u2[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
				}
				counter++;
			}
			counter = startCounter;
			// Bereich oberhalb der Mitte
			for (int z = zEndIndex; z < SharedVariables.QHR - 1; z++) {
				for (int x = 1; x < SharedVariables.QLR - 1; x++) {
					SharedVariables.u1[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
					SharedVariables.u2[x][0][z] = InitializeParameter.RTL
							- (counter * factorPerCell);
				}
				counter++;
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
			// Die Fläche wird mit der durchschnittlichen Randtemperatur
			// initialisiert. So muss später nicht noch mal extra der Außenrand
			// der linken Seite initialisiert werden.
			return SharedVariables.AVERAGE_BORDER_TEMP_LEFT_SIDE;
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
		// FIXME Scrollbar machen
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
