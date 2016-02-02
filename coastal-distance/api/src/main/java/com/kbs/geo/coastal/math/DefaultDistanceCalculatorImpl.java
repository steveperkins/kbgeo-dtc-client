package com.kbs.geo.coastal.math;

import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.GeoCoordinate;

/**
 * Calculates the distance in miles between the two points passed to constructor using standard Haversine formula
 * @author Steve
 *
 */
public class DefaultDistanceCalculatorImpl implements DistanceCalculator {
	
	private GeoCoordinate coordinate1;
	private GeoCoordinate coordinate2;

	public DefaultDistanceCalculatorImpl(GeoCoordinate coordinate1, GeoCoordinate coordinate2) {
		this.coordinate1 = coordinate1;
		this.coordinate2 = coordinate2;
	}

	@Override
	public DistanceCalculatorResult calculate() {
		Double coordinateLat = Math.toRadians(coordinate2.getLat().subtract(coordinate1.getLat()).doubleValue());
		Double coordinateLon = Math.toRadians(coordinate2.getLng().subtract(coordinate1.getLng()).doubleValue());
		
		Double lt1 = Math.toRadians(coordinate1.getLat().doubleValue());
		Double lt2 = Math.toRadians(coordinate2.getLat().doubleValue());
		
		Double a = Math.pow(Math.sin(coordinateLat / 2),2) + Math.pow(Math.sin(coordinateLon / 2),2) * Math.cos(lt1) * Math.cos(lt2);
        Double c = 2 * Math.asin(Math.sqrt(a));
        
        return new DistanceCalculatorResult(new CoastlinePoint(coordinate2.getLat(), coordinate2.getLng(), null), EARTH_RADIUS_IN_MILES * c);
	}

}
