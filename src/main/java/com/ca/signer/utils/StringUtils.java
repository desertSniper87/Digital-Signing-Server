package com.ca.signer.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

@SuppressWarnings("deprecation")
public class StringUtils {
	public static boolean containsIgnoringCase(String source, String pattern) {
		return toUpperCase(source).contains(toUpperCase(pattern));
	}

	public static boolean isEqualIgnoringCase(String str1, String str2) {
		str1 = toUpperCase(str1);
		str2 = toUpperCase(str2);
		return str1.equals(str2);
	}

	public static boolean isBlank(String str) {
		str = trim(str);
		return str.isEmpty();
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static String toUpperCase(String str) {
		str = trim(str);
		return str.toUpperCase();
	}

	public static boolean isEqual(String str1, String str2) {
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equals(str2);
	}

	public static String trim(String str) {
		if (str == null) {
			return "";
		}
		return str.trim();
	}

	public static String escapeCharFromSignature(String str) {
		return str.replace("\\r\\n", "");
	}

	public static String getCommaSeparatedString(Collection<? extends Number> collection) {
		StringBuilder stringBuilder = new StringBuilder("(");
		int index = 0;
		for (Iterator<? extends Number> iterator = collection.iterator(); iterator.hasNext(); index++) {
			if (index > 0) {
				stringBuilder.append(",");
			}
			stringBuilder.append(iterator.next());
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	public static Integer[] toIntArray(String[] param) {
		Integer[] integers = new Integer[param.length];
		for (int i = 0; i < param.length; i++) {
			integers[i] = Integer.parseInt(param[i]);
		}
		return integers;
	}

	public static Double[] toDoubleArray(String[] parameterValues) {
		Double[] doubles = new Double[parameterValues.length];
		for (int i = 0; i < parameterValues.length; i++) {
			doubles[i] = Double.parseDouble(parameterValues[i]);
		}
		return doubles;
	}

	public static String parseJwtFromText(String headerAuth) {
		if (StringUtils.isNotBlank(headerAuth) && headerAuth.startsWith("Bearer")) {
			return headerAuth.replace("Bearer", "").trim();
		}
		return null;
	}

	public static String get17DigitNid(String dateOfBirth, String nid13Digit) {
		return new StringBuilder(dateOfBirth.substring(0, 4)).append(nid13Digit).toString();
	}

	@SuppressWarnings("deprecation")
	public static String cleanIt(String str) {
		return StringEscapeUtils.escapeHtml4(str);
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static final int convertBengaliNumberToEnglish(String numberStr) {
		return Integer.parseInt(getDigitEnglishFromBangla(numberStr));
	}

	public static final String getDigitEnglishFromBangla(String number) {
		Map<Character, Character> banglaToEnglishDigitsMap = new HashMap<Character, Character>();

		banglaToEnglishDigitsMap.put('০', '0');
		banglaToEnglishDigitsMap.put('১', '1');
		banglaToEnglishDigitsMap.put('২', '2');
		banglaToEnglishDigitsMap.put('৩', '3');
		banglaToEnglishDigitsMap.put('৪', '4');
		banglaToEnglishDigitsMap.put('৫', '5');
		banglaToEnglishDigitsMap.put('৬', '6');
		banglaToEnglishDigitsMap.put('৭', '7');
		banglaToEnglishDigitsMap.put('৮', '8');
		banglaToEnglishDigitsMap.put('৯', '9');

		if (StringUtils.isBlank(number))
			return new String("");

		StringBuilder builder = new StringBuilder();
		try {
			for (int i = 0; i < number.length(); i++) {
				if (banglaToEnglishDigitsMap.containsKey(number.charAt(i))) {
					builder.append(banglaToEnglishDigitsMap.get(number.charAt(i)));
				} else {
					builder.append(number.charAt(i));
				}
			}
		} catch (Exception e) {
			return new String("");
		}
		return builder.toString();
	}
}
