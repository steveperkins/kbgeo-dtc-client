package com.kbs.geo.coastal.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.GridPoint32Dao;
import com.kbs.geo.coastal.dao.mapper.GridPointRowMapper;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;

@Component
public class GridPoint32DaoImpl extends AbstractDao<GridPoint> implements GridPoint32Dao {
	private static final Double BOUNDING_BOX_MILES_PER_SIDE = 32.0;  
	private static final Double MILES_PER_LATITUDE = 69.0;  
	private static final Double BOUNDING_BOX_OFFSET_LAT = 1 / (MILES_PER_LATITUDE / BOUNDING_BOX_MILES_PER_SIDE);
	private static final Double MILES_PER_LONGITUDE = 53.0;
	private static final Double BOUNDING_BOX_OFFSET_LON = 1 / (MILES_PER_LONGITUDE / BOUNDING_BOX_MILES_PER_SIDE);
	
	private static final String INSERT_SQL = "INSERT INTO grid_point_32 (lat, lon, closest_coastline_point_id, distance_in_miles) VALUES(?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE grid_point_32 SET lat=?, lon=?, closest_coastline_point_id=?, distance_in_miles=? WHERE id=?";
	private static final String GET_BETWEEN_SQL = "SELECT * FROM grid_point_32 WHERE id BETWEEN ? AND ?";
	private static final String GET_OFFSET_SQL = "SELECT * FROM grid_point_32 ORDER BY id LIMIT ?, ?";
	private static final String GET_ALL_SQL = "SELECT * FROM grid_point_32";
	private static final String GET_POINTS_SURROUNDING_SQL = "SELECT * FROM grid_point_32 WHERE lat BETWEEN (? - " + BOUNDING_BOX_OFFSET_LAT + ") AND (? + " + BOUNDING_BOX_OFFSET_LAT + ") AND lon BETWEEN (? - " + BOUNDING_BOX_OFFSET_LON + ") AND (? + " + BOUNDING_BOX_OFFSET_LON + ")";
	
	@Autowired
	private DataSource datasource;
	
	public GridPoint32DaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public List<GridPoint> getPointsBetween(Long startId, Long endId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_BETWEEN_SQL, new Object[]{ startId, endId }, new GridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getOffset(Long startRow, Long endRow) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_OFFSET_SQL, new Object[]{ startRow, endRow}, new GridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_ALL_SQL, new GridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getPointsSurrounding(LatLng coordinate) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_POINTS_SURROUNDING_SQL, new BigDecimal[]{ coordinate.getLat(), coordinate.getLat(), coordinate.getLng(), coordinate.getLng() }, new GridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public void save(List<GridPoint> points) {
		List<Object[]> params = new ArrayList<Object[]>();
		for(GridPoint point: points) {
			params.add(new Object [] { 
					point.getLat(), 
					point.getLng(),
					point.getClosestCoastlinePointId(), 
					(null != point.getDistanceInMiles() ? roundToSixPlaces(point.getDistanceInMiles()) : null)});
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(INSERT_SQL, params);
	}
	
	@Override
	public void update(List<GridPoint> points) {
		List<Object[]> params = new ArrayList<Object[]>();
		int x;
		for(x = 0; x < points.size(); x++) {
			GridPoint point = points.get(x);
			System.out.println("Processing point " + point.getId());
			params.add(new Object [] { point.getLat(), point.getLng(), point.getClosestCoastlinePointId(), roundToSixPlaces(point.getDistanceInMiles()), point.getId() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(UPDATE_SQL, params);
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.DECIMAL, // lat
				Types.DECIMAL, // lon
				Types.BIGINT, // coastline_point_id
				Types.INTEGER, // distance in miles
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.DECIMAL, // lat
				Types.DECIMAL, // lon
				Types.BIGINT, // coastline_point_id
				Types.INTEGER, // distance in miles
				Types.BIGINT // ID
		};
	}
	
	protected Integer create(GridPoint point) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getLat(), 
				point.getLng()
		}), keyHolder);
		
		point.setId(keyHolder.getKey().intValue());
		return point.getId();
	}
	
	protected Integer update(GridPoint point) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getLat(), 
				point.getLng(), 
				point.getClosestCoastlinePointId(),
				roundToSixPlaces(point.getDistanceInMiles()),
				point.getId()
		}));
		
		return point.getId();
	}
	
	private Double roundToSixPlaces(Double value) {
		return new BigDecimal(value).setScale(6, RoundingMode.HALF_EVEN).doubleValue();
	}

}
