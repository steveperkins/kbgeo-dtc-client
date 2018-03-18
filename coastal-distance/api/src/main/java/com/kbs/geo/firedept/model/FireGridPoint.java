package com.kbs.geo.firedept.model;

import java.math.BigDecimal;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.coastal.model.Identifiable;

public class FireGridPoint implements Identifiable, GeoCoordinate {
	private Integer id;
	private Integer clientId;
	private BigDecimal lat;
	private BigDecimal lng;
	private Double distanceInMiles;
	private Integer closestFireDeptId;
	private String resolution;
	
	public FireGridPoint() { }

	public FireGridPoint(BigDecimal lat, BigDecimal lng, Integer closestFireDeptId) {
		this.lat = lat;
		this.lng = lng;
		this.closestFireDeptId = closestFireDeptId;
	}
	
	public FireGridPoint(Double lat, Double lon, Integer closestFireDeptId) {
		this(new BigDecimal(lat), new BigDecimal(lon), closestFireDeptId);
	}
	
	public FireGridPoint(BigDecimal lat, BigDecimal lng, String resolution) {
		this.lat = lat;
		this.lng = lng;
		this.resolution = resolution;
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
		return lng;
	}
	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}
	public Integer getClosestFireDeptId() {
		return closestFireDeptId;
	}
	public void setClosestFireDeptId(Integer closestFireDeptId) {
		this.closestFireDeptId = closestFireDeptId;
	}
	
	public Double getDistanceInMiles() {
		return distanceInMiles;
	}

	public void setDistanceInMiles(Double distanceInMiles) {
		this.distanceInMiles = distanceInMiles;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	@Override
	public String toString() {
		return String.format("[%f,%f]", lat, lng);
	}
	
}
