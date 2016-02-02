package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.GridPoint;

public class ClientGridPointRowMapper implements RowMapper<GridPoint> {
	@Override
	public GridPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
		GridPoint point = new GridPoint();
		point.setId(rs.getInt("id"));
		point.setLat(rs.getBigDecimal("lat"));
		point.setLng(rs.getBigDecimal("lon"));
		point.setDistanceInMiles(rs.getDouble("distance_in_miles"));
		point.setClosestCoastlinePointId(rs.getInt("coastline_point_id"));
		return point;
	}

}
