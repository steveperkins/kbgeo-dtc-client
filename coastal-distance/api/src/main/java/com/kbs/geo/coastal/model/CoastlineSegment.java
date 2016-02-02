package com.kbs.geo.coastal.model;

/**
 * coastline_segment
 * @author Steve
 *
 */
public class CoastlineSegment implements Identifiable {
	private Integer id;
	private String coast;
	private String description;
	private Double sortOrder;
	
	public CoastlineSegment() { }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCoast() {
		return coast;
	}

	public void setCoast(String coast) {
		this.coast = coast;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Double sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
