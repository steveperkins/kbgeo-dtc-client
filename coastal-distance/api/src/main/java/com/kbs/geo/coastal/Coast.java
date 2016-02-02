package com.kbs.geo.coastal;

public enum Coast {
	US_EAST("US-EAST"),
	US_GULF("US-GULF"),
	US_WEST("US-WEST");
	
	private String value;
	private Coast(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
}
