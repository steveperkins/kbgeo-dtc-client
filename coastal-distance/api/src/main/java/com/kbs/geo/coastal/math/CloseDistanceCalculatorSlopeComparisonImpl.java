package com.kbs.geo.coastal.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.coastal.model.LatLng;

/**
 * Calculates the distance in miles between the target inland point and the closest virtual intersection between the min and the max of the three coastal points passed to constructor using standard Haversine formula
 * @author Steve
 *
 */
public class CloseDistanceCalculatorSlopeComparisonImpl implements DistanceCalculator {
	private static final MathContext MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);
	private final Logger LOG = LoggerFactory.getLogger(CloseDistanceCalculatorSlopeComparisonImpl.class);
	private GeoCoordinate targetPoint;
	private GeoCoordinate coastlinePoint1;
	private GeoCoordinate coastlinePoint2;
	private GeoCoordinate coastlinePoint3;

	public CloseDistanceCalculatorSlopeComparisonImpl(GeoCoordinate targetPoint, GeoCoordinate coastlinePoint1, GeoCoordinate coastlinePoint2, GeoCoordinate coastlinePoint3) {
		this.targetPoint = targetPoint;
		this.coastlinePoint1 = coastlinePoint1;
		this.coastlinePoint2 = coastlinePoint2;
		this.coastlinePoint3 = coastlinePoint3;
	}

	@Override
	public DistanceCalculatorResult calculate() {
		/*
		 * Formula is:
		 * Xc = [m2x1 + m(yr – y1) +xr] / (m2+1)
		 * Yc = [m2yr + m(xr – x1) +y1] / (m2+1)
		 * 
		 * where
		 * m = (y2 – y1)/(x2 – x1)
		 * 
		 * x == longitude
		 * y == latitude
		 * 
		 * x1 == coastlinePoint1.lng
		 * y1 == coastlinePoint1.lat
		 * 
		 * x2 == coastlinePoint2.lng
		 * y2 == coastlinePoint2.lat
		 * 
		 * xr == targetPoint.lng
		 * yr == targetPoint.lat
		 */
		
		/*
		 * We will perform one calculation per pair of coastline points, then take the smallest result
		 */
//		LOG.debug("Calculating close distance for {} to {} ({} to {}", new Object[] {coastlinePoint1.getId(), coastlinePoint2.getId(), coastlinePoint1.toString(), coastlinePoint2.toString()});
		DistanceCalculatorResult distance1 = findIntersection(targetPoint, coastlinePoint1, coastlinePoint2);
//		LOG.debug("Calculating close distance for {} to {} ({} to {}", new Object[] {coastlinePoint2.getId(), coastlinePoint3.getId(), coastlinePoint2.toString(), coastlinePoint3.toString()});
		DistanceCalculatorResult distance2 = findIntersection(targetPoint, coastlinePoint2, coastlinePoint3);
		
		// If no intersection could be found, return null
		if(null == distance1 && null == distance2) return null;
		// If there was no intersection with the first two coastline points but there was with the second two, return the result of the second two points
		if(null == distance1 && null != distance2) return distance2;
		// If there was an intersection with the first two coastline points but there wasn't with the second two, return the result of the first two points
		if(null != distance1 && null == distance2) return distance1;
		
		// If there was an intersection with both sets of points, return the closest intersection 
		if(distance1.getDistanceInMiles() < distance2.getDistanceInMiles()) return distance1;
		return distance2;
	}
	
	/*
	 * Calculates the point between <code>coastlinePoint1</code> and <code>coastlinePoint2</code> that is perpendicular to the <code>targetPoint</code>.
	 * If such a point does not exist, <code>null</code> is returned.
	 */
	protected DistanceCalculatorResult findIntersection(GeoCoordinate targetPoint, GeoCoordinate coastlinePoint1, GeoCoordinate coastlinePoint2) {
		/*
		 * First find the slope between the two coastal points
		 */
		LOG.debug(String.format("targetPoint = %s\r\ncoastlinePoint1 = %s\r\ncoastlinePoint2 = %s", targetPoint, coastlinePoint1, coastlinePoint2));
		BigDecimal coastalSlope = getSlopeBetween(coastlinePoint1, coastlinePoint2);
		
		/*
		 * Now calculate the slope perpendicular to the known coastal slope  
		 */
		BigDecimal coastalPerpendicularSlope = getPerpendicularSlope(coastalSlope);
		
		/*
		 * Then find the slope between each coastal point and the target point
		 */
		BigDecimal targetToCoastline1Slope = getSlopeBetween(coastlinePoint1, targetPoint);
		BigDecimal targetToCoastline2Slope = getSlopeBetween(coastlinePoint2, targetPoint);
		
		/*
		 * Now determine whether the perpendicular slope is between the two target-coastline slopes. If so, the nearest intersection point 
		 * is between these two coastline points. If not, stop processing because we don't care anymore. 
		 */
		BigDecimal absCoastalPerpendicularSlope = coastalPerpendicularSlope.abs();
		if(absCoastalPerpendicularSlope.compareTo(targetToCoastline1Slope) > 0  && absCoastalPerpendicularSlope.compareTo(targetToCoastline2Slope) < 0) {
			BigDecimal coastalSlopeSquared = coastalSlope.pow(2, MATH_CONTEXT);
			BigDecimal coastalSlopeSquaredPlus1 = coastalSlopeSquared.add(BigDecimal.ONE, MATH_CONTEXT);
			
			BigDecimal x1 = coastlinePoint1.getLat();
			BigDecimal y1 = coastlinePoint1.getLng();
			
			BigDecimal xr = targetPoint.getLat();
			BigDecimal yr = targetPoint.getLng();
			
			BigDecimal intersectionLat = 
					coastalSlopeSquared.multiply(x1, MATH_CONTEXT)
					.add(
							coastalSlope.multiply(yr.subtract(y1, MATH_CONTEXT))
							.add(xr, MATH_CONTEXT), MATH_CONTEXT)
							.divide(coastalSlopeSquaredPlus1, MATH_CONTEXT);
			
			BigDecimal intersectionLng = coastalSlopeSquared.multiply(yr)
					.add(
							coastalSlope.multiply(
									xr.subtract(x1, MATH_CONTEXT), MATH_CONTEXT)
									.add(y1, MATH_CONTEXT), MATH_CONTEXT)
									.divide(coastalSlopeSquaredPlus1, MATH_CONTEXT);
			
//			BigDecimal intersectionLat = (coastalSlopeSquared * x1 + coastalSlope * (yr - y1) + xr) / (coastalSlopeSquared + 1);
//			BigDecimal intersectionLng = (coastalSlopeSquared * yr + coastalSlope * (xr - x1) + y1) / (coastalSlopeSquared + 1);
			
			LOG.debug("m = {}", coastalSlope);
			LOG.debug("m-squared = {}", coastalSlopeSquared);
			LOG.debug("x1, y1 = {}, {}", x1, y1);
			LOG.debug("xr, yr = {}, {}", xr, yr);
			LOG.debug("Intersection lat = {}", String.format("(%f * %f + %f * (%f - %f) + %f) / (%f + 1)", coastalSlopeSquared, x1, coastalSlope, yr, y1, xr, coastalSlopeSquared));
			LOG.debug("Intersection lng = {}", String.format("(%f * %f + %f * (%f - %f) + %f) / (%f + 1)", coastalSlopeSquared, yr, coastalSlope, xr, x1, y1, coastalSlopeSquared));
			
			LOG.debug("Intersection: {}, {}", intersectionLat, intersectionLng);
			
			DistanceCalculatorResult result =  new DefaultDistanceCalculatorImpl(targetPoint, new LatLng(intersectionLat, intersectionLng)).calculate();
			result.getCoastlinePoint().setLat(intersectionLat);
			result.getCoastlinePoint().setLng(intersectionLng);
			return result;
		} else {
			return null;
		}
		
	}

	protected BigDecimal getSlopeBetween(GeoCoordinate point1, GeoCoordinate point2) {
		BigDecimal x1 = point1.getLat();
		BigDecimal y1 = point1.getLng();
		
		BigDecimal x2 = point2.getLat();
		BigDecimal y2 = point2.getLng();
		
		BigDecimal slope = y2.subtract(y1, MATH_CONTEXT).divide(x2.subtract(x1, MATH_CONTEXT), MATH_CONTEXT);
		LOG.debug(String.format("Slope between %s and %s is %f = (%f - %f) / (%f - %f)", point1, point2, slope, y2, y1, x2, x1));
		return slope;
	}
	
	protected BigDecimal getPerpendicularSlope(BigDecimal slope) {
		if(slope.equals(BigDecimal.ZERO)) return slope;
		BigDecimal perpendicularSlope = new BigDecimal(-1).divide(slope, MATH_CONTEXT);
		LOG.debug(String.format("Perpendicular slope of %f is %f = -1 / %f", slope, perpendicularSlope, slope));
		return perpendicularSlope;
	}
	
	protected BigDecimal isSlopeBetween() {
		return null;
	}
}
