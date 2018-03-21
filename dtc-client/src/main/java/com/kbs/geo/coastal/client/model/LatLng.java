package com.kbs.geo.coastal.client.model;

import java.math.BigDecimal;

/**
 * A latitude/longitude pair representing a given point on Earth
 * @author http://www.kbgeo.com
 *
 */
public class LatLng {
	private BigDecimal lat;
	private BigDecimal lng;
	
	public LatLng() {}
	public LatLng(BigDecimal lat, BigDecimal lng) {
		this.lat = lat;
		this.lng = lng;
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
}
