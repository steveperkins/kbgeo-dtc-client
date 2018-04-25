package com.kbs.geo.firedept.model;


import javax.xml.bind.annotation.XmlRootElement;

import com.kbs.geo.coastal.model.GeoCoordinate;

@XmlRootElement
public class DistanceToFireStationResult {
	private Double distanceInMiles;
	private Double drivingDistanceInMiles;
	private Long drivingDurationInSeconds;
	private Long drivingDurationInSecondsTrafficAdjusted;
	private GeoCoordinate targetPoint;
	private FireDepartment fireDepartment;
	
	public Double getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}
	public Double getDrivingDistanceInMiles() {
		return drivingDistanceInMiles;
	}
	public void setDrivingDistanceInMiles(Double drivingDistanceInMiles) {
		this.drivingDistanceInMiles = drivingDistanceInMiles;
	}
	public Long getDrivingDurationInSeconds() {
		return drivingDurationInSeconds;
	}
	public void setDrivingDurationInSeconds(Long drivingDurationInSeconds) {
		this.drivingDurationInSeconds = drivingDurationInSeconds;
	}
	public Long getDrivingDurationInSecondsTrafficAdjusted() {
		return drivingDurationInSecondsTrafficAdjusted;
	}
	public void setDrivingDurationInSecondsTrafficAdjusted(Long drivingDurationInSecondsTrafficAdjusted) {
		this.drivingDurationInSecondsTrafficAdjusted = drivingDurationInSecondsTrafficAdjusted;
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
