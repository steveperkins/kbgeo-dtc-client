package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.GridPointClientCoastlinePoint;

public interface GridPointClientCoastlinePointDao {
	GridPointClientCoastlinePoint get(Integer clientId, Integer gridPointId);
	void save(GridPointClientCoastlinePoint point);
	void save(List<GridPointClientCoastlinePoint> points);
	void update(List<GridPointClientCoastlinePoint> points);
	List<GridPointClientCoastlinePoint> getAll();
}
