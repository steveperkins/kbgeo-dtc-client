package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.CoastlineSegment;

public interface CoastlineSegmentDao {
	CoastlineSegment get(Integer id);
	Integer save(CoastlineSegment point);
	List<CoastlineSegment> getAll();
	List<CoastlineSegment> getAll(Integer clientId);
}
