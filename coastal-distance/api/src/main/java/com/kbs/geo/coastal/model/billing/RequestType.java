package com.kbs.geo.coastal.model.billing;

public enum RequestType {
	ERROR(1),
	DISTANCE_TO_COAST(2),
	// Requests to retrieve client console data
	CONSOLE(3);
	
	private Integer id;
	private RequestType(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public static RequestType fromId(Integer id) {
		for(RequestType t: values()) {
			if(t.getId().equals(id)) return t;
		}
		return null;
	}
}
