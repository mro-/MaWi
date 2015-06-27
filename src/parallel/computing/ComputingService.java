package parallel.computing;

import parallel.InitializeParameter;
import parallel.init.SharedVariables;
import parallel.visualization.ColorService;

/**
 * Auslagerung der Berechnungsfunktion für die Wärmediffusion. Quader wird in
 * Scheiben geschnitten entlang der x-Achse.
 */
public class ComputingService {

	/**
	 * Berechnung der Quaderfelder im entsprechenden Abschnitt des Quaders.
	 */
	public static void compute(int xStart, int xEnd) {
		float newTemperature;
		float oldTemperature;

		for (int x = xStart; x < xEnd; x++) {
			for (int y = 1; y < SharedVariables.QBR - 1; y++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					if (SharedVariables.isu1Base) {
						oldTemperature = SharedVariables.u1[x][y][z];
						newTemperature = oldTemperature
								+ SharedVariables.FACTOR
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
								+ SharedVariables.FACTOR
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
					if (InitializeParameter.VISUALIZATION) {
						if (z == SharedVariables.Z_HALF
								&& newTemperature != oldTemperature) {
							ColorService.computeAndSetColor(newTemperature, x,
									y);
						}
					}
				}
			}
		}
	}

}
