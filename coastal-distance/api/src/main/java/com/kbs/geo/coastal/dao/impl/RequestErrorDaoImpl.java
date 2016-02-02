package com.kbs.geo.coastal.dao.impl;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.dao.RequestErrorDao;
import com.kbs.geo.coastal.dao.mapper.RequestErrorRowMapper;
import com.kbs.geo.coastal.model.billing.RequestError;
import com.kbs.geo.coastal.model.billing.RequestType;

@Component
public class RequestErrorDaoImpl extends AbstractDao<RequestError> implements RequestErrorDao {
	private static final String INSERT_SQL = "INSERT INTO request_error (client_id, source_ip, request_url, request_body, request_type_id, response_status, response_body, request_time, response_time, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE request_error SET client_id=?, source_ip=?, request_url=? request_body=?, request_type_id=?, response_status=?, response_body=?, request_time=?, response_time=?, WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM request_error WHERE id=?";
	private static final String COUNT_BY_CLIENT_ID_DATE_RANGE_REQUEST_TYPE_SQL = "SELECT COUNT(id) FROM request_error WHERE client_id=? AND request_time BETWEEN ? AND ? AND request_type_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public RequestErrorDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public RequestError get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<RequestError> clientRequest = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new RequestErrorRowMapper());
		if(null != clientRequest && !clientRequest.isEmpty()) return clientRequest.get(0);
		return null;
	}
	
	@Override
	public Long getCount(Integer clientId, Date beginDate, Date endDate, RequestType requestType) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		Long count = jdbcTemplate.queryForObject(COUNT_BY_CLIENT_ID_DATE_RANGE_REQUEST_TYPE_SQL, Long.class, clientId, beginDate, endDate, requestType.getId());
		return count;
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
				Types.DATE // created date
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
				Types.BIGINT // client ID
		};
	}
	
	protected Integer create(RequestError error) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] {
				error.getClientId(),
				error.getSourceIp(),
				error.getRequestUrl(),
				error.getRequestBody(),
				error.getRequestType().getId(),
				error.getResponseStatus(),
				error.getResponseBody(),
				error.getRequestTime(),
				error.getResponseTime(),
				new Date()
		}), keyHolder);
		
		error.setId(keyHolder.getKey().intValue());
		return error.getId();
	}
	
	protected Integer update(RequestError error) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] {
				error.getClientId(),
				error.getSourceIp(),
				error.getRequestUrl(),
				error.getRequestBody(),
				error.getRequestType().getId(),
				error.getResponseStatus(),
				error.getResponseBody(),
				error.getRequestTime(),
				error.getResponseTime(),
				error.getId(),
		}));
		
		return error.getId();
	}

}
