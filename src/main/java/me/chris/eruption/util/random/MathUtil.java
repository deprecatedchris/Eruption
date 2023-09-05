package me.chris.eruption.util.random;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

	public String convertTicksToMinutes(int ticks) {
		long minute = (long) ticks / 1200L;
		long second = (long) ticks / 20L - (minute * 60L);

		String secondString = Math.round(second) + "";

		if (second < 10) {
			secondString = 0 + secondString;
		}
		String minuteString = Math.round(minute) + "";

		if (minute == 0) {
			minuteString = 0 + "";
		}

		return minuteString + ":" + secondString;
	}

	public String convertToRomanNumeral(int number) {
		return switch (number) {
			case 1 -> "I";
			case 2 -> "II";
			default -> null;
		};
	}
}
