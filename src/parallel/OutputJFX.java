package parallel;

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

public class OutputJFX extends Application {

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

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void initializeQuader() {
        for (int ix = 0; ix < InitializeParameter.QB; ix++) {
            boolean leftSide = true;
            if (ix == 0 || ix == InitializeParameter.QB - 1) {
                leftSide = false;
            }
            for (int iy = 0; iy < InitializeParameter.QL; iy++) {
                if (leftSide && iy == 0) {
                    InitializeParameter.u1[ix][iy] = InitializeParameter.RT_L;
                    InitializeParameter.u2[ix][iy] = InitializeParameter.RT_L;
                } else if (ix == 0 || ix == InitializeParameter.QB - 1 || leftSide && iy == InitializeParameter.QB - 1) {
                    InitializeParameter.u1[ix][iy] = InitializeParameter.RT_OTHER;
                    InitializeParameter.u2[ix][iy] = InitializeParameter.RT_OTHER;
                } else {
                    InitializeParameter.u1[ix][iy] = InitializeParameter.TS;
                    InitializeParameter.u2[ix][iy] = InitializeParameter.TS;
                }
            }
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


        // Initialisierung der Fläche
        for (int iy = 0; iy < InitializeParameter.QL; iy++) {
            for (int ix = 0; ix < InitializeParameter.QB; ix++) {
                Rectangle r = new Rectangle();
                r.setWidth(10);
                r.setHeight(10);
                //r.setStroke(Color.BLACK);
                r.setFill(computeColor(InitializeParameter.u1[ix][iy]));
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
                            InitializeParameter.u2[ix][iy] = InitializeParameter.u1[ix][iy]
                                    + (0.1f * (InitializeParameter.u1[ix + 1][iy] + InitializeParameter.u1[ix - 1][iy] - (2 * InitializeParameter.u1[ix][iy])))
                                    + (0.1f * (InitializeParameter.u1[ix][iy + 1] + InitializeParameter.u1[ix][iy - 1] - (2 * InitializeParameter.u1[ix][iy])));
                        }
                    }

                    // Alle Felder berücksichtigen
                    for (int iy = 0; iy < InitializeParameter.QL; iy++) {
                        for (int ix = 0; ix < InitializeParameter.QB; ix++) {
                            // Farben berechnen und zuweisen
                            Rectangle rectangle = (Rectangle) getNodeFromGridPane(
                                    gridpane, iy, ix);
                            if (rectangle != null) {
                                rectangle.setFill(computeColor((float) InitializeParameter.u2[ix][iy]));
                            }
                            // Neue Werte, sind jetzt alte Werte
                            // Tausch InitializeParameter.u1 = InitializeParameter.u2, geht so nicht, da arrayreferenzen
                            // gebildet werden
                            // Inhalte müssen explizit getauscht werden
                            InitializeParameter.u1[ix][iy] = InitializeParameter.u2[ix][iy];
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

