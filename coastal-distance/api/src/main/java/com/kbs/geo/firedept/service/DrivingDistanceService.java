package com.kbs.geo.firedept.service;

import com.kbs.geo.coastal.http.exception.DistanceMatrixException;
import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.firedept.model.DrivingDistanceResult;

public interface DrivingDistanceService {

	DrivingDistanceResult getDistanceBetween(GeoCoordinate origin, GeoCoordinate destination)
			throws DistanceMatrixException;

}