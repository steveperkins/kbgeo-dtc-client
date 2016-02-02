package com.kbs.geo.coastal.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.DistanceGridDao;
import com.kbs.geo.coastal.dao.mapper.DistanceGridPointRowMapper;
import com.kbs.geo.coastal.model.DistanceGridPoint;
import com.kbs.geo.coastal.model.LatLng;

@Component
public class DistanceGridDaoImpl extends AbstractDao<DistanceGridPoint> implements DistanceGridDao {
	private static final String DEGREE_OFFSET = "0.02";
	private static final String INSERT_SQL = "INSERT INTO distance_grid (lat, lon, distance_miles) VALUES(?, ?, ?)";
	private static final String GET_DISTANCE_SQL = "SELECT id, lat, lon, distance_miles FROM distance_grid WHERE ? BETWEEN (lat - " + DEGREE_OFFSET + ") AND (lat + " + DEGREE_OFFSET + ") AND ? BETWEEN (lon - " + DEGREE_OFFSET + ") AND (lon + " + DEGREE_OFFSET + ")";
	
	@Autowired
	private DataSource datasource;
	
	public DistanceGridDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
	}
	
	@Override
	public void save(List<DistanceGridPoint> gridPoints) {
		List<Object[]> params = new ArrayList<Object[]>();
		for(DistanceGridPoint gridPoint: gridPoints) {
			params.add(new Double [] { gridPoint.getLat(), gridPoint.getLon(), gridPoint.getDistanceMiles() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(INSERT_SQL, params);
	}
	
	@Override
	public DistanceGridPoint getNearestPoint(LatLng latLng) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		DistanceGridPoint gridPoint = jdbcTemplate.queryForObject(GET_DISTANCE_SQL, new Object[]{ latLng.getLat(), latLng.getLng() }, new DistanceGridPointRowMapper());
		return gridPoint;
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.BIGINT, // id
				Types.VARCHAR, // source IP
				Types.VARCHAR, // request URL
				Types.NVARCHAR, // request body
				Types.INTEGER, // request type
				Types.INTEGER, // response status
				Types.NVARCHAR, // response body
				Types.DATE, // request time
				Types.DATE, // response time
				Types.DATE, // created date
				Types.DATE // updated date
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.BIGINT, // id
				Types.VARCHAR, // source IP
				Types.VARCHAR, // request URL
				Types.NVARCHAR, // request body
				Types.INTEGER, // request type
				Types.INTEGER, // response status
				Types.NVARCHAR, // response body
				Types.DATE, // request time
				Types.DATE, // response time
				Types.DATE, // updated date
				Types.BIGINT // client ID
		};
	}
	
	protected Integer create(DistanceGridPoint gridPoint) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				gridPoint.getLat(), 
				gridPoint.getLon(), 
				gridPoint.getDistanceMiles()
		}), keyHolder);
		
		gridPoint.setId(keyHolder.getKey().intValue());
		return gridPoint.getId();
	}
	
	protected Integer update(DistanceGridPoint gridPoint) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				gridPoint.getLat(), 
				gridPoint.getLon(), 
				gridPoint.getDistanceMiles(),
				gridPoint.getId()
		}));
		
		return gridPoint.getId();
	}

}
