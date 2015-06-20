package parallel;

/**
 * Berechnung, die ein Thread durchführt.
 */
public class ComputingRunnable implements Runnable {

	private int xStart;

	private int xEnd;

	public ComputingRunnable(int xStart, int xEnd) {
		this.xStart = xStart;
		this.xEnd = xEnd;
	}

	@Override
	public void run() {
		ComputingService.compute(xStart, xEnd);
	}

}
