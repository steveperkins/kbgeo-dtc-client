package com.kbs.geo.coastal.service;

import com.kbs.geo.coastal.model.DistanceGridPoint;
import com.kbs.geo.coastal.model.LatLng;

public interface DistanceGridService {
	DistanceGridPoint getNearestPoint(LatLng latLng);
}
