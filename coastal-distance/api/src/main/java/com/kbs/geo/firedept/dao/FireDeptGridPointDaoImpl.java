package com.kbs.geo.firedept.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.GridPoint128Dao;
import com.kbs.geo.coastal.dao.impl.AbstractDao;
import com.kbs.geo.coastal.dao.mapper.GridPointRowMapper;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.firedept.dao.mapper.FireGridPointRowMapper;
import com.kbs.geo.firedept.model.FireGridPoint;

@Component
public class FireDeptGridPointDaoImpl implements FireDeptGridPointDao {
	private static final Double BOUNDING_BOX_MILES_PER_SIDE = 128.0;
	private static final Double MILES_PER_LATITUDE = 69.0;  
	private static final Double BOUNDING_BOX_OFFSET_LAT = 1 / (MILES_PER_LATITUDE / BOUNDING_BOX_MILES_PER_SIDE);
	private static final Double MILES_PER_LONGITUDE = 53.0;
	private static final Double BOUNDING_BOX_OFFSET_LON = 1 / (MILES_PER_LONGITUDE / BOUNDING_BOX_MILES_PER_SIDE);
	
	private static final String INSERT_SQL = "INSERT INTO grid_point_%s (lat, lon, resolution) VALUES(?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE grid_point_%s SET lat=?, lon=?, fire_dept_id=?, distance_in_miles=? WHERE id=?";
	private static final String GET_BETWEEN_SQL = "SELECT * FROM grid_point_%s WHERE id BETWEEN ? AND ?";
	private static final String GET_OFFSET_SQL = "SELECT * FROM grid_point_%s where closest_coastline_point_id is null ORDER BY id LIMIT ?, ?";
	private static final String GET_ALL_SQL = "SELECT * FROM grid_point_%s";
	private static final String GET_POINTS_SURROUNDING_SQL = "SELECT * FROM grid_point_%s WHERE lat BETWEEN (? - " + BOUNDING_BOX_OFFSET_LAT + ") AND (? + " + BOUNDING_BOX_OFFSET_LAT + ") AND lon BETWEEN (? - " + BOUNDING_BOX_OFFSET_LON + ") AND (? + " + BOUNDING_BOX_OFFSET_LON + ")";
	
	private String readTableNameSuffix;
	private String writeTableNameSuffix;
	
	@Autowired
	private DataSource datasource;
	
	private RowMapper<FireGridPoint> rowMapper = new FireGridPointRowMapper();
	
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
	/* (non-Javadoc)
	 * @see com.kbs.geo.firedept.dao.FireDeptGridPointDao#getAll()
	 */
	@Override
	public List<FireGridPoint> getAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<FireGridPoint> gridPoints = jdbcTemplate.query(String.format(GET_ALL_SQL, readTableNameSuffix), rowMapper);
		return gridPoints;
	}
	
	
	/*@Override
	public List<FireGridPoint> getPointsSurrounding(LatLng coordinate) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<FireGridPoint> gridPoints = jdbcTemplate.query(GET_POINTS_SURROUNDING_SQL, new BigDecimal[]{ coordinate.getLat(), coordinate.getLat(), coordinate.getLng(), coordinate.getLng() }, rowMapper);
		return gridPoints;
	}*/
	
	/* (non-Javadoc)
	 * @see com.kbs.geo.firedept.dao.FireDeptGridPointDao#save(java.util.List)
	 */
	@Override
	public void save(List<FireGridPoint> points) {
		List<Object[]> params = new ArrayList<Object[]>();
		for(FireGridPoint point: points) {
			params.add(new Object [] { point.getLat(), point.getLng(), point.getResolution() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(String.format(INSERT_SQL, writeTableNameSuffix), params);
	}
	
	/* (non-Javadoc)
	 * @see com.kbs.geo.firedept.dao.FireDeptGridPointDao#update(java.util.List)
	 */
	@Override
	public void update(List<FireGridPoint> points) {
		List<Object[]> params = new ArrayList<Object[]>();
		int x;
		for(x = 0; x < points.size(); x++) {
			FireGridPoint point = points.get(x);
			System.out.println("Processing point " + point.getId());
			params.add(new Object [] { point.getLat(), point.getLng(), point.getClosestFireDeptId(), roundToSeventeenPlaces(point.getDistanceInMiles()), point.getId() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(String.format(UPDATE_SQL, writeTableNameSuffix), params);
	}
	
	
	private Double roundToSeventeenPlaces(Double value) {
		return new BigDecimal(value).setScale(17, RoundingMode.HALF_EVEN).doubleValue();
	}

	/* (non-Javadoc)
	 * @see com.kbs.geo.firedept.dao.FireDeptGridPointDao#setTableNameSuffix(java.lang.String)
	 */
	@Override
	public void setReadTableNameSuffix(String tableNameSuffix) {
		this.readTableNameSuffix = tableNameSuffix;
	}

	@Override
	public void setWriteTableNameSuffix(String tableNameSuffix) {
		this.writeTableNameSuffix = tableNameSuffix;
	}

}
