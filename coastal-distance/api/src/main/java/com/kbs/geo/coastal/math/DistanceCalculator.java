package com.kbs.geo.coastal.math;

public interface DistanceCalculator {
	public static final Double EARTH_RADIUS_IN_MILES = 3959.87433;
	public DistanceCalculatorResult calculate();
}
