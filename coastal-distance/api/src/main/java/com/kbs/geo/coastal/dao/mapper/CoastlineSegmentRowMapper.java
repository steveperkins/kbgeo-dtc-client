package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.CoastlineSegment;

public class CoastlineSegmentRowMapper implements RowMapper<CoastlineSegment> {
	
	@Override
	public CoastlineSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
		CoastlineSegment segment = new CoastlineSegment();
		segment.setId(rs.getInt("id"));
		segment.setCoast(rs.getString("coast"));
		segment.setDescription(rs.getString("description"));
		segment.setSortOrder(rs.getDouble("sort_order"));
		return segment;
	}

}
