package com.kbs.geo.coastal.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.CoastlineSegmentDao;
import com.kbs.geo.coastal.dao.mapper.CoastlineSegmentRowMapper;
import com.kbs.geo.coastal.model.CoastlineSegment;

@Component
public class CoastlineSegmentDaoImpl extends AbstractDao<CoastlineSegment> implements CoastlineSegmentDao {
	private static final String INSERT_SQL = "INSERT INTO coastline_segment (coast, description, sort_order) VALUES(?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE coastline_segment SET coast=?, description=?, sort_order=?) WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM coastline_segment WHERE id=?";
	private static final String GET_ALL_SQL = "SELECT * FROM coastline_segment";
	private static final String GET_ALL_BY_CLIENT_SQL = "SELECT cs.* FROM coastline_segment cs JOIN client_coastline_segment ccs ON cs.id=ccs.segment_id WHERE ccs.client_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public CoastlineSegmentDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public CoastlineSegment get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		CoastlineSegment point = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new Object[]{ id }, new CoastlineSegmentRowMapper());
		return point;
	}
	
	@Override
	public List<CoastlineSegment> getAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlineSegment> points = jdbcTemplate.query(GET_ALL_SQL, new CoastlineSegmentRowMapper());
		return (null == points ? new ArrayList<CoastlineSegment>() : points);
	}
	
	@Override
	public List<CoastlineSegment> getAll(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<CoastlineSegment> points = jdbcTemplate.query(GET_ALL_BY_CLIENT_SQL, new Integer[]{ clientId }, new CoastlineSegmentRowMapper());
		return (null == points ? new ArrayList<CoastlineSegment>() : points);
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.VARCHAR, // coast
				Types.VARCHAR, // description
				Types.BIGINT, // sort order
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.VARCHAR, // coast
				Types.VARCHAR, // description
				Types.DECIMAL, // sort order
				Types.INTEGER // ID
		};
	}
	
	protected Integer create(CoastlineSegment segment) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				segment.getCoast(),
				segment.getDescription(),
				segment.getSortOrder()
		}), keyHolder);
		
		segment.setId(keyHolder.getKey().intValue());
		return segment.getId();
	}
	
	protected Integer update(CoastlineSegment segment) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				segment.getCoast(),
				segment.getDescription(),
				segment.getSortOrder(),
				segment.getId()
		}));
		
		return segment.getId();
	}

}
