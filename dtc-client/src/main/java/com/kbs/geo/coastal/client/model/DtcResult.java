package com.kbs.geo.coastal.client.model;

/**
 * The result of a Distance to Coast request
 * @author http://www.kbgeo.com
 *
 */
public class DtcResult {
	private double distanceInMiles;
	private LatLng targetPoint;
	private LatLng coastlinePoint;
	
	public DtcResult() {}
	public DtcResult(double distanceInMiles, LatLng targetPoint, LatLng coastlinePoint) {
		this.distanceInMiles = distanceInMiles;
		this.targetPoint = targetPoint;
		this.coastlinePoint = coastlinePoint;
	}
	/**
	 * @return distance, in miles, between the target (inland) point and the nearest point on the coast (coastline point)
	 */
	public double getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(double distanceInMiles) {
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
