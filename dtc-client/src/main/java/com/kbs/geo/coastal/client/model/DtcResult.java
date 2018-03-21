package com.kbs.geo.coastal.client.model;

import java.math.BigDecimal;

/**
 * The result of a Distance to Coast request
 * @author http://www.kbgeo.com
 *
 */
public class DtcResult {
	private BigDecimal distanceInMiles;
	private LatLng targetPoint;
	private LatLng coastlinePoint;
	
	public DtcResult() {}
	public DtcResult(BigDecimal distanceInMiles, LatLng targetPoint, LatLng coastlinePoint) {
		this.distanceInMiles = distanceInMiles;
		this.targetPoint = targetPoint;
		this.coastlinePoint = coastlinePoint;
	}
	/**
	 * @return distance, in miles, between the target (inland) point and the nearest point on the coast (coastline point)
	 */
	public BigDecimal getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(BigDecimal distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}
	/**
	 * @return the inland point from which to measure the distance to the nearest coast
	 */
	public LatLng getTargetPoint() {
		return targetPoint;
	}
	public void setTargetPoint(LatLng targetPoint) {
		this.targetPoint = targetPoint;
	}
	/**
	 * @return the point on the coastline nearest to the target point
	 */
	public LatLng getCoastlinePoint() {
		return coastlinePoint;
	}
	public void setCoastlinePoint(LatLng coastlinePoint) {
		this.coastlinePoint = coastlinePoint;
	}
}
