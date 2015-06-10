package notparallel;

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

public class SequentiellOutputJFX extends Application {

    // Randtemperatur
    private final float RT_OTHER = 20;
    // Randtemperatur linke Quaderseite
    private final float RT_L = 50;
    // Starttemperatur (Quader)
    private final float TS = 20;

    // Anzahl Zellen (zähler startet bei 0)
    private final int NX = 50;
    private final int NY = 50;

    //Minimale Temperatur
    private final int MIN_TEMP = 0;
    //Maximale Temperatur
    private final int MAX_TEMP = 50;

    // Iterationsschritte
    private final int T = 100;

    //11 Farben (müssen insgesamt 11 sein!!!)
    private final Color[] COLORS =
            {
                    Color.rgb(0, 0, 255), //blue
                    Color.rgb(137, 216, 230), //light blue
                    Color.rgb(200, 255, 200), //blau grün
                    Color.rgb(220, 255, 150), //yello green LIGHTGOLDENRODYELLOW 250,250,200
                    Color.rgb(255, 255, 100, 0.8), // hell gelb
                    Color.rgb(255, 255, 0), //gelb
                    Color.rgb(255, 201, 14), // gelb orange
                    Color.rgb(255, 130, 0), //orange
                    Color.rgb(255, 80, 0), //orange rot
                    Color.rgb(255, 0, 0),  // rot
                    Color.rgb(190, 1, 0) // dunkel rot
                    /*
                    Color.rgb(255, 204, 0), // gelb orange
                    Color.rgb(255, 170, 0), //orange
                    Color.rgb(255, 100, 0), //orange rot
                    Color.rgb(255, 0, 0),  // rot
                    Color.rgb(200, 0, 0) // dunkel rot
                    */
            };
    /*
    alte Werte
    float[][] u1 = {{10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
    {10, 10, 10, 10, 10, 10, 10, 10, 10, 10}};
    */
    float[][] u1 = new float[NX][NY];
    // neue werte (mit Randbedingungen)
    /*
    float[][] u2 = {{10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
            {10, 10, 10, 10, 10, 10, 10, 10, 10, 10}};
    */
    float[][] u2 = new float[NX][NY];

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void initialize() {
        for (int ix = 0; ix < NX; ix++) {
            boolean leftSide = true;
            if (ix == 0 || ix == NX - 1) {
                leftSide = false;
            }
            for (int iy = 0; iy < NY; iy++) {
                if (leftSide && iy == 0) {
                    u1[ix][iy] = RT_L;
                    u2[ix][iy] = RT_L;
                } else if (ix == 0 || ix == NX - 1 || leftSide && iy == NX - 1) {
                    u1[ix][iy] = RT_OTHER;
                    u2[ix][iy] = RT_OTHER;
                } else {
                    u1[ix][iy] = TS;
                    u2[ix][iy] = TS;
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();

        primaryStage.setTitle("Simulation Wärmediffusion");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 600, Color.WHITE);

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(20));
        gridpane.setHgap(1);
        gridpane.setVgap(1);


        // Initialisierung der Fläche
        for (int iy = 0; iy < NY; iy++) {
            for (int ix = 0; ix < NX; ix++) {
                Rectangle r = new Rectangle();
                r.setWidth(10);
                r.setHeight(10);
                //r.setStroke(Color.BLACK);
                r.setFill(computeColor3(u1[ix][iy]));
                gridpane.add(r, iy, ix);
            }
        }

        for (int i = 0; i < 11; i++) {
            Rectangle r = new Rectangle();
            r.setWidth(10);
            r.setHeight(10);
            r.setFill(COLORS[i]);
            gridpane.add(r, i, NY + 3);
        }

        root.setCenter(gridpane);


        primaryStage.setScene(scene);
        primaryStage.show();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                Thread.sleep(100);

                // Berechnung aller inneren Felder
                for (int n = 1; n <= T; n++) {
                    for (int iy = 1; iy < NY - 1; iy++) {
                        for (int ix = 1; ix < NX - 1; ix++) {
                            u2[ix][iy] = u1[ix][iy]
                                    + (0.1f * (u1[ix + 1][iy] + u1[ix - 1][iy] - (2 * u1[ix][iy])))
                                    + (0.1f * (u1[ix][iy + 1] + u1[ix][iy - 1] - (2 * u1[ix][iy])));
                        }
                    }

                    // Alle Felder berücksichtigen
                    for (int iy = 0; iy < NY; iy++) {
                        for (int ix = 0; ix < NX; ix++) {
                            // Farben berechnen und zuweisen
                            Rectangle rectangle = (Rectangle) getNodeFromGridPane(
                                    gridpane, iy, ix);
                            if (rectangle != null) {
                                rectangle.setFill(computeColor3((float) u2[ix][iy]));
                            }
                            // Neue Werte, sind jetzt alte Werte
                            // Tausch u1 = u2, geht so nicht, da arrayreferenzen
                            // gebildet werden
                            // Inhalte müssen explizit getauscht werden
                            u1[ix][iy] = u2[ix][iy];
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

    private Paint computeColor(float aktTemp) {
        if (aktTemp > 0 && aktTemp <= 3) {
            return Color.DARKBLUE;
        }
        if (aktTemp > 3 && aktTemp <= 5) {
            return Color.BLUE;
        }
        if (aktTemp > 5 && aktTemp <= 8) {
            return Color.LIGHTBLUE;
        }
        if (aktTemp > 8 && aktTemp <= 10) {
            return Color.GREENYELLOW;
        }
        if (aktTemp > 10 && aktTemp <= 12) {
            return Color.YELLOW;
        }
        if (aktTemp > 12 && aktTemp <= 15) {
            return Color.ORANGE;
        }
        if (aktTemp > 15 && aktTemp <= 20) {
            return Color.ORANGERED;
        }
        if (aktTemp > 20 && aktTemp <= 50) {
            return Color.RED;
        }
        return Color.GREY;
    }

    //Paint computeColor2(float temp) {
    //    //0-240
    //    return Color.hsb(240 - temp, 1, 1);
    //}


    //-30 +30 BlueYellowRed
    private Paint computeColor3(float t) {
        // Map the temperature to a 0-1 range
        // (value-min)/(max-min)
        float a = (t - MIN_TEMP) / (MAX_TEMP - MIN_TEMP);
        // a = (a < 0) ? 0 : ((a > 1) ? 1 : a);
        a = (a < 0) ? 0 : ((a > 1) ? 1 : a);
        // 0-10
        a = (float) Math.floor(a * 10);

        return COLORS[(int) (a)];
    }
 /*
        Color.rgb(0, 0, 255),
                Color.rgb(0, 75, 255),
                Color.rgb(0, 180, 255),
                Color.rgb(0, 255, 255),
                Color.rgb(0, 175, 255),
                Color.rgb(175, 255, 0),
                Color.rgb(255, 255, 0),
                Color.rgb(255, 180, 0),
                Color.rgb(255, 75, 0),
                Color.rgb(255, 0, 0),
                */
}
