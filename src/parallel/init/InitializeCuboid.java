package parallel.init;

import parallel.SharedVariables;

/**
 * Alle Methoden um den Quader, je nach Konfiguration, initial mit allen
 * Temperaturen zu bestücken.
 */
public class InitializeCuboid {

	/**
	 * Faktor um den die Temperatur von der Mitte zum Rand linear abnimmt.
	 */
	private static float factorPerCell;

	/**
	 * Initialiserung des Quaders mit den Randtemperaturen und der
	 * Starttemperatur.
	 */
	public static void initializeQuader() {
		// Alle Zellen des Quaders durchlaufen (inklusive Rand)
		for (int x = 0; x < SharedVariables.QLR; x++) {
			for (int y = 0; y < SharedVariables.QBR; y++) {
				for (int z = 0; z < SharedVariables.QHR; z++) {

					// Linke Seite mit Randtemperatur initialisieren
					if (y == 0) {
						SharedVariables.u1[x][y][z] = getTemperatureForHeatMode(
								x, z);
						SharedVariables.u2[x][y][z] = getTemperatureForHeatMode(
								x, z);

						// Rechte Seite mit Randtemperatur initialisieren
					} else if (y == SharedVariables.QBR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTR;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTR;

						// Hintere Seite mit Randtemperatur initialisieren
					} else if (x == 0) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTH;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTH;

						// Vordere Seite mit Randtemperatur initialisieren
					} else if (x == SharedVariables.QLR - 1) {
						SharedVariables.u1[x][y][z] = InitializeParameter.RTV;
						SharedVariables.u2[x][y][z] = InitializeParameter.RTV;

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
				}
			}
		}

		// Temperaturen der linken Seite berechnen, falls sich die Wärmequelle
		// in der Mitte befindet
		if (InitializeParameter.HEAT_MODE == 2) {
			updateRTlinksForCentralHeatMode();
		}
	}

	/**
	 * Initialisiert die linke Seite je nach Wärmequelle. <br>
	 * 1: Konstante Temperatur der gesamten Fläche <br>
	 * 2: Wärmequelle in der Mitte der Fläche <br>
	 * 3: Sinus
	 */
	private static float getTemperatureForHeatMode(int x, int z) {
		switch (InitializeParameter.HEAT_MODE) {
		case 1:
			// Fläche entspricht ingesamt der linken Randtemperatur
			return InitializeParameter.RTL;
		case 2:
			// Die Fläche wird mit der durchschnittlichen Randtemperatur
			// initialisiert. So muss später nicht noch mal extra der Außenrand
			// der linken Seite initialisiert werden.
			return SharedVariables.AVERAGE_BORDER_TEMP_LEFT_SIDE;
		case 3:
			// Fläche entspricht ingesamt der linken Randtemperatur
			return InitializeParameter.RTL;
		}
		return 0;
	}

