package com.kbs.geo.firedept.model;

public class DrivingDistanceResult {
	private Long durationSeconds;
	private Long durationAdjustedForTraffic;
	private Double distanceMiles;
	
	public DrivingDistanceResult() {
		this(0l, 0l, 0.0);
	}
	
	public DrivingDistanceResult(Long durationSeconds, Long durationAdjustedForTraffic, Double distanceMiles) {
		this.durationSeconds = durationSeconds;
		this.durationAdjustedForTraffic = durationAdjustedForTraffic;
		this.distanceMiles = distanceMiles;
	}
	
	public Long getDurationSeconds() {
		return durationSeconds;
	}
	public void setDurationSeconds(Long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
	public Double getDistanceMiles() {
		return distanceMiles;
	}
	public void setDistanceMiles(Double distanceMiles) {
		this.distanceMiles = distanceMiles;
	}
	public Long getDurationAdjustedForTraffic() {
		return durationAdjustedForTraffic;
	}
	public void setDurationAdjustedForTraffic(Long durationAdjustedForTraffic) {
		this.durationAdjustedForTraffic = durationAdjustedForTraffic;
	}

}
