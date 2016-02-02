package com.kbs.geo.coastal.model;

import java.math.BigDecimal;

/**
 * Represents a geographical coordinate with a latitude and a longitude
 * @author Steve
 *
 */
public interface GeoCoordinate {
	public BigDecimal getLat();
	public void setLat(BigDecimal lat);
	public BigDecimal getLng();
	public void setLng(BigDecimal lon);
}
