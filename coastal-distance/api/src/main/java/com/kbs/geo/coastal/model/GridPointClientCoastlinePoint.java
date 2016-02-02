package com.kbs.geo.coastal.model;

public class GridPointClientCoastlinePoint {
	private Integer clientId;
	private Integer gridPointId;
	private Integer coastlinePointId;
	private Double distanceInMiles;
	
	public GridPointClientCoastlinePoint() { }
	
	public GridPointClientCoastlinePoint(Integer clientId, Integer gridPointId, Integer coastlinePointId, Double distanceInMiles) {
		super();
		this.clientId = clientId;
		this.gridPointId = gridPointId;
		this.coastlinePointId = coastlinePointId;
		this.distanceInMiles = distanceInMiles;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getGridPointId() {
		return gridPointId;
	}

	public void setGridPointId(Integer gridPointId) {
		this.gridPointId = gridPointId;
	}

	public Integer getCoastlinePointId() {
		return coastlinePointId;
	}

	public void setCoastlinePointId(Integer coastlinePointId) {
		this.coastlinePointId = coastlinePointId;
	}

	public Double getDistanceInMiles() {
		return distanceInMiles;
	}

	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}

	@Override
	public String toString() {
		return String.format("[clientId=%d, gridPointId=%d, coastlinePointId=%d]", clientId, gridPointId, coastlinePointId);
	}
	
}
