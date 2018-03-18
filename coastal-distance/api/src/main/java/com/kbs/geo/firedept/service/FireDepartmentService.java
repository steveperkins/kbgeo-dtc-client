package com.kbs.geo.firedept.service;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.firedept.model.DistanceToFireStationResult;

public interface FireDepartmentService {
	DistanceToFireStationResult getNearestFireDept(GeoCoordinate targetPoint);
}
