package notparallel.twoD;

import java.text.DecimalFormat;

public class SequentiellOutputText {

    public static void main(String[] args) {
        // alte werte
        double[][] u1 = {{10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {50, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10}};

        // neue werte (mit Randbedingungen)
        double[][] u2 = {{10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {50, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 10},
                {10, 10, 10, 10, 10, 10, 10, 10, 10, 10}};

        // Anzahl Zellen (zähler startet bei 0)
        int nx = 10;
        int ny = 10;

        // Iterationsschritte
        int t = 5;

        DecimalFormat f = new DecimalFormat("00.00");

        for (int n = 1; n <= t; n++) {

            for (int iy = 1; iy < ny - 1; iy++) {
                for (int ix = 1; ix < nx - 1; ix++) {
                    u2[ix][iy] = u1[ix][iy]
                            + 0.1
                            * (u1[ix + 1][iy] + u1[ix - 1][iy] - 2 * u1[ix][iy])
                            + 0.1
                            * (u1[ix][iy + 1] + u1[ix][iy - 1] - 2 * u1[ix][iy]);

                }
            }

            System.out.print("t=" + n + "\n");
            for (int i = 0; i < nx; i++) {
                for (int j = 0; j < ny; j++) {
                    System.out.print(f.format(u2[i][j]) + " ");
                    // Neue Werte, sind jetzt alte Werte
                    u1[i][j] = u2[i][j];
                }
                System.out.println();
            }
        }
    }
}
