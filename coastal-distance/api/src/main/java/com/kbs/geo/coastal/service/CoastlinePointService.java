package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.DistanceToCoastResult;
import com.kbs.geo.coastal.model.LatLng;

public interface CoastlinePointService {
	Integer save(CoastlinePoint point);
	CoastlinePoint get(Integer id);
	List<CoastlinePoint> getAll(Integer clientId);
	List<CoastlinePoint> getBetween(Integer clientId, Double startOrder, Double endOrder);
	DistanceToCoastResult getDistanceToCoast(LatLng targetPoint);
	Double getMilesBetween(LatLng targetPoint, CoastlinePoint coastlinePoint);
}
