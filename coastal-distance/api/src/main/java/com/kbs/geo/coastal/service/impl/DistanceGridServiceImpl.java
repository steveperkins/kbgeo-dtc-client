package com.kbs.geo.coastal.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.DistanceGridDao;
import com.kbs.geo.coastal.http.exception.InvalidLatLngException;
import com.kbs.geo.coastal.model.DistanceGridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.DistanceGridService;

@Service
public class DistanceGridServiceImpl implements DistanceGridService {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceGridServiceImpl.class);

	// Record only lat/lon within the continental US
	private static final Double MAX_LATITUDE = 49.065544;
	private static final Double MAX_LONGITUDE = -67.390137;
	private static final Double MIN_LATITUDE = 24.4809;
	private static final Double MIN_LONGITUDE = -124.942017;
	
	@Autowired
	DistanceGridDao distanceGridDao;

	@Override
	public DistanceGridPoint getNearestPoint(LatLng latLng) {
	/*	if(null == latLng) throw new InvalidLatLngException("No lat/long provided");
		if(null == latLng.getLat()) throw new InvalidLatLngException("Missing latitude");
		if(null == latLng.getLng()) throw new InvalidLatLngException("Missing longitude");
		if(latLng.getLat() < MIN_LATITUDE || latLng.getLat() > MAX_LATITUDE) throw new InvalidLatLngException("Latitude outside valid bounds");
		if(latLng.getLng() < MIN_LONGITUDE|| latLng.getLng() > MAX_LONGITUDE) throw new InvalidLatLngException("Longitude outside valid bounds");
		
		return distanceGridDao.getNearestPoint(latLng);*/
		return null;
	}
}
