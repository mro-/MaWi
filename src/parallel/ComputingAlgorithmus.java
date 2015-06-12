package parallel;

/**
 *
 */
public class ComputingAlgorithmus implements Runnable {

	private int xStart;
	private int xEnd;

	public ComputingAlgorithmus(int xStart, int xEnd) {
		this.xStart = xStart;
		this.xEnd = xEnd;
	}

	@Override
	public void run() {
		// Berechnung der Quaderfelder im entsprechenden Abschnitt
		for (int x = xStart; x < xEnd; x++) {
			for (int y = 1; y < SharedVariables.QBR - 1; y++) {
				for (int z = 1; z < SharedVariables.QHR - 1; z++) {

					if (SharedVariables.isu1Base) {
						SharedVariables.u2[x][y][z] = SharedVariables.u1[x][y][z]
								+ SharedVariables.FAKTOR
								* (SharedVariables.u1[x - 1][y][z]
										+ SharedVariables.u1[x + 1][y][z]
										+ SharedVariables.u1[x][y - 1][z]
										+ SharedVariables.u1[x][y + 1][z]
										+ SharedVariables.u1[x][y][z - 1]
										+ SharedVariables.u1[x][y][z + 1] - (6 * SharedVariables.u1[x][y][z]));
					} else {
						SharedVariables.u1[x][y][z] = SharedVariables.u2[x][y][z]
								+ SharedVariables.FAKTOR
								* (SharedVariables.u2[x - 1][y][z]
										+ SharedVariables.u2[x + 1][y][z]
										+ SharedVariables.u2[x][y - 1][z]
										+ SharedVariables.u2[x][y + 1][z]
										+ SharedVariables.u2[x][y][z - 1]
										+ SharedVariables.u2[x][y][z + 1] - (6 * SharedVariables.u2[x][y][z]));
					}

				}
			}
		}

	}
}
