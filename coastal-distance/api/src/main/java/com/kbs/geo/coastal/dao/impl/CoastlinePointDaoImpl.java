package com.kbs.geo.coastal.dao.impl;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.Coast;
import com.kbs.geo.coastal.dao.CoastlinePointDao;
import com.kbs.geo.coastal.dao.mapper.CoastlinePointRowMapper;
import com.kbs.geo.coastal.model.CoastlinePoint;

@Component
public class CoastlinePointDaoImpl extends AbstractDao<CoastlinePoint> implements CoastlinePointDao {
	private static final String INSERT_SQL = "INSERT INTO coastline_point (lat, lon) VALUES(?, ?)";
	private static final String UPDATE_SQL = "UPDATE coastline_point SET lat=?, lon=?, sort_order=?) WHERE id=?";
	private static final String SET_SORT_ORDER_SQL = "UPDATE coastline_point SET sort_order=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM coastline_point WHERE id=?";
	private static final String GET_BETWEEN_SQL = "SELECT * FROM coastline_point WHERE client_id=? AND sort_order BETWEEN ? AND ?";
	private static final String GET_BETWEEN_SORT_ORDERS_SQL = "WITH segment_override AS (" 
			  + "SELECT DISTINCT segment_id FROM client_coastline_segment WHERE client_id=? AND active = TRUE"
			  + ") "
			  + "SELECT cp.* "
			  + "FROM coastline_point cp " 
			  + "LEFT JOIN segment_override so ON cp.segment_id=so.segment_id " 
			  + "WHERE cp.sort_order BETWEEN ? AND ? AND ((so.segment_id IS NULL AND cp.client_id=1) OR cp.client_id=?)";
	private static final String GET_ALL_SQL = "SELECT * FROM coastline_point WHERE client_id=?";
	private static final String GET_BY_COAST_SQL = "SELECT * FROM coastline_point WHERE client_id=? AND coast=?";
	private static final String GET_ALL_POINTS_BY_CLIENT_SEGMENT_SQL = "SELECT cp.* FROM coastline_point cp WHERE cp.client_id=? AND cp.segment_id=?";
	private static final String GET_POINTS_BEFORE_AND_AFTER_POINT = "(SELECT cp.* FROM coastline_point cp WHERE cp.client_id=? AND cp.sort_order < ? ORDER BY cp.sort_order DESC LIMIT 1) UNION (SELECT cp.* FROM coastline_point cp WHERE cp.client_id=? AND cp.sort_order > ? ORDER BY cp.sort_order LIMIT 1)";
	
	@Autowired
	private DataSource datasource;
	
	private PreparedStatementCreatorFactory setSortOrderPreparedStatementCreatorFactory;
	
	public CoastlinePointDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public CoastlinePoint get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		CoastlinePoint point = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new Object[]{ id }, new CoastlinePointRowMapper());
		return point;
	}
	
	@Override
	public List<CoastlinePoint> getAll(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlinePoint> points = jdbcTemplate.query(GET_ALL_SQL, new Number[] { clientId }, new CoastlinePointRowMapper());
		return (null == points ? new ArrayList<CoastlinePoint>() : points);
	}
	
	@Override
	public List<CoastlinePoint> getByCoast(Integer clientId, Coast coast) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlinePoint> points = jdbcTemplate.query(GET_BY_COAST_SQL, new Object[] { clientId, coast.getValue() }, new CoastlinePointRowMapper());
		return (null == points ? new ArrayList<CoastlinePoint>() : points);
	}
	
	@Override
	public List<CoastlinePoint> getPointsBetween(Integer clientId, Double startOrder, Double endOrder) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlinePoint> points = jdbcTemplate.query(GET_BETWEEN_SQL, new Number[]{ clientId, startOrder, endOrder }, new CoastlinePointRowMapper());
		return (null == points ? new ArrayList<CoastlinePoint>() : points);
	}
	
	@Override
	public List<CoastlinePoint> getPointsBetweenSortOrders(Integer clientId, Double startOrder, Double endOrder) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlinePoint> points = jdbcTemplate.query(GET_BETWEEN_SORT_ORDERS_SQL, new Number[]{ clientId, startOrder, endOrder, clientId }, new CoastlinePointRowMapper());
		return (null == points ? new ArrayList<CoastlinePoint>() : points);
	}
	
	@Override
	public List<CoastlinePoint> getPointsBeforeAndAfter(Integer clientId, CoastlinePoint point) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlinePoint> points = jdbcTemplate.query(GET_POINTS_BEFORE_AND_AFTER_POINT, new Number[]{ clientId, point.getSortOrder(), clientId, point.getSortOrder() }, new CoastlinePointRowMapper());
		return (null == points ? new ArrayList<CoastlinePoint>() : points);
	}
	
	@Override
	public List<CoastlinePoint> getByClientSegment(Integer clientId, Integer segmentId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlinePoint> points = jdbcTemplate.query(GET_ALL_POINTS_BY_CLIENT_SEGMENT_SQL, new Integer[]{ clientId, segmentId }, new CoastlinePointRowMapper());
		return (null == points ? new ArrayList<CoastlinePoint>() : points);
	}
	
	@Override
	public void save(List<CoastlinePoint> points) {
		List<Object[]> params = new ArrayList<Object[]>();
		for(CoastlinePoint point: points) {
			params.add(new BigDecimal [] { point.getLat(), point.getLng() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(INSERT_SQL, params);
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
	
	protected Integer create(CoastlinePoint point) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getLat(), 
				point.getLng()
		}), keyHolder);
		
		point.setId(keyHolder.getKey().intValue());
		
		Double sortOrder = (null == point.getSortOrder() ? point.getId() : point.getSortOrder());
				
		// Set sort order
		jdbcTemplate.update(getSetSortOrderPreparedStatementCreatorFactory().newPreparedStatementCreator(new Object[] { sortOrder, point.getId() }));
		return point.getId();
	}
	
	protected Integer update(CoastlinePoint point) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				point.getLat(), 
				point.getLng(), 
				point.getSortOrder(),
				point.getId()
		}));
		
		return point.getId();
	}

	private PreparedStatementCreatorFactory getSetSortOrderPreparedStatementCreatorFactory() {
		if(null == setSortOrderPreparedStatementCreatorFactory) {
			setSortOrderPreparedStatementCreatorFactory = new PreparedStatementCreatorFactory(SET_SORT_ORDER_SQL);
		}
		return setSortOrderPreparedStatementCreatorFactory;
	}

}
