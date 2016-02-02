package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.GridPoint;

public class GridPointRowMapper implements RowMapper<GridPoint> {
	@Override
	public GridPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
		GridPoint point = new GridPoint();
		point.setId(rs.getInt("id"));
		point.setLat(rs.getBigDecimal("lat"));
		point.setLng(rs.getBigDecimal("lon"));
		return point;
	}

}
