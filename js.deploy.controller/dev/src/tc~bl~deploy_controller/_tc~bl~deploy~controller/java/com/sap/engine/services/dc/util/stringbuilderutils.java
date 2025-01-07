package com.sap.engine.services.dc.util;

public final class StringBuilderUtils {

	private StringBuilderUtils() {
	}

	public static String concat(final String... strings) {
		if (strings == null) {
			return null;
		}

		final StringBuilder result = new StringBuilder();
		for (final String string : strings) {
			result.append(string);
		}
		return result.toString();
	}

}
