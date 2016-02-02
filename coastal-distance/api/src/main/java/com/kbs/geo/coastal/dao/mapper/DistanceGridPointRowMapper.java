package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.DistanceGridPoint;

public class DistanceGridPointRowMapper implements RowMapper<DistanceGridPoint> {
	
	@Override
	public DistanceGridPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
		DistanceGridPoint gridPoint = new DistanceGridPoint();
		gridPoint.setId(rs.getInt("id"));
		gridPoint.setDistanceMiles(rs.getDouble("distance_miles"));
		gridPoint.setLat(rs.getDouble("lat"));
		gridPoint.setLon(rs.getDouble("lon"));
		return gridPoint;
	}

}
