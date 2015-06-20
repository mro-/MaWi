package parallel;

import java.util.concurrent.Callable;

/**
 * Berechnung, die vom Thread-Pool durchgeführt werden kann.
 */
public class ComputingCallable implements Callable<Void> {

	private int xStart;

	private int xEnd;

	public ComputingCallable(int xStart, int xEnd) {
		this.xStart = xStart;
		this.xEnd = xEnd;
	}

	@Override
	public Void call() throws Exception {
		ComputingService.compute(xStart, xEnd);
		return null;
	}

}
