package com.opzoon.vdi.core.util;

public abstract class ConditionUtils {
	
	private ConditionUtils() {}

	public static boolean numberEquals(long number1, long number2) {
		return number1 == number2;
	}

	public static boolean numberNotEquals(long number1, long number2) {
		return number1 != number2;
	}

}
