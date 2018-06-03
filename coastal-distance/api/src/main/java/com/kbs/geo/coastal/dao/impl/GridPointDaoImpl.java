package com.kbs.geo.coastal.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.Coast;
import com.kbs.geo.coastal.dao.GridPointClientCoastlinePointDao;
import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.dao.mapper.ClientGridPointRowMapper;
import com.kbs.geo.coastal.dao.mapper.GridPointRowMapper;
import com.kbs.geo.coastal.dao.mapper.MinMaxCoastlinePointSortOrderRowMapper;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.GridPointClientCoastlinePoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.model.MinMaxCoastlinePointSortOrder;

@Component
public class GridPointDaoImpl extends AbstractDao<GridPoint> implements GridPointDao {
	/*private static final Double BOUNDING_BOX_MILES_PER_SIDE = 8.0;  
	private static final Double MILES_PER_LATITUDE = 69.0;
	// Expand the search area so that it will encompass any box that encompasses our target point, and add a little slush
	private static final Double BOUNDING_BOX_OFFSET_LAT = (1 / (MILES_PER_LATITUDE / BOUNDING_BOX_MILES_PER_SIDE)) + 0.4;
	private static final Double MILES_PER_LONGITUDE = 53.0;
	private static final Double BOUNDING_BOX_OFFSET_LON = (1 / (MILES_PER_LONGITUDE / BOUNDING_BOX_MILES_PER_SIDE)) + 0.4;*/
	private static final Double BOUNDING_BOX_OFFSET = 0.135;
	
	private static final String INSERT_SQL = "INSERT INTO grid_point (lat, lon) VALUES(?, ?)";
	private static final String UPDATE_SQL = "UPDATE grid_point SET lat=?, lon=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM grid_point WHERE id=?";
	private static final String GET_BETWEEN_SQL = "SELECT gp.id, gp.lat, gp.lon, COALESCE(gpccp2.coastline_point_id, gpccp1.coastline_point_id) AS coastline_point_id, COALESCE(gpccp2.distance_in_miles, gpccp1.distance_in_miles) AS distance_in_miles FROM grid_point gp JOIN grid_point_client_coastline_point gpccp1 ON gpccp1.client_id=1 AND gp.id=gpccp1.grid_point_id LEFT JOIN grid_point_client_coastline_point gpccp2 ON gpccp1.client_id=? AND gp.id=gpccp1.grid_point_id WHERE sort_order BETWEEN ? AND ?";
	private static final String GET_OFFSET_SQL = "SELECT * FROM grid_point where closest_coastline_point_id is null ORDER BY id LIMIT ?, ?";
	private static final String GET_ALL_SQL = "SELECT * FROM grid_point";
//	private static final String GET_POINTS_SURROUNDING_SQL = "SELECT * FROM grid_point WHERE lat BETWEEN (? - " + BOUNDING_BOX_OFFSET_LAT + ") AND (? + " + BOUNDING_BOX_OFFSET_LAT + ") AND lon BETWEEN (? - " + BOUNDING_BOX_OFFSET_LON + ") AND (? + " + BOUNDING_BOX_OFFSET_LON + ")";
	private static final String GET_POINTS_SURROUNDING_SQL = "SELECT gp.id, gp.lat, gp.lon, gpccp.coastline_point_id, gpccp.distance_in_miles FROM grid_point gp JOIN grid_point_client_coastline_point gpccp ON gp.id=gpccp.grid_point_id JOIN coastline_point cp ON gpccp.coastline_point_id=cp.id JOIN kbs_client kc ON kc.id=? WHERE gpccp.client_id=COALESCE(kc.coastline_client_id, 1) AND cp.client_id=COALESCE(kc.coastline_client_id, 1) AND gp.lat BETWEEN (? - " + BOUNDING_BOX_OFFSET + ") AND (? + " + BOUNDING_BOX_OFFSET + ") AND gp.lon BETWEEN (? - " + BOUNDING_BOX_OFFSET + ") AND (? + " + BOUNDING_BOX_OFFSET + ")";
//	private static final String GET_COASTLINE_POINT_SORT_ORDERS_SURROUNDING_SQL = "SELECT MIN(cp.sort_order) AS min_sort_order, MAX(cp.sort_order) AS max_sort_order FROM grid_point gp JOIN grid_point_client_coastline_point gpccp ON gp.id=gpccp.grid_point_id JOIN coastline_point cp ON gpccp.coastline_point_id=cp.id AND gpccp.client_id=cp.client_id WHERE gpccp.client_id=? AND gp.lat BETWEEN (? - " + BOUNDING_BOX_OFFSET_LAT + ") AND (? + " + BOUNDING_BOX_OFFSET_LAT + ") AND gp.lon BETWEEN (? - " + BOUNDING_BOX_OFFSET_LON + ") AND (? + " + BOUNDING_BOX_OFFSET_LON + ")";
	private static final String GET_COASTLINE_POINT_SORT_ORDERS_SURROUNDING_SQL = "SELECT MIN(cp.sort_order) AS min_sort_order, MAX(cp.sort_order) AS max_sort_order FROM grid_point gp JOIN grid_point_client_coastline_point gpccp ON gp.id=gpccp.grid_point_id JOIN coastline_point cp ON gpccp.coastline_point_id=cp.id AND gpccp.client_id=cp.client_id JOIN kbs_client kc ON kc.id=? WHERE gpccp.client_id=COALESCE(kc.coastline_client_id, 1) AND gp.lat BETWEEN (? - " + BOUNDING_BOX_OFFSET + ") AND (? + " + BOUNDING_BOX_OFFSET + ") AND gp.lon BETWEEN (? - " + BOUNDING_BOX_OFFSET + ") AND (? + " + BOUNDING_BOX_OFFSET + ")";
	private static final String GET_POINTS_WEST_OF_LAT_SQL = "SELECT * FROM grid_point WHERE lat =< ?";
	private static final String GET_POINTS_EAST_OF_LAT_SQL = "SELECT * FROM grid_point WHERE lat >= ?";
	private static final String GET_POINTS_NORTH_OF_LON_SQL = "SELECT * FROM grid_point WHERE lon >= ?";
	private static final String GET_POINTS_SOUTH_OF_LON_SQL = "SELECT * FROM grid_point WHERE lon <= ?";
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private GridPointClientCoastlinePointDao gridPointClientCoastlinePointDao;
	
