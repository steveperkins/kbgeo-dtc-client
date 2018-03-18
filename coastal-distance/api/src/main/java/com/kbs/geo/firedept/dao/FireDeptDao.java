package com.kbs.geo.firedept.dao;

import java.util.List;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.firedept.model.FireDepartment;

public interface FireDeptDao {
	FireDepartment get(Integer id);
	List<FireDepartment> getAll();
	List<FireDepartment> getNearestToPoint(GeoCoordinate point);
}
