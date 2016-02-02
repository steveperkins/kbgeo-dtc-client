package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.GridPointClientCoastlinePoint;

public class GridPointClientCoastlinePointRowMapper implements RowMapper<GridPointClientCoastlinePoint> {
	@Override
	public GridPointClientCoastlinePoint mapRow(ResultSet rs, int rowNum) throws SQLException {
		GridPointClientCoastlinePoint point = new GridPointClientCoastlinePoint();
		point.setClientId(rs.getInt("client_id"));
		point.setGridPointId(rs.getInt("grid_point_id"));
		point.setCoastlinePointId(rs.getInt("coastline_point_id"));
		point.setDistanceInMiles(rs.getDouble("distance_in_miles"));
		return point;
	}
	
}
