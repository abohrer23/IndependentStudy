package support.cse131;

import java.util.Collection;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class PrimitiveArrays {
	public static int[] toIntArray(Collection<Integer> collection) {
		return collection.stream().mapToInt(Integer::intValue).toArray();
	}

	public static double[] toDoubleArray(Collection<Double> collection) {
		return collection.stream().mapToDouble(Double::doubleValue).toArray();
	}
}
