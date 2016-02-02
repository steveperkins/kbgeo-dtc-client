package com.kbs.geo.coastal.validator;

import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.http.exception.InvalidLatLngException;
import com.kbs.geo.coastal.model.LatLng;

@Component
public class LatLngValidator {
	private static final Double MIN_LAT = -90.0;
	private static final Double MAX_LAT = 90.0;
	private static final Double MIN_LNG = -180.0;
	private static final Double MAX_LNG = 180.0;
	
	public void validate(LatLng latLng) {
		if(null == latLng) throw new InvalidLatLngException("Null latLng");
		if(null == latLng.getLat() || null == latLng.getLng()) throw new InvalidLatLngException("Both lat and lng must both be provided");
		if(latLng.getLat().doubleValue() < MIN_LAT || latLng.getLat().doubleValue() > MAX_LAT) throw new InvalidLatLngException("Provided lat is outside possible latitude range");
		if(latLng.getLng().doubleValue() < MIN_LNG || latLng.getLng().doubleValue() > MAX_LNG) throw new InvalidLatLngException("Provided lng is outside possible longitude range");
	}
}
