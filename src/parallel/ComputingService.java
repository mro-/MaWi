package parallel;

/**
 * Auslagerung der Berechnungsfunktion für die Wärmediffusion. Quader wird in
 * Scheiben geschnitten.
 *
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
						OutputJFX.computeAndSetColor(newTemperature, x, y);
					}
				}
			}
		}
	}

}
