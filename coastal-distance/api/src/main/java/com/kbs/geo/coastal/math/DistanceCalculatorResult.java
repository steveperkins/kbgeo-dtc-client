package com.kbs.geo.coastal.math;

import com.kbs.geo.coastal.model.CoastlinePoint;

public class DistanceCalculatorResult {
	private CoastlinePoint coastlinePoint;
	private Double distanceInMiles;
	
	public DistanceCalculatorResult(CoastlinePoint coastlinePoint, Double distanceInMiles) {
		this.coastlinePoint = coastlinePoint;
		this.distanceInMiles = distanceInMiles;
	}
	
	public CoastlinePoint getCoastlinePoint() {
		return coastlinePoint;
	}
	public void setCoastlinePoint(CoastlinePoint coastlinePoint) {
		this.coastlinePoint = coastlinePoint;
	}
	public Double getDistanceInMiles() {
		return distanceInMiles;
	}
	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}
}
