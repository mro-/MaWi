package notparallel.threeD;

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

import java.text.DecimalFormat;

/**
 * Modul dient der Prüfung, ob die parallelen Rechnungen richtig sind.
 * Die Rechnung wird hier sequentiell durchgeführt auf einem 3D Würfel.
 * Die Temperaturen der einzelen Zellen werden ebenfalls angezeigt.
 */
public class OutputJFX_3D extends Application {

    // 11 Farben (müssen insgesamt 11 sein!!!)
    private final Color[] COLORS = {Color.rgb(0, 0, 255), // blau
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

    /**
     * Initialiserung des Quaders mit Randtemperaturen und Starttemperatur.
     */
    public void initializeQuader() {
        for (int x = 0; x < SharedVariables_3D.QLR; x++) {
            for (int y = 0; y < SharedVariables_3D.QBR; y++) {
                for (int z = 0; z < SharedVariables_3D.QHR; z++) {

                    // Hintere Seite mit Randtemperatur initialisieren
                    if (x == 0) {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.RTH;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.RTH;

                        // Vordere Seite mit Randtemperatur initialisieren
                    } else if (x == SharedVariables_3D.QLR - 1) {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.RTV;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.RTV;

                        // Rechte Seite mit Randtemperatur initialisieren
                    } else if (y == SharedVariables_3D.QBR - 1) {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.RTR;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.RTR;

                        // Linke Seite mit Randtemperatur initialisieren
                        // (konstate Temperatur über die gesamte Seite)
                        // TODO weitere Aufgaben implementieren
                    } else if (y == 0) {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.RTL;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.RTL;

                        // Untere Seite mit Randtemperatur initialisieren
                    } else if (z == 0) {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.RTU;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.RTU;

                        // Obere Seite mit Randtemperatur initialisieren
                    } else if (z == SharedVariables_3D.QHR - 1) {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.RTO;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.RTO;

                        // Quader mit Starttemperatur initialisieren
                    } else {
                        SharedVariables_3D.u1[x][y][z] = InitializeParameter_3D.TS;
                        SharedVariables_3D.u2[x][y][z] = InitializeParameter_3D.TS;
                    }
                    // System.out.print(SharedVariables_3D.u1[x][y][z] + " ");
                }
                // System.out.print("\n");
            }
            // System.out.println();
        }
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

        int zHalf = SharedVariables_3D.QHR / 2;

        // Initialisierung der Fläche
        for (int x = 0; x < SharedVariables_3D.QLR; x++) {
            for (int y = 0; y < SharedVariables_3D.QBR; y++) {
                Rectangle r = new Rectangle();
                r.setWidth(40);
                r.setHeight(40);
                // r.setStroke(Color.BLACK);
                r.setFill(computeColor(SharedVariables_3D.u1[x][y][zHalf]));

                // Text hinzufügen, um Werte zu prüfen
                Text text = new Text(SharedVariables_3D.u1[x][y][zHalf] + "");
                text.setFont(Font.font("Verdana", 10));
                StackPane stack = new StackPane();
                stack.getChildren().addAll(r, text);

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
            gridpane.add(r, i, InitializeParameter_3D.QL + 3);
        }

        root.setCenter(gridpane);

        primaryStage.setScene(scene);
        primaryStage.show();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                Thread.sleep(100);

                for (int n = 1; n <= InitializeParameter_3D.N; n++) {
                    // Berechnung aller Quaderfelder (Rand wird nicht verändert)
                    for (int x = 1; x < SharedVariables_3D.QLR - 1; x++) {
                        for (int y = 1; y < SharedVariables_3D.QBR - 1; y++) {
                            for (int z = 1; z < SharedVariables_3D.QHR - 1; z++) {
                                SharedVariables_3D.u2[x][y][z] = SharedVariables_3D.u1[x][y][z]
                                        + (SharedVariables_3D.FAKTOR * (SharedVariables_3D.u1[x - 1][y][z]
                                        + SharedVariables_3D.u1[x + 1][y][z]
                                        + SharedVariables_3D.u1[x][y - 1][z]
                                        + SharedVariables_3D.u1[x][y + 1][z]
                                        + SharedVariables_3D.u1[x][y][z - 1]
                                        + SharedVariables_3D.u1[x][y][z + 1] - (6 * SharedVariables_3D.u1[x][y][z])));
                            }
                        }
                    }

                    DecimalFormat f = new DecimalFormat("0.0");

                    // Ausgabe, alle Felder berücksichtigen
                    for (int x = 0; x < SharedVariables_3D.QLR; x++) {
                        for (int y = 0; y < SharedVariables_3D.QBR; y++) {
                            // Farben berechnen und zuweisen
                            // Rectangle rectangle = (Rectangle)
                            // getNodeFromGridPane(
                            // gridpane, y, x);

                            // Zahlen und Farbe der anzuzeigenden Rechtecke
                            // ändern
                            StackPane stack = (StackPane) getNodeFromGridPane(
                                    gridpane, y, x);
                            Rectangle rectangle = (Rectangle) stack
                                    .getChildren().get(0);
                            if (rectangle != null) {
                                rectangle
                                        .setFill(computeColor((float) SharedVariables_3D.u2[x][y][zHalf]));
                            }
                            Text text = (Text) stack.getChildren().get(1);
                            if (text != null) {
                                text.setText(f
                                        .format(SharedVariables_3D.u2[x][y][zHalf])
                                        + "");
                            }
                        }
                    }

                    // Neue Werte, sind jetzt alte Werte
                    for (int x = 1; x < SharedVariables_3D.QLR; x++) {
                        for (int y = 1; y < SharedVariables_3D.QBR; y++) {
                            for (int z = 1; z < SharedVariables_3D.QHR; z++) {
                                // u1 = u2, geht so nicht, da arrayreferenzen
                                // gebildet werden
                                // Inhalte müssen explizit getauscht werden
                                SharedVariables_3D.u1[x][y][z] = SharedVariables_3D.u2[x][y][z];
                            }
                        }
                    }
                    Thread.sleep(10);
                    // Platform.runLater
                    // runnable
                    // das immer aufrufen wenn matrix berechnet ist
                    // asynctask
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
        float mappedTemperatureF = (temperature - InitializeParameter_3D.MIN_TEMP)
                / (InitializeParameter_3D.MAX_TEMP - InitializeParameter_3D.MIN_TEMP);
        mappedTemperatureF = (mappedTemperatureF < 0) ? 0
                : ((mappedTemperatureF > 1) ? 1 : mappedTemperatureF);

        // Mappen auf 0-10 Skala
        int mappedTemperatureI = Math.round(mappedTemperatureF * 10);

        return COLORS[mappedTemperatureI];
    }
}
