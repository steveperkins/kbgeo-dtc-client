package com.kbs.geo.math;

import com.kbs.geo.coastal.math.DefaultDistanceCalculatorImpl;
import com.kbs.geo.coastal.math.DistanceCalculator;
import com.kbs.geo.coastal.model.GeoCoordinate;

public class DistanceCalculatorUtil {
	
	public static Double getMilesBetween(GeoCoordinate startPoint, GeoCoordinate endPoint) {
		return new DefaultDistanceCalculatorImpl(startPoint, endPoint).calculate().getDistanceInMiles();
	}
	
	public static Double getDistanceInMiles(DistanceCalculator calculator) {
		return calculator.calculate().getDistanceInMiles();
	}
	
}
