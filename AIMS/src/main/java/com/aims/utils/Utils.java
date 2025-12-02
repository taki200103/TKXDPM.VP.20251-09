package com.aims.utils;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

public class Utils {
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-4s] [%1$tF %1$tT] [%2$-7s] %5$s %n");
	}

	public static Logger getLogger(String className) {
		return Logger.getLogger(className);
	}

	public static String getCurrencyFormat(int num) {
		@SuppressWarnings("deprecation")
		Locale vietname = new Locale("vi", "VN");
		NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(vietname);
		return defaultFormat.format(num);
	}
}
