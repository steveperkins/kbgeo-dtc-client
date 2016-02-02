package com.kbs.geo.coastal.model;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DistanceToCoastResult {
	private Double distanceInMiles;
	private LatLng targetPoint;
	private CoastlinePoint coastlinePoint;
	
	public Double getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}
	public LatLng getTargetPoint() {
		return targetPoint;
	}
	public void setTargetPoint(LatLng targetPoint) {
		this.targetPoint = targetPoint;
	}
	public CoastlinePoint getCoastlinePoint() {
		return coastlinePoint;
	}
	public void setCoastlinePoint(CoastlinePoint coastlinePoint) {
		this.coastlinePoint = coastlinePoint;
	}
	
}
