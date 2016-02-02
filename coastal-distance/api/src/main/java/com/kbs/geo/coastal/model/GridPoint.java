package com.kbs.geo.coastal.model;

import java.math.BigDecimal;

public class GridPoint implements Identifiable, GeoCoordinate {
	private Integer id;
	private Integer clientId;
	private BigDecimal lat;
	private BigDecimal lon;
	private Double distanceInMiles;
	private Integer closestCoastlinePointId;
	
	public GridPoint() { }

	public GridPoint(BigDecimal lat, BigDecimal lon, Integer closestCoastlinePointId) {
		this.lat = lat;
		this.lon = lon;
		this.closestCoastlinePointId = closestCoastlinePointId;
	}
	
	public GridPoint(Double lat, Double lon, Integer closestCoastlinePointId) {
		this(new BigDecimal(lat), new BigDecimal(lon), closestCoastlinePointId);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getLat() {
		return lat;
	}
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	public BigDecimal getLng() {
		return lon;
	}
	public void setLng(BigDecimal lng) {
		this.lon = lng;
	}
	public Integer getClosestCoastlinePointId() {
		return closestCoastlinePointId;
	}
	public void setClosestCoastlinePointId(Integer closestCoastlinePointId) {
		this.closestCoastlinePointId = closestCoastlinePointId;
	}
	
	public Double getDistanceInMiles() {
		return distanceInMiles;
	}

	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}

	@Override
	public String toString() {
		return String.format("[%f,%f]", lat, lon);
	}
	
}
