package com.kbs.geo.firedept.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.firedept.model.FireGridPoint;

public class FireGridPointRowMapper implements RowMapper<FireGridPoint> {
	
	@Override
	public FireGridPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
		FireGridPoint point = new FireGridPoint();
		point.setId(rs.getInt("id"));
		point.setLat(rs.getBigDecimal("lat"));
		point.setLng(rs.getBigDecimal("lon"));
		point.setClosestFireDeptId(rs.getInt("fire_dept_id"));
		point.setDistanceInMiles(rs.getDouble("distance_in_miles"));
		return point;
	}

}
