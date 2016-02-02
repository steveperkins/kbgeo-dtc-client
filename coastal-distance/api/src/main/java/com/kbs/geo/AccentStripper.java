package com.kbs.geo;

import java.text.Normalizer;

public class AccentStripper {
	public static String stripAccents(String s) {
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    return s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	}
}
