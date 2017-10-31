package com.kbs.geo.coastal.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.log4j.Logger;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.coastal.model.LatLng;

/**
 * Calculates the distance in miles between the target inland point and the closest virtual intersection between the min and the max of the three coastal points passed to constructor using standard Haversine formula
 * @author Steve
 *
 */
public class CloseDistanceCalculatorImpl implements DistanceCalculator {
	private static final MathContext MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);
	private static final Logger LOG = Logger.getLogger(CloseDistanceCalculatorImpl.class);
	private GeoCoordinate targetPoint;
	private GeoCoordinate coastlinePoint1;
	private GeoCoordinate coastlinePoint2;
	private GeoCoordinate coastlinePoint3;

	public CloseDistanceCalculatorImpl(GeoCoordinate targetPoint, GeoCoordinate coastlinePoint1, GeoCoordinate coastlinePoint2, GeoCoordinate coastlinePoint3) {
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
		if(coastlinePoint1.getLat().equals(coastlinePoint2.getLat()) && coastlinePoint1.getLng().equals(coastlinePoint2.getLng())) {
			LOG.error("coastlinePoint1 and coastlinePoint2 are the same point! Check the database for duplicate coastline points.");
			return null;
		}
		BigDecimal coastalSlope = getSlopeBetween(coastlinePoint1, coastlinePoint2);
		
		
		/*
		 * Next calculate the x,y intersection between the coastal point and the target point
		 */
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
		
//		yc = mxc + [y2 - mx2]
		
		BigDecimal intersectionLng = coastalSlope.multiply(intersectionLat).add(coastlinePoint2.getLng().subtract(coastalSlope.multiply(coastlinePoint2.getLat())));
		/*BigDecimal intersectionLng = coastalSlopeSquared.multiply(yr)
				.add(
						coastalSlope.multiply(
								xr.subtract(x1, MATH_CONTEXT), MATH_CONTEXT)
								.add(y1, MATH_CONTEXT), MATH_CONTEXT)
								.divide(coastalSlopeSquaredPlus1, MATH_CONTEXT);*/
		
		LOG.debug(String.format("m = %s", String.valueOf(coastalSlope)));
		LOG.debug(String.format("m-squared = %s", String.valueOf(coastalSlopeSquared)));
		LOG.debug(String.format("x1, y1 = %s, %s", String.valueOf(x1), String.valueOf(y1)));
		LOG.debug(String.format("xr, yr = %s, %s", String.valueOf(xr), String.valueOf(yr)));
		LOG.debug("Intersection lat = " + String.format("(%f * %f + %f * (%f - %f) + %f) / (%f + 1)", coastalSlopeSquared, x1, coastalSlope, yr, y1, xr, coastalSlopeSquared));
		LOG.debug("Intersection lng = " + String.format("(%f * %f + %f * (%f - %f) + %f) / (%f + 1)", coastalSlopeSquared, yr, coastalSlope, xr, x1, y1, coastalSlopeSquared));
		
		LOG.debug(String.format("Intersection: %s, %s", String.valueOf(intersectionLat), String.valueOf(intersectionLng)));
		
		/*
		 * Now verify the intersection point is actually on the line but between the two coastal points
		 */
		LatLng intersectionPoint = new LatLng(intersectionLat, intersectionLng);
		if(isPointBetween(coastlinePoint1, coastlinePoint2, intersectionPoint)) {
			DistanceCalculatorResult result =  new DefaultDistanceCalculatorImpl(targetPoint, intersectionPoint).calculate();
			result.getCoastlinePoint().setLat(intersectionLat);
			result.getCoastlinePoint().setLng(intersectionLng);
			return result;
		}
		return null;
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
	
/*	protected Boolean isPointBetween(GeoCoordinate boundaryPoint1, GeoCoordinate boundaryPoint2, GeoCoordinate targetPoint) {
		BigDecimal crossProduct = targetPoint.getLng().subtract(boundaryPoint1.getLng()).multiply(
				boundaryPoint2.getLat().subtract(boundaryPoint1.getLat())
				).subtract(
						targetPoint.getLat().subtract(boundaryPoint1.getLat())
						.multiply(boundaryPoint2.getLng().subtract(boundaryPoint1.getLng()))
						);
		
	    if(crossProduct.abs().compareTo(BigDecimal.ZERO) > 0) return Boolean.FALSE;

	    BigDecimal dotProduct = targetPoint.getLat().subtract(boundaryPoint1.getLat())
	    		.multiply(boundaryPoint2.getLat().subtract(boundaryPoint1.getLat()))
	    		.add(
	    				targetPoint.getLng().subtract(boundaryPoint1.getLng()).multiply(boundaryPoint2.getLng().subtract(boundaryPoint1.getLng())));
	    if(dotProduct.compareTo(BigDecimal.ZERO) < 0) return Boolean.FALSE;

	    BigDecimal squaredLengthBa = boundaryPoint2.getLat().subtract(boundaryPoint1.getLat()).pow(2)
	    		.add(boundaryPoint2.getLng().subtract(boundaryPoint1.getLng()).pow(2));
	    if(dotProduct.compareTo(squaredLengthBa) > 0) return Boolean.FALSE;
	    return Boolean.TRUE;
	}*/
	
	protected Boolean isPointBetween(GeoCoordinate boundaryPoint1, GeoCoordinate boundaryPoint2, GeoCoordinate targetPoint) {
		BigDecimal dx1 = boundaryPoint2.getLat().subtract(boundaryPoint1.getLat());
		BigDecimal dy1 = boundaryPoint2.getLng().subtract(boundaryPoint1.getLng());
		
		if(dx1.abs().compareTo( dy1.abs()) >= 0) {
			// Line is more horizontal than vertical
			if(dx1.compareTo(BigDecimal.ZERO) > 0) {
				return boundaryPoint1.getLat().compareTo(targetPoint.getLat()) <= 0 && targetPoint.getLat().compareTo(boundaryPoint2.getLat()) <= 0;
			}
			return boundaryPoint2.getLat().compareTo(targetPoint.getLat()) <= 0 && targetPoint.getLat().compareTo(boundaryPoint1.getLat()) <= 0;
		} else {
			// Line is more vertical than horizontal
			if(dy1.compareTo(BigDecimal.ZERO) > 0) {
				return boundaryPoint1.getLng().compareTo(targetPoint.getLng()) <= 0 && targetPoint.getLng().compareTo(boundaryPoint2.getLng()) <= 0;
			}
			return boundaryPoint2.getLng().compareTo(targetPoint.getLng()) <= 0 && targetPoint.getLng().compareTo(boundaryPoint1.getLng()) <= 0;
		}
		
/*		return targetLatAbs.compareTo(boundaryPoint2.getLat().abs()) >= 0
				&& targetLatAbs.compareTo(boundaryPoint1.getLat().abs()) <= 0
				&& targetLngAbs.compareTo(boundaryPoint2.getLng().abs()) >= 0
				&& targetLngAbs.compareTo(boundaryPoint1.getLng().abs()) <= 0;*/
	}
}
