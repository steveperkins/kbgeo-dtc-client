package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.Coast;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.model.MinMaxCoastlinePointSortOrder;

public interface GridPointDao {
	GridPoint get(Integer id);
	Integer save(GridPoint point);
	void save(List<GridPoint> points);
	void update(List<GridPoint> points);
	List<GridPoint> getAll();
	List<GridPoint> getPointsBetween(Integer clientId, Long startId, Long endId);
	List<GridPoint> getPointsSurrounding(Integer clientId, LatLng latLng);
	List<GridPoint> getOffset(Long startRow, Long endRow);
	MinMaxCoastlinePointSortOrder getBoundingCoastlinePointSortOrders(Integer clientId, LatLng coordinate);
	List<GridPoint> getPointsByCoast(Coast coast);
}
