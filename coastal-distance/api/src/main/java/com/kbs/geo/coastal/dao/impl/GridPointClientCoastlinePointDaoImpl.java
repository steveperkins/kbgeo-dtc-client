package com.kbs.geo.coastal.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.BatchUpdateException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.GridPointClientCoastlinePointDao;
import com.kbs.geo.coastal.dao.mapper.GridPointClientCoastlinePointRowMapper;
import com.kbs.geo.coastal.model.GridPointClientCoastlinePoint;

@Component
public class GridPointClientCoastlinePointDaoImpl implements GridPointClientCoastlinePointDao {
	private static final String INSERT_SQL = "INSERT INTO grid_point_client_coastline_point (client_id, grid_point_id, coastline_point_id, distance_in_miles) VALUES(?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE grid_point_client_coastline_point SET coastline_point_id=?, distance_in_miles=? WHERE (client_id=? AND grid_point_id=?)";
	private static final String SELECT_BY_IDS_SQL = "SELECT * FROM grid_point_client_coastline_point WHERE grid_point_id=? AND client_id=?";
	private static final String GET_ALL_SQL = "SELECT * FROM grid_point_client_coastline_point";
	
	
	@Autowired
	private DataSource datasource;
	
	public GridPointClientCoastlinePointDaoImpl() {}
	
	@Override
	public List<GridPointClientCoastlinePoint> getAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<GridPointClientCoastlinePoint> gridPoints = jdbcTemplate.query(GET_ALL_SQL, new GridPointClientCoastlinePointRowMapper());
		return gridPoints;
	}
	
	@Override
	public void save(List<GridPointClientCoastlinePoint> points) {
		List<GridPointClientCoastlinePoint> updates = new ArrayList<GridPointClientCoastlinePoint>();
		List<Object[]> params = new ArrayList<Object[]>();
		for(GridPointClientCoastlinePoint point: points) {
			if(null != get(point.getClientId(), point.getGridPointId())) {
				updates.add(point);
			} else {
				params.add(new Number [] { 
						point.getClientId(), 
						point.getGridPointId(),
						point.getCoastlinePointId(),
						roundToSixPlaces(point.getDistanceInMiles())
				});
			}
		}
		
		update(updates);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		try {
			jdbcTemplate.batchUpdate(INSERT_SQL, params);
		} catch(Exception e) {
			e.printStackTrace();
			if(e instanceof BatchUpdateException) {
				((BatchUpdateException)e).getNextException().printStackTrace();
			}
			// fuck off
		}
		
	}
	
	@Override
	public void update(List<GridPointClientCoastlinePoint> points) {
		List<Object[]> params = new ArrayList<Object[]>();
		int x;
		for(x = 0; x < points.size(); x++) {
			GridPointClientCoastlinePoint point = points.get(x);
			params.add(new Object [] { 
					point.getCoastlinePointId(), 
					roundToSixPlaces(point.getDistanceInMiles()), 
					point.getClientId(), 
					point.getGridPointId(), 
					point.getCoastlinePointId()
				});
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(UPDATE_SQL, params);
	}
	
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.INTEGER, // client_id
				Types.INTEGER, // grid_point_id
				Types.INTEGER, // coastline_point_id
				Types.DECIMAL, // distance in miles
		};
	}
	
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.INTEGER, // client_id
				Types.INTEGER, // grid_point_id
				Types.INTEGER, // coastline_point_id
				Types.DECIMAL, // distance in miles
				Types.INTEGER, // client_id
				Types.INTEGER // grid_point_id
		};
	}
	
	protected void create(GridPointClientCoastlinePoint point) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getClientId(), 
				point.getGridPointId(),
				point.getCoastlinePointId(),
				roundToSixPlaces(point.getDistanceInMiles())
		}));
	}
	
	protected void update(GridPointClientCoastlinePoint point) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getClientId(), 
				point.getGridPointId(),
				point.getCoastlinePointId(),
				roundToSixPlaces(point.getDistanceInMiles()),
				point.getClientId(), 
				point.getGridPointId(),
				point.getCoastlinePointId(),
		}));
		
	}
	
	@Override
	public GridPointClientCoastlinePoint get(Integer clientId, Integer gridPointId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		GridPointClientCoastlinePoint point = jdbcTemplate.queryForObject(SELECT_BY_IDS_SQL, new Object[]{ clientId, gridPointId }, new GridPointClientCoastlinePointRowMapper());
		return point;
	}

	private Double roundToSixPlaces(Double value) {
		if(null == value) return null;
		return new BigDecimal(value).setScale(6, RoundingMode.HALF_EVEN).doubleValue();
	}
	
	
	
	
	private PreparedStatementCreatorFactory insertStatementFactory;
	private PreparedStatementCreatorFactory updateStatementFactory;
	
	public void save(GridPointClientCoastlinePoint obj) {
		GridPointClientCoastlinePoint existingPoint = get(obj.getClientId(), obj.getGridPointId());
		if(null == existingPoint) {
			create(obj);
		} else {
			update(obj);
		}
	}
	
	protected PreparedStatementCreatorFactory getInsertStatementFactory() {
		if(null == insertStatementFactory) {
			insertStatementFactory = new PreparedStatementCreatorFactory(INSERT_SQL, getInsertParamTypes());
		}
		return insertStatementFactory;
	}
	
	protected PreparedStatementCreatorFactory getUpdateStatementFactory() {
		if(null == updateStatementFactory) {
			updateStatementFactory = new PreparedStatementCreatorFactory(UPDATE_SQL, getUpdateParamTypes());
		}
		return updateStatementFactory;
	}

}
