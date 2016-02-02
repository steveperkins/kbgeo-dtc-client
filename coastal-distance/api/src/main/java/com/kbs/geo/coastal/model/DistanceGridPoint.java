package com.kbs.geo.coastal.model;

public class DistanceGridPoint implements Identifiable {
	private Integer id;
	private Double lat;
	private Double lon;
	private Double distanceMiles;
	private Integer closestCoastlinePointId;
	
	public DistanceGridPoint() {
	}

	public DistanceGridPoint(Double lat, Double lon, Double distanceMiles, Integer closestCoastlinePointId) {
		this.lat = lat;
		this.lon = lon;
		this.distanceMiles = distanceMiles;
		this.closestCoastlinePointId = closestCoastlinePointId;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getDistanceMiles() {
		return distanceMiles;
	}
	public void setDistanceMiles(Double distanceMiles) {
		this.distanceMiles = distanceMiles;
	}

	public Integer getClosestCoastlinePointId() {
		return closestCoastlinePointId;
	}

	public void setClosestCoastlinePointId(Integer closestCoastlinePointId) {
		this.closestCoastlinePointId = closestCoastlinePointId;
	}
	
}
