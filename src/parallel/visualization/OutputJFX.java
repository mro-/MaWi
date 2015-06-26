package parallel.visualization;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import parallel.InitializeParameter;
import parallel.computing.ControlUnit;
import parallel.init.InitializeCuboid;
import parallel.init.InitializeServices;
import parallel.init.SharedVariables;

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
		InitializeCuboid.initializeQuader();

		// Initialisierung des Farbarrays
		InitializeServices.initializeColorArray();

		// Berechnungsservices anlegen
		InitializeServices.createComputingServices();

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
	 * 1 Zelle wird als Quadrat von CELL_WIDTH Pixel visualisiert
	 */
	public static void updatePixelInView() {
		// Ausgabe, alle Felder berücksichtigen (inklusive Rand)
		for (int x = 0; x < SharedVariables.QLR_2D; x = x
				+ InitializeParameter.CELL_WIDTH) {
			for (int y = 0; y < SharedVariables.QBR_2D; y = y
					+ InitializeParameter.CELL_WIDTH) {

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
	 * Initialisiert das Ausgabefenster für die Simulation.
	 */
	private void initializeOutputWindow(Stage primaryStage) {
		primaryStage.setTitle("Simulation Wärmediffusion");
		ScrollPane root = new ScrollPane();
		Scene scene = new Scene(root, SharedVariables.QBR_2D + 2,
				SharedVariables.QLR_2D + 2, Color.WHITE);

		// Image und dessen PixelWriter ist die performanteste Methode um in
		// JavaFX Pixel darzustellen
		ImageView imageView = new ImageView();
		WritableImage image = new WritableImage(SharedVariables.QBR_2D,
				SharedVariables.QLR_2D);
		imageView.setImage(image);
		pixelWriter = image.getPixelWriter();
		root.setContent(imageView);
		primaryStage.setScene(scene);
	}
}