	/**
	 * Berechnung der Temperaturen der linken Seite, wenn die Wärmequelle in der
	 * Mitte ist. Berechnung geht kreisförmig vor. Die Randtemperatur wird an
	 * den vier Seiten gleich der Durchschnittstemperatur der Ränder gesetzt.
	 */
	private static void updateRTlinksForCentralHeatMode() {
		// Ausdehnung der Hitzequelle in der Mitte
		int xStartIndex;
		int xEndIndex;
		int zStartIndex;
		int zEndIndex;

		// Ist die Kante der x-Länge oder z-Höhe länger?
		boolean xLenghtGreaterThanZ = SharedVariables.QLR > SharedVariables.QHR;

		// Ausdehnung der Hitzequelle bestimmen. Variiert je nach Länge der
		// Seiten von 2x2 bis 3x3 Quaderzellen.
		// Quaderlänge (x-Achse) gerade -> Hitzequelle 2 Zellen breit
		if (SharedVariables.QLR % 2 == 0) {
			xEndIndex = SharedVariables.QLR / 2;
			xStartIndex = xEndIndex - 1;
			// Quaderlänge (x-Achse) ungerade -> Hitzequelle 3 Zellen breit
		} else {
			xEndIndex = ((SharedVariables.QLR - 1) / 2) + 1;
			xStartIndex = xEndIndex - 2;
		}
		// Quaderhöhe (z-Achse) gerade -> Hitzequelle 2 Zellen hoch
		if (SharedVariables.QHR % 2 == 0) {
			zEndIndex = SharedVariables.QHR / 2;
			zStartIndex = zEndIndex - 1;
			// Quaderhöhe (z-Achse) ungerade -> Hitzequelle 3 Zellen hoch
		} else {
			zEndIndex = ((SharedVariables.QHR - 1) / 2) + 1;
			zStartIndex = zEndIndex - 2;
		}

		// Wärmequelle in der Mitte der initialisieren
		for (int x = xStartIndex; x <= xEndIndex; x++) {
			for (int z = zStartIndex; z <= zEndIndex; z++) {
				SharedVariables.u1[x][0][z] = InitializeParameter.RTL;
				SharedVariables.u2[x][0][z] = InitializeParameter.RTL;
			}
		}

		// Faktor berechnen um den die Temperatur von der Mitte zum Rand linear
		// abnimmt (von der längeren Seite ausgehen).
		if (xLenghtGreaterThanZ) {
			factorPerCell = (InitializeParameter.RTL - SharedVariables.AVERAGE_BORDER_TEMP_LEFT_SIDE)
					/ (SharedVariables.QLR / 2);
		} else {
			factorPerCell = (InitializeParameter.RTL - SharedVariables.AVERAGE_BORDER_TEMP_LEFT_SIDE)
					/ (SharedVariables.QHR / 2);
		}

		// Kreis um eine Einheit vergrößern
		xStartIndex--;
		xEndIndex++;
		zStartIndex--;
		zEndIndex++;

		// Weitere Ausdehnung von der Mitte ausgehend berechnen (kreisförmig,
		// quadratisch).
		int counter;
		for (counter = 1; xStartIndex > 0 && zStartIndex > 0
				&& xEndIndex < SharedVariables.QLR - 1
				&& zEndIndex < SharedVariables.QHR - 1; counter++) {

			// nach links und rechts, ausgehend von der Mitte, Temperaturen
			// berechnen
			for (int x = xStartIndex; x <= xEndIndex; x++) {
				for (int z = zStartIndex; z <= zEndIndex; z++) {
					if (x == xStartIndex || x == xEndIndex) {
						setTemperatureOnLeftSide(counter, x, z);
					}
				}
			}
			// nach oben und unten, ausgehend von der Mitte, Temperaturen
			// berechnen
			for (int z = zStartIndex; z <= zEndIndex; z++) {
				for (int x = xStartIndex; x <= xEndIndex; x++) {
					if (z == zStartIndex || z == zEndIndex) {
						setTemperatureOnLeftSide(counter, x, z);
					}
				}
			}

			// Kreis um eine Einheit vergrößern
			xStartIndex--;
			xEndIndex++;
			zStartIndex--;
			zEndIndex++;
		}

		// Wenn die linke Seite rechteckig ist, müssen darüber hinaus noch
		// weitere Initialisierungen vorgenommen werden für die restlichen
		// Flächen (Temperatur weiterhin linear absteigend zum Rand).
		int startCounter = counter;
		// x-Kante ist länger (Länge des Quaders)
		if (xLenghtGreaterThanZ) {
			// Bereich links der Mitte
			for (int x = xStartIndex; x > 0; x--) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					setTemperatureOnLeftSide(counter, x, z);
				}
				counter++;
			}
			counter = startCounter;
			// Bereich rechts der Mitte
			for (int x = xEndIndex; x < SharedVariables.QLR - 1; x++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {
					setTemperatureOnLeftSide(counter, x, z);
				}
				counter++;
			}
			// z-Kante ist länger (Höhe des Quaders)
		} else {
			// Bereich unterhalb der Mitte
			for (int z = zStartIndex; z > 0; z--) {
				for (int x = 1; x < SharedVariables.QLR - 1; x++) {
					setTemperatureOnLeftSide(counter, x, z);
				}
				counter++;
			}
			counter = startCounter;
			// Bereich oberhalb der Mitte
			for (int z = zEndIndex; z < SharedVariables.QHR - 1; z++) {
				for (int x = 1; x < SharedVariables.QLR - 1; x++) {
					setTemperatureOnLeftSide(counter, x, z);
				}
				counter++;
			}
		}
	}

	/**
	 * Initialisiert eine Zelle des Quaders innerhalb der linken Seite mit der
	 * entsprechenden Temperatur.
	 */
	private static void setTemperatureOnLeftSide(int counter, int x, int z) {
		float temperature = InitializeParameter.RTL - (counter * factorPerCell);
		SharedVariables.u1[x][0][z] = temperature;
		SharedVariables.u2[x][0][z] = temperature;
	}

}
