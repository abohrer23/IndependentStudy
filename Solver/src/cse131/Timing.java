package support.cse131;

import java.util.Optional;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class Timing {
	private static Optional<Long> t0 = Optional.empty();

	public static double getCurrentTimeInSeconds() {
		if (t0.isPresent()) {
			long millisDelta = System.currentTimeMillis() - t0.get();
			return millisDelta * 0.001;
		} else {
			t0 = Optional.of(System.currentTimeMillis());
			return 0.0;
		}
	}
}
