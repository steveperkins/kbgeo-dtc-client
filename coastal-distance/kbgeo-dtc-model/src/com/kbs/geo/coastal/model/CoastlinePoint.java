package com.kbs.geo.coastal.model;


import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.kbs.geo.coastal.model.serializer.BigDecimalSerializer;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CoastlinePoint implements Identifiable, GeoCoordinate {
	@XmlTransient
	@JsonIgnore
	private Integer id;
	private BigDecimal lat;
	private BigDecimal lng;
	@JsonIgnore
	@XmlTransient
	private Double sortOrder;
	
	public CoastlinePoint() { }

	public CoastlinePoint(BigDecimal lat, BigDecimal lng, Double sortOrder) {
		this.lat = lat;
		this.lng = lng;
		this.setSortOrder(sortOrder);
	}
	
	public CoastlinePoint(Double lat, Double lng, Double sortOrder) {
		this(new BigDecimal(lat), new BigDecimal(lng), sortOrder);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Double getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Double sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	@Override
	public String toString() {
		return String.format("[%f,%f]", lat, lng);
	}
	
}
