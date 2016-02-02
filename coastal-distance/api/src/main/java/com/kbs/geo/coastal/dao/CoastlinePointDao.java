package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.Coast;
import com.kbs.geo.coastal.model.CoastlinePoint;

public interface CoastlinePointDao {
	CoastlinePoint get(Integer id);
	Integer save(CoastlinePoint point);
	void save(List<CoastlinePoint> points);
	List<CoastlinePoint> getAll(Integer clientId);
	List<CoastlinePoint> getByCoast(Integer clientId, Coast coast);
	List<CoastlinePoint> getPointsBetween(Integer clientId, Double startOrder, Double endOrder);
	List<CoastlinePoint> getPointsBetweenSortOrders(Integer clientId, Double startOrder, Double endOrder);
	List<CoastlinePoint> getByClientSegment(Integer clientId, Integer segmentId);
	List<CoastlinePoint> getPointsBeforeAndAfter(Integer clientId, CoastlinePoint point);
}
