package com.kbs.geo.coastal.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.ClientRequestDao;
import com.kbs.geo.coastal.dao.ClientStatisticsDao;
import com.kbs.geo.coastal.dao.mapper.ClientStatisticsRowMapper;
import com.kbs.geo.coastal.model.billing.ClientStatistics;

@Component
public class ClientStatisticsDaoImpl extends AbstractDao<ClientStatistics> implements ClientStatisticsDao {
	private static final String INSERT_SQL = "INSERT INTO client_statistics (client_id, request_type_id, `year`, `month`, request_count, error_count, created) VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_statistics SET client_id=?, request_type_id=?, `year`=?, `month`=?, request_count=?, error_count=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_statistics WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT * FROM client_statistics WHERE client_id=?";
	private static final String SELECT_BY_CLIENT_ID_YEAR_MONTH_SQL = "SELECT * FROM client_statistics WHERE client_id=? AND `year`=? AND `month`=? ORDER BY request_type_id";
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private ClientRequestDao clientRequestDao;
	
	public ClientStatisticsDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientStatistics get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientStatistics> clientStatistics = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientStatisticsRowMapper());
		if(null != clientStatistics && !clientStatistics.isEmpty()) return clientStatistics.get(0);
		return null;
	}
	
	@Override
	public List<ClientStatistics> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientStatistics> clientStatistics = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientStatisticsRowMapper());
		return (null == clientStatistics ? new ArrayList<ClientStatistics>() : clientStatistics);
	}
	
	@Override
	public List<ClientStatistics> getByClientIdYearMonth(Integer clientId, Integer year, Integer month) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientStatistics> clientStatistics = jdbcTemplate.query(SELECT_BY_CLIENT_ID_YEAR_MONTH_SQL, new Object[]{ clientId, year, month }, new ClientStatisticsRowMapper());
		return (null == clientStatistics ? new ArrayList<ClientStatistics>() : clientStatistics);
	}
	
	@Override
	public void save(List<ClientStatistics> list) {
		for(ClientStatistics statistics: list) {
			save(statistics);
		}
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.BIGINT, // client ID
				Types.INTEGER, // request type
				Types.INTEGER, // year
				Types.INTEGER, // month
				Types.BIGINT, // request count
				Types.BIGINT, // error count
				Types.DATE // created date
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.BIGINT, // client ID
				Types.INTEGER, // request type
				Types.INTEGER, // year
				Types.INTEGER, // month
				Types.BIGINT, // request count
				Types.BIGINT, // error count
				Types.DATE, // created date
				Types.BIGINT // id
		};
	}
	
	protected Integer create(ClientStatistics clientStatistics) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientStatistics.getClientId(),
				clientStatistics.getRequestType().getId(),
				clientStatistics.getYear(),
				clientStatistics.getMonth(),
				clientStatistics.getRequestCount(),
				clientStatistics.getErrorCount(),
				new Date()
		}), keyHolder);
		
		clientStatistics.setId(keyHolder.getKey().intValue());
		return clientStatistics.getId();
	}
	
	protected Integer update(ClientStatistics clientStatistics) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientStatistics.getClientId(),
				clientStatistics.getRequestType().getId(),
				clientStatistics.getYear(),
				clientStatistics.getMonth(),
				clientStatistics.getRequestCount(),
				clientStatistics.getErrorCount(),
				new Date(),
				clientStatistics.getId()
		}));
		
		return clientStatistics.getId();
	}
	
}
