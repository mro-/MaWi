package parallel;

import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Deprecated
public class CopyOfOutputJFX_oldWithTextAndError extends Application {

	// 11 Farben (müssen insgesamt 11 sein!!!)
	private final Color[] COLORS = { Color.rgb(0, 0, 255), // blau
			Color.rgb(137, 216, 230), // hell blau
			Color.rgb(200, 255, 200), // blau grün
			Color.rgb(220, 255, 150), // gelb grün
			Color.rgb(255, 255, 100, 0.8), // hell gelb
			Color.rgb(255, 255, 0), // gelb
			Color.rgb(255, 201, 14), // gelb orange
			Color.rgb(255, 130, 0), // orange
			Color.rgb(255, 80, 0), // orange rot
			Color.rgb(255, 0, 0), // rot
			Color.rgb(190, 1, 0) // dunkel rot
	};

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		initializeQuader();

		primaryStage.setTitle("Simulation Wärmediffusion");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 600, 600, Color.WHITE);

		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(20));
		gridpane.setHgap(1);
		gridpane.setVgap(1);

		// Initialisierung der Fläche
		int zHalf = SharedVariables.QHR / 2;
		for (int x = 0; x < SharedVariables.QLR; x++) {
			for (int y = 0; y < SharedVariables.QBR; y++) {
				Rectangle r = new Rectangle();
				r.setWidth(40);
				r.setHeight(40);
				// r.setStroke(Color.BLACK);
				r.setFill(computeColor(SharedVariables.u1[x][y][zHalf]));

				// Text hinzufügen, um Werte zu prüfen
				Text text = new Text(SharedVariables.u1[x][y][zHalf] + "");
				text.setFont(Font.font("Verdana", 10));
				StackPane stack = new StackPane();
				stack.getChildren().addAll(r, text);
				// Ende Text

				// Erst y dann x, damit linke Seite links dargestellt wird
				gridpane.add(stack, y, x);
			}
		}

		// Farbskala ausgeben
		for (int i = 0; i < 11; i++) {
			Rectangle r = new Rectangle();
			r.setWidth(40);
			r.setHeight(40);
			r.setFill(COLORS[i]);
			gridpane.add(r, i, InitializeParameter.QL + 3);
		}

		root.setCenter(gridpane);

		primaryStage.setScene(scene);
		primaryStage.show();

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws Exception {

				// Thread.sleep(500);
				for (int n = 1; n <= InitializeParameter.N; n++) {

					// Berechnung aller Quaderfelder (Rand wird nicht verändert)
					// Thread t1 = new Thread(new ComputingAlgorithmus(1,
					// SharedVariables.QLR - 1));

					Thread t1 = new Thread(new ComputingAlgorithmus(1, 7 - 1));
					Thread t2 = new Thread(new ComputingAlgorithmus(6,
							SharedVariables.QLR - 1));

					// Platform.runLater(t1);
					// Platform.runLater(t2);

					t1.start();
					t2.start();
					t1.join();
					t2.join();
					DecimalFormat f = new DecimalFormat("0.0");

					// Ausgabe, alle Felder berücksichtigen
					for (int x = 0; x < SharedVariables.QLR; x++) {
						for (int y = 0; y < SharedVariables.QBR; y++) {
							// Farben berechnen und zuweisen
							// Ohne Text
							// Rectangle rectangle = (Rectangle)
							// getNodeFromGridPane(
							// gridpane, y, x);
							float temperature;
							if (SharedVariables.isu1Base) {
								temperature = SharedVariables.u2[x][y][zHalf];
							} else {
								temperature = SharedVariables.u1[x][y][zHalf];
							}

							// Zahlen mit ausgeben
							StackPane stack = (StackPane) getNodeFromGridPane(
									gridpane, y, x);
							Rectangle rectangle = (Rectangle) stack
									.getChildren().get(0);
							if (rectangle != null) {
								rectangle.setFill(computeColor(temperature));
							}
							Text text = (Text) stack.getChildren().get(1);
							if (text != null) {
								text.setText(f.format(temperature) + "");
							}
							// Ende Text
						}
					}

					if (SharedVariables.isu1Base) {
						SharedVariables.isu1Base = false;
					} else {
						SharedVariables.isu1Base = true;
					}
				}
				return null;

			}
		};

		new Thread(task).start();

	}

	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getColumnIndex(node) == col
					&& GridPane.getRowIndex(node) == row) {
				return node;
			}
		}
		return null;
	}

	private Paint computeColor(float temperature) {
		// Temperatur auf 0-1 Skala mappen
		// (value-min)/(max-min)
		float mappedTemperatureF = (temperature - InitializeParameter.MIN_TEMP)
				/ (InitializeParameter.MAX_TEMP - InitializeParameter.MIN_TEMP);
		mappedTemperatureF = (mappedTemperatureF < 0) ? 0
				: ((mappedTemperatureF > 1) ? 1 : mappedTemperatureF);

		// Mappen auf 0-10 Skala
		int mappedTemperatureI = Math.round(mappedTemperatureF * 10);

		return COLORS[mappedTemperatureI];
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
