package com.kbs.geo.coastal.client.model;

/**
 * A latitude/longitude pair representing a given point on Earth
 * @author http://www.kbgeo.com
 *
 */
public class LatLng {
	private double lat;
	private double lng;
	
	public LatLng() {}
	public LatLng(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
}
