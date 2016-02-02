package com.kbs.geo.coastal.model;

public class MinMaxCoastlinePointSortOrder {
	private Double min;
	private Double max;
	
	public MinMaxCoastlinePointSortOrder() { }

	public MinMaxCoastlinePointSortOrder(Double min, Double max) {
		this.min = min;
		this.max = max;
	}
	
	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	@Override
	public String toString() {
		return String.format("[%d to %d]", min, max);
	}
	
}
