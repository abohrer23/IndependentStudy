package cse131;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 */
public class ReflectionUtils {
	public static Field getOneAndOnlyOneDeclaredFieldContainingIgnoringCase(Class<?> cls,
			String... lowerCaseCandidateFieldNames) {

		Field[] fields = cls.getDeclaredFields();
		List<Field> foundFields = new LinkedList<>();
		for (Field field : fields) {
			for (String lowerCaseCandidateFieldName : lowerCaseCandidateFieldNames) {
				if (field.getName().toLowerCase().contains(lowerCaseCandidateFieldName)) {
					foundFields.add(field);
					break;
				}
			}
		}
		if (foundFields.size() == 1) {
			return foundFields.get(0);
		} else {
			String candidateText = lowerCaseCandidateFieldNames.length == 1 ? lowerCaseCandidateFieldNames[0]
					: "one of " + Arrays.toString(lowerCaseCandidateFieldNames);
			if (foundFields.size() > 1) {
				StringBuilder allFieldsSB = new StringBuilder();
				for (Field field : fields) {
					allFieldsSB.append(foundFields.contains(field) ? "*** " : "    ");
					allFieldsSB.append(field.getName());
					allFieldsSB.append("\n");
				}
				throw new RuntimeException("more than one of the declared instance variables:\n"
						+ allFieldsSB.toString() + "contains (ignoring case) " + candidateText
						+ ".\nIn order to provide early test results, we regretably require that only one instance variable contains "
						+ candidateText);
			} else {
				StringBuilder allFieldsSB = new StringBuilder();
				for (Field field : fields) {
					allFieldsSB.append("  ");
					allFieldsSB.append(field.getName());
					allFieldsSB.append("\n");
				}
				throw new RuntimeException("out of all declared instance variables:\n" + allFieldsSB.toString()
						+ "none contain (even ignoring case) " + candidateText);
			}
		}
	}
}
