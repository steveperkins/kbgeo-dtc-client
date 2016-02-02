package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.DistanceGridPoint;
import com.kbs.geo.coastal.model.LatLng;

public interface DistanceGridDao {
	Integer save(DistanceGridPoint gridPoint);
	void save(List<DistanceGridPoint> gridPoints);
	
	DistanceGridPoint getNearestPoint(LatLng latLng);
}
