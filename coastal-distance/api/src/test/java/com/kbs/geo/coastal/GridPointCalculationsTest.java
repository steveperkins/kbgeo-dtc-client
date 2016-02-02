package com.kbs.geo.coastal;

import static org.junit.Assert.*;

import org.junit.Test;

public class GridPointCalculationsTest {

	private static final Double EARTH_CIRCUMFERENCE_IN_STATUTE_MILES = 3963.1; // See http://www8.nau.edu/cvm/latlon_formula.html
	private static final Double MILES_PER_DEGREE = 57.295780; // was 60
	
	@Test
	public void test() {
		Double gridLat = 25.0000;
		Double gridLon = -95.1132;
		
		Double coastalLat = 25.767583;
		Double coastalLon = -97.159500;
		System.out.println(getMilesBetween(gridLat, gridLon, coastalLat, coastalLon));
	}
	
	
	
	
	
	protected Double getMilesBetween(Double gridLatitude, Double gridLongitude, Double coastalLatitude, Double coastalLongitude) {
        Double gridLat = Math.toRadians(gridLatitude);
        Double gridLon = Math.toRadians(gridLongitude);
        Double coastalLat = Math.toRadians(coastalLatitude);
        Double coastalLon = Math.toRadians(coastalLongitude);

       /*************************************************************************
        * Compute using law of cosines
        *************************************************************************/
        // great circle distance in radians
        Double angle1 = Math.acos(Math.sin(gridLat) * Math.sin(coastalLat)
                      + Math.cos(gridLat) * Math.cos(coastalLat) * Math.cos(gridLon - coastalLon));

        // convert back to degrees
        angle1 = Math.toDegrees(angle1);

        // each degree on a great circle of Earth is 60 nautical miles
//        Double distance1 = 60 * angle1;


       /*************************************************************************
        * Compute using Haversine formula
        *************************************************************************/
        Double a = Math.pow(Math.sin((coastalLat-gridLat)/2), 2)
                 + Math.cos(gridLat) * Math.cos(coastalLat) * Math.pow(Math.sin((coastalLon-gridLon)/2), 2);

        // great circle distance in radians. UsesMath.min to prevent rounding error as distances approach 12,000 km (http://www.movable-type.co.uk/scripts/gis-faq-5.1.html) 
        Double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        // convert back to degrees
        angle2 = Math.toDegrees(angle2);

        // each degree on a great circle of Earth is 60 nautical miles
        Double distanceInMiles = MILES_PER_DEGREE * angle2;

        return distanceInMiles;
    }
	
}
