package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.MinMaxCoastlinePointSortOrder;

public class MinMaxCoastlinePointSortOrderRowMapper implements RowMapper<MinMaxCoastlinePointSortOrder> {
	@Override
	public MinMaxCoastlinePointSortOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
		MinMaxCoastlinePointSortOrder boundarySortOrder = new MinMaxCoastlinePointSortOrder();
		boundarySortOrder.setMin(rs.getDouble("min_sort_order"));
		boundarySortOrder.setMax(rs.getDouble("max_sort_order"));
		return boundarySortOrder;
	}

}
