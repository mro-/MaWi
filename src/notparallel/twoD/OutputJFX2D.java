package notparallel.twoD;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import parallel.InitializeParameter;

public class OutputJFX2D extends Application {

    //11 Farben (müssen insgesamt 11 sein!!!)
    private final Color[] COLORS =
            {
                    Color.rgb(0, 0, 255), // blau
                    Color.rgb(137, 216, 230), // hell blau
                    Color.rgb(200, 255, 200), // blau grün
                    Color.rgb(220, 255, 150), // gelb grün
                    Color.rgb(255, 255, 100, 0.8), // hell gelb
                    Color.rgb(255, 255, 0), // gelb
                    Color.rgb(255, 201, 14), // gelb orange
                    Color.rgb(255, 130, 0), // orange
                    Color.rgb(255, 80, 0), // orange rot
                    Color.rgb(255, 0, 0),  // rot
                    Color.rgb(190, 1, 0) // dunkel rot
            };
    //2D Array Rechteck
    private float[][] u1_2D = new float[InitializeParameter.QL][InitializeParameter.QB];
    private float[][] u2_2D = new float[InitializeParameter.QL][InitializeParameter.QB];

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void initializeRectangle() {
        for (int ix = 0; ix < InitializeParameter.QB; ix++) {
            boolean leftSide = true;
            if (ix == 0 || ix == InitializeParameter.QB - 1) {
                leftSide = false;
            }
            for (int iy = 0; iy < InitializeParameter.QL; iy++) {
                if (leftSide && iy == 0) {
                    u1_2D[ix][iy] = InitializeParameter.RTL;
                    u2_2D[ix][iy] = InitializeParameter.RTL;
                } else if (ix == 0 || ix == InitializeParameter.QB - 1 || leftSide && iy == InitializeParameter.QB - 1) {
                    //Annahme, dass Randtemperatur aller Seiten gleich ist
                    u1_2D[ix][iy] = InitializeParameter.RTR;
                    u2_2D[ix][iy] = InitializeParameter.RTR;
                } else {
                    u1_2D[ix][iy] = InitializeParameter.TS;
                    u2_2D[ix][iy] = InitializeParameter.TS;
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initializeRectangle();

        primaryStage.setTitle("Simulation Wärmediffusion");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 600, Color.WHITE);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(20));
        gridpane.setHgap(1);
        gridpane.setVgap(1);


        // Initialisierung der Fläche
        for (int iy = 0; iy < InitializeParameter.QL; iy++) {
            for (int ix = 0; ix < InitializeParameter.QB; ix++) {
                Rectangle r = new Rectangle();
                r.setWidth(10);
                r.setHeight(10);
                //r.setStroke(Color.BLACK);
                r.setFill(computeColor(u1_2D[ix][iy]));
                gridpane.add(r, iy, ix);
            }
        }

        for (int i = 0; i < 11; i++) {
            Rectangle r = new Rectangle();
            r.setWidth(10);
            r.setHeight(10);
            r.setFill(COLORS[i]);
            gridpane.add(r, i, InitializeParameter.QL + 3);
        }

        root.setCenter(gridpane);


        primaryStage.setScene(scene);
        primaryStage.show();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                Thread.sleep(100);

                // Berechnung aller inneren Felder
                for (int n = 1; n <= InitializeParameter.N; n++) {
                    for (int iy = 1; iy < InitializeParameter.QL - 1; iy++) {
                        for (int ix = 1; ix < InitializeParameter.QB - 1; ix++) {
                            u2_2D[ix][iy] = u1_2D[ix][iy]
                                    + (0.1f * (u1_2D[ix + 1][iy] + u1_2D[ix - 1][iy] - (2 * u1_2D[ix][iy])))
                                    + (0.1f * (u1_2D[ix][iy + 1] + u1_2D[ix][iy - 1] - (2 * u1_2D[ix][iy])));
                        }
                    }

                    // Alle Felder berücksichtigen
                    for (int iy = 0; iy < InitializeParameter.QL; iy++) {
                        for (int ix = 0; ix < InitializeParameter.QB; ix++) {
                            // Farben berechnen und zuweisen
                            Rectangle rectangle = (Rectangle) getNodeFromGridPane(
                                    gridpane, iy, ix);
                            if (rectangle != null) {
                                rectangle.setFill(computeColor((float) u2_2D[ix][iy]));
                            }
                            // Neue Werte, sind jetzt alte Werte
                            // Tausch InitializeParameter_3D.u1_2D = InitializeParameter_3D.u2_2D, geht so nicht, da arrayreferenzen
                            // gebildet werden
                            // Inhalte müssen explizit getauscht werden
                            u1_2D[ix][iy] = u2_2D[ix][iy];
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
        float mappedTemperatureF = (temperature - InitializeParameter.MIN_TEMP) / (InitializeParameter.MAX_TEMP - InitializeParameter.MIN_TEMP);
        mappedTemperatureF = (mappedTemperatureF < 0) ? 0 : ((mappedTemperatureF > 1) ? 1 : mappedTemperatureF);

        // Mappen auf 0-10 Skala
        int mappedTemperatureI = Math.round(mappedTemperatureF * 10);

        return COLORS[mappedTemperatureI];
    }
}

