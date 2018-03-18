package com.kbs.geo.firedept.model;

import java.math.BigDecimal;

import com.kbs.geo.coastal.model.GeoCoordinate;

public class FireDepartment implements GeoCoordinate {
	private Integer id;
	private String stateId;
	private String name;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String county;
	private String staffType;
	private String organizationType;
	private String website;
	private int stationCount;
	private int careerFirefighterCount;
	private int volunteerFirefighterCount;
	private int paidPerCallFirefighterCount;
	private int paidCivilianCount;
	private int volunteerCivilianCount;
	private Boolean isEmergencyManagementPrimary = Boolean.FALSE;
	private BigDecimal lat;
	private BigDecimal lng;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStateId() {
		return stateId;
	}
	public void setStateId(String stateId) {
		this.stateId = stateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getStaffType() {
		return staffType;
	}
	public void setStaffType(String staffType) {
		this.staffType = staffType;
	}
	public String getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public int getStationCount() {
		return stationCount;
	}
	public void setStationCount(int stationCount) {
		this.stationCount = stationCount;
	}
	public int getCareerFirefighterCount() {
		return careerFirefighterCount;
	}
	public void setCareerFirefighterCount(int careerFirefighterCount) {
		this.careerFirefighterCount = careerFirefighterCount;
	}
	public int getVolunteerFirefighterCount() {
		return volunteerFirefighterCount;
	}
	public void setVolunteerFirefighterCount(int volunteerFirefighterCount) {
		this.volunteerFirefighterCount = volunteerFirefighterCount;
	}
	public int getPaidPerCallFirefighterCount() {
		return paidPerCallFirefighterCount;
	}
	public void setPaidPerCallFirefighterCount(int paidPerCallFirefighterCount) {
		this.paidPerCallFirefighterCount = paidPerCallFirefighterCount;
	}
	public int getPaidCivilianCount() {
		return paidCivilianCount;
	}
	public void setPaidCivilianCount(int paidCivilianCount) {
		this.paidCivilianCount = paidCivilianCount;
	}
	public int getVolunteerCivilianCount() {
		return volunteerCivilianCount;
	}
	public void setVolunteerCivilianCount(int volunteerCivilianCount) {
		this.volunteerCivilianCount = volunteerCivilianCount;
	}
	public Boolean getIsEmergencyManagementPrimary() {
		return isEmergencyManagementPrimary;
	}
	public void setIsEmergencyManagementPrimary(Boolean isEmergencyManagementPrimary) {
		this.isEmergencyManagementPrimary = isEmergencyManagementPrimary;
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
