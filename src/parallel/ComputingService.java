package parallel;

import javafx.scene.paint.Color;

/**
 * Berechnung, die ein Thread durchführt. Quader wird in Scheiben geschnitten.
 */
public class ComputingService implements Runnable {

	private int xStart;

	private int xEnd;

	public ComputingService(int xStart, int xEnd) {
		this.xStart = xStart;
		this.xEnd = xEnd;
	}

	@Override
	public void run() {
		// Berechnung der Quaderfelder im entsprechenden Abschnitt
		float newTemperature;
		float oldTemperature;

		for (int x = xStart; x < xEnd; x++) {
			for (int y = 1; y < SharedVariables.QBR - 1; y++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					if (SharedVariables.isu1Base) {
						oldTemperature = SharedVariables.u1[x][y][z];
						newTemperature = oldTemperature
								+ SharedVariables.FAKTOR
								* (SharedVariables.u1[x - 1][y][z]
										+ SharedVariables.u1[x + 1][y][z]
										+ SharedVariables.u1[x][y - 1][z]
										+ SharedVariables.u1[x][y + 1][z]
										+ SharedVariables.u1[x][y][z - 1]
										+ SharedVariables.u1[x][y][z + 1] - (6 * oldTemperature));
						SharedVariables.u2[x][y][z] = newTemperature;
					} else {
						oldTemperature = SharedVariables.u2[x][y][z];
						newTemperature = oldTemperature
								+ SharedVariables.FAKTOR
								* (SharedVariables.u2[x - 1][y][z]
										+ SharedVariables.u2[x + 1][y][z]
										+ SharedVariables.u2[x][y - 1][z]
										+ SharedVariables.u2[x][y + 1][z]
										+ SharedVariables.u2[x][y][z - 1]
										+ SharedVariables.u2[x][y][z + 1] - (6 * oldTemperature));
						SharedVariables.u1[x][y][z] = newTemperature;
					}
					// Farbe für entsprechende Zellen berechnen, falls sich die
					// Temperatur verändert hat
					// => Optimierung, Farbe könnte auch immer am Ende berechnet
					// werden für alle Zellen
					if (z == SharedVariables.Z_HALF
							&& newTemperature != oldTemperature) {
						computeAndSetColor(newTemperature, x, y);
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

		SharedVariables.tempInColor[x][y] = SharedVariables.COLORS[mappedTemperatureI];
	}

	/**
	 * Alternative Farbberechnung (fließender Übergang)
	 */
	private void computeAndSetColor2(float temperature, int x, int y) {
		SharedVariables.tempInColor[x][y] = Color.hsb(temperature, 1, 1);
	}
}
