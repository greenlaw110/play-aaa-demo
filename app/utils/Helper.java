package utils;

public class Helper {

	public static <T> T notNull(T obj, String fieldName) {
		if (obj == null) {
			throw new IllegalArgumentException(fieldName + " should not be null");
		}
		return obj;
	}

	public static String notBlank(String str, String fieldName) {
		notNull(str, fieldName);
		if (str.trim().length() == 0) {
			throw new IllegalArgumentException(fieldName + " should not be blank");
		}
		return str;
	}

}
