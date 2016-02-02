package com.kbs.geo.coastal.model;

import java.io.Serializable;

public class GeocodeResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String line1;
	private String line2;
	private String city;
	private String county;
	private String state;
	private String zip;
	private Double lat;
	private Double lng;
	private String fullAddress;
	
	public String getLine1() {
		return line1;
	}
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	public String getLine2() {
		return line2;
	}
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state.toUpperCase();
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getStandardizedAddress() {
		String str = String.format("%s %s%s %s", (null == line1 ? "" : line1.toUpperCase()), (null == line2 ? "" : line2.toUpperCase() + " "), (null == city ? "" : city.toUpperCase() + " ,"), (null == state ? "" : state.toUpperCase()));
		if(null != zip && !"".equals(zip.trim())) str += " " + zip;
		return str.trim();
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public String toString() {
		StringBuilder addressStr = new StringBuilder(getLine1());
		if(isNotBlank(getCity())) addressStr.append(",").append(getCity());
		if(isNotBlank(getState())) addressStr.append(" ").append(getState());
		if(isNotBlank(getZip())) addressStr.append(" ").append(getZip());
		return addressStr.toString();
	}
	private Boolean isNotBlank(String str) {
		return null != str && !"".equals(str.trim());
	}
	public String getFullAddress() {
		return fullAddress;
	}
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
}
