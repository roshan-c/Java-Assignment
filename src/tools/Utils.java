package tools;

import java.util.Random;

public class Utils {
	private Random random = new Random();
	
	/**
	 * Generates a random integer between min (inclusive) and max (exclusive).
	 * @param min The minimum value.
	 * @param max The maximum value (exclusive).
	 * @return A random integer in the specified range.
	 */
	public int randomInt(int min, int max) {
		if (min >= max) {
			// Or throw an IllegalArgumentException, or return min
			return min;
		}
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * Generates a random double between min (inclusive) and max (exclusive).
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @return A random double in the specified range.
	 */
	public double randomDouble(double min, double max) {
		if (min >= max) {
			// Or throw an IllegalArgumentException, or return min
			return min;
		}
		return min + (max - min) * random.nextDouble();
	}
	
	/**
	 * Pauses the current thread for a specified number of milliseconds.
	 * @param millis The number of milliseconds to pause.
	 */
	public void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
			System.err.println("Thread pause was interrupted: " + e.getMessage());
		}
	}
}
