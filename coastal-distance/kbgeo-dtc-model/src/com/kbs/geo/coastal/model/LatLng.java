package com.kbs.geo.coastal.model;


import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LatLng implements Serializable, GeoCoordinate {
	private static final long serialVersionUID = 1L;
	private BigDecimal lat;
	private BigDecimal lng;
	
	public LatLng() {};
	
	public LatLng(BigDecimal lat, BigDecimal lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public LatLng(Double lat, Double lng) {
		this.lat = new BigDecimal(lat);
		this.lng = new BigDecimal(lng);
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
	public void setLng(BigDecimal lon) {
		this.lng = lon;
	}

	@Override
	public String toString() {
		return "[lat=" + (null == lat ? "null" : lat.doubleValue()) + ", lng=" + (null == lat ? "null" : lng.doubleValue()) + "]";
	}
	
}
