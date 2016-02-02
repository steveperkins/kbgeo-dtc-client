package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.CoastlinePoint;

public class CoastlinePointRowMapper implements RowMapper<CoastlinePoint> {
	
	@Override
	public CoastlinePoint mapRow(ResultSet rs, int rowNum) throws SQLException {
		CoastlinePoint point = new CoastlinePoint();
		point.setId(rs.getInt("id"));
		point.setLat(rs.getBigDecimal("lat"));
		point.setLng(rs.getBigDecimal("lon"));
		point.setSortOrder(rs.getDouble("sort_order"));
		return point;
	}

}
