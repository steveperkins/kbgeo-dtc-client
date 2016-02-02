package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;

public interface GridPoint128Dao {
	Integer save(GridPoint point);
	void save(List<GridPoint> points);
	void update(List<GridPoint> points);
	List<GridPoint> getAll();
	List<GridPoint> getPointsBetween(Long startId, Long endId);
	List<GridPoint> getPointsSurrounding(LatLng latLng);
	List<GridPoint> getOffset(Long startRow, Long endRow);
}
