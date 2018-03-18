package com.kbs.geo.firedept.model;


import javax.xml.bind.annotation.XmlRootElement;

import com.kbs.geo.coastal.model.GeoCoordinate;

@XmlRootElement
public class DistanceToFireStationResult {
	private Double distanceInMiles;
	private GeoCoordinate targetPoint;
	private FireDepartment fireDepartment;
	
	public Double getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}
	public GeoCoordinate getTargetPoint() {
		return targetPoint;
	}
	public void setTargetPoint(GeoCoordinate targetPoint) {
		this.targetPoint = targetPoint;
	}
	public FireDepartment getFireDepartment() {
		return fireDepartment;
	}
	public void setFireDepartment(FireDepartment fireDepartment) {
		this.fireDepartment = fireDepartment;
	}
	
}
