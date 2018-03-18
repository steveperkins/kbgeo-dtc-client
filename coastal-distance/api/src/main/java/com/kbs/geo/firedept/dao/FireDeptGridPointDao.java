package com.kbs.geo.firedept.dao;

import java.util.List;

import com.kbs.geo.firedept.model.FireGridPoint;

public interface FireDeptGridPointDao {

	/*	@Override
	public List<FireGridPoint> getPointsBetween(Long startId, Long endId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<FireGridPoint> gridPoints = jdbcTemplate.query(GET_UNPROCESSED_BETWEEN_SQL, new Object[]{ startId, endId }, rowMapper);
		return gridPoints;
	}
	
	@Override
	public List<FireGridPoint> getOffset(Long startRow, Long endRow) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<FireGridPoint> gridPoints = jdbcTemplate.query(GET_OFFSET_SQL, new Object[]{ startRow, endRow}, rowMapper);
		return gridPoints;
	}
	*/
	List<FireGridPoint> getAll();

	void save(List<FireGridPoint> points);

	void update(List<FireGridPoint> points);

	void setReadTableNameSuffix(String tableNameSuffix);
	void setWriteTableNameSuffix(String tableNameSuffix);

}