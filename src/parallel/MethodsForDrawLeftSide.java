package parallel;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Visualisierung der linken Seite <br>
 * FIXME Klasse löschen, nachdem Screenshots gemacht wurden
 */
public class MethodsForDrawLeftSide {

	// SharedVariables
	public static Color[][] tempInColor = new Color[QLR][QHR];

	private static PixelWriter pixelWriter;

	/**
	 * Einfärbung der angezeigten Fläche, anhand der Farben des Color Arrays. <br>
	 * 
	 * 1 Zelle wird als Quadrat von CELL_WIDTH Pixel visualisiert
	 */
	public static void updatePixelInView() {
		// Ausgabe, alle Felder berücksichtigen (inklusive Rand)
		for (int x = 0; x < SharedVariables.QLR_2D; x = x
				+ InitializeParameter.CELL_WIDTH) {
			for (int z = 0; z < (SharedVariables.QHR * InitializeParameter.CELL_WIDTH); z = z
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
						/ InitializeParameter.CELL_WIDTH][z
						/ InitializeParameter.CELL_WIDTH];

				// und Pixel entsprechend der Zellgröße anpassen
				// Zellen können mehrere Pixel groß sein
				for (int i = x; i < x + InitializeParameter.CELL_WIDTH; i++) {
					for (int j = z; j < z + InitializeParameter.CELL_WIDTH; j++) {
						// Erst y dann x, damit linke Seite links dargestellt
						// wird
						pixelWriter.setColor(i, j, color);
					}
				}
			}
		}
	}

	/**
	 * Initialisierung des Farbarrays anhand der vorgegebenen
	 * Initialisierungstemperaturen.
	 */
	private void initializeColorArray() {
		for (int x = 0; x < SharedVariables.QLR_2D; x = x
				+ InitializeParameter.CELL_WIDTH) {
			for (int z = 0; z < (SharedVariables.QHR * InitializeParameter.CELL_WIDTH); z = z
					+ InitializeParameter.CELL_WIDTH) {

				// Auf x und z Quaderzellen runterrechnen
				int xCell = x / InitializeParameter.CELL_WIDTH;
				int zCell = z / InitializeParameter.CELL_WIDTH;

				float temperature = SharedVariables.u1[xCell][0][zCell];

				// Color Array initialisieren
				computeAndSetColor(temperature, xCell, zCell);
			}
		}
	}

	/**
	 * Initialisiert das Ausgabefenster für die Simulation.
	 */
	private void initializeOutputWindow(Stage primaryStage) {
		primaryStage.setTitle("Simulation Wärmediffusion");
		BorderPane root = new BorderPane();
		// 20 Pixel Außenrand
		Scene scene = new Scene(root, SharedVariables.QLR_2D,
				SharedVariables.QHR * InitializeParameter.CELL_WIDTH,
				Color.WHITE);

		// Image und dessen PixelWriter ist die performanteste Methode um in
		// JavaFX Pixel darzustellen
		ImageView imageView = new ImageView();
		WritableImage image = new WritableImage(SharedVariables.QLR_2D,
				SharedVariables.QHR * InitializeParameter.CELL_WIDTH);
		imageView.setImage(image);
		pixelWriter = image.getPixelWriter();
		root.setCenter(imageView);
		primaryStage.setScene(scene);
	}
}