	private JdbcTemplate jdbcTemplate;
	
	public GridPointDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@PostConstruct
	public void init() {
		jdbcTemplate = new JdbcTemplate(datasource);
	}
	
	@Override
	public GridPoint get(Integer id) {
		GridPoint point = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new Object[]{ id }, new GridPointRowMapper());
		return point;
	}
	
	@Override
	public List<GridPoint> getPointsBetween(Integer clientId, Long startId, Long endId) {
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_BETWEEN_SQL, new Object[]{ clientId, startId, endId }, new ClientGridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getOffset(Long startRow, Long endRow) {
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_OFFSET_SQL, new Object[]{ startRow, endRow}, new GridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getAll() {
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_ALL_SQL, new GridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getPointsByCoast(Coast coast) {
		LatLng midpoint = new LatLng(new BigDecimal(39.8282), new BigDecimal(-98.5795));
		BigDecimal param = BigDecimal.ZERO;
		String sql = null;
		switch (coast) {
		case US_WEST:
			sql = GET_POINTS_WEST_OF_LAT_SQL;
			param = midpoint.getLat();
			break;
		case US_EAST:
			sql = GET_POINTS_EAST_OF_LAT_SQL;
			param = midpoint.getLat();
			break;
		case US_GULF:
			sql = GET_POINTS_SOUTH_OF_LON_SQL;
			param = midpoint.getLng();
			break;
		}
		
		List<GridPoint> gridPoints = jdbcTemplate.query(sql, new Number[]{ param }, new ClientGridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public List<GridPoint> getPointsSurrounding(Integer clientId, LatLng coordinate) {
		List<GridPoint> gridPoints = jdbcTemplate.query(GET_POINTS_SURROUNDING_SQL, new Number[]{ clientId, coordinate.getLat(), coordinate.getLat(), coordinate.getLng(), coordinate.getLng() }, new ClientGridPointRowMapper());
		return gridPoints;
	}
	
	@Override
	public MinMaxCoastlinePointSortOrder getBoundingCoastlinePointSortOrders(Integer clientId, LatLng coordinate) {
		MinMaxCoastlinePointSortOrder boundingSortOrders = jdbcTemplate.queryForObject(GET_COASTLINE_POINT_SORT_ORDERS_SURROUNDING_SQL, new Number[]{ clientId, coordinate.getLat(), coordinate.getLat(), coordinate.getLng(), coordinate.getLng() }, new MinMaxCoastlinePointSortOrderRowMapper());
		return boundingSortOrders;
	}
	
	@Override
	public void save(List<GridPoint> points) {
		List<GridPointClientCoastlinePoint> gridPointClientRelations = new ArrayList<GridPointClientCoastlinePoint>();
		List<Object[]> params = new ArrayList<Object[]>();
		for(GridPoint point: points) {
			params.add(new BigDecimal [] { point.getLat(), point.getLng() });
			
			gridPointClientRelations.add(new GridPointClientCoastlinePoint(point.getClientId(), point.getId(), point.getClosestCoastlinePointId(), point.getDistanceInMiles()));
		}
		
		jdbcTemplate.batchUpdate(INSERT_SQL, params);
		
		gridPointClientCoastlinePointDao.save(gridPointClientRelations);
	}
	
	@Override
	public void update(List<GridPoint> points) {
		List<GridPointClientCoastlinePoint> gridPointClientRelations = new ArrayList<GridPointClientCoastlinePoint>();
		List<Object[]> params = new ArrayList<Object[]>();
		int x;
		for(x = 0; x < points.size(); x++) {
			GridPoint point = points.get(x);
			System.out.println("Processing point " + point.getId());
			params.add(new Object [] { point.getLat(), point.getLng(), point.getId() });
			
			gridPointClientRelations.add(new GridPointClientCoastlinePoint(point.getClientId(), point.getId(), point.getClosestCoastlinePointId(), point.getDistanceInMiles()));
		}
		
		jdbcTemplate.batchUpdate(UPDATE_SQL, params);
		
		gridPointClientCoastlinePointDao.save(gridPointClientRelations);
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.DECIMAL, // lat
				Types.DECIMAL // lon
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.DECIMAL, // lat
				Types.DECIMAL, // lon
				Types.BIGINT // ID
		};
	}
	
	protected Integer create(GridPoint point) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getLat(), 
				point.getLng()
		}), keyHolder);
		
		point.setId(keyHolder.getKey().intValue());
		return point.getId();
	}
	
	protected Integer update(GridPoint point) {
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
		if(null == value) return null;
		return new BigDecimal(value).setScale(6, RoundingMode.HALF_EVEN).doubleValue();
	}

}
