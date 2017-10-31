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
import com.kbs.geo.coastal.dao.mapper.ClientRequestRowMapper;
import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.model.billing.RequestType;

@Component
public class ClientRequestDaoImpl extends AbstractDao<ClientRequest> implements ClientRequestDao {
	private static final String INSERT_SQL = "INSERT INTO client_request (client_auth_id, source_ip, request_url, request_body, request_type_id, response_status, response_body, request_time, response_time, error, error_message, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_request SET client_auth_id=?, source_ip=?, request_url=? request_body=?, request_type_id=?, response_status=?, response_body=?, request_time=?, response_time=?, error=?, error_message=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_request WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT cr.* FROM client_request cr JOIN client_auth ca ON cr.client_auth_id=ca.id WHERE ca.client_id=?";
	private static final String COUNT_BY_CLIENT_ID_SQL = "SELECT COUNT(cr.id) FROM client_request cr JOIN client_auth ca ON cr.client_auth_id=ca.id WHERE ca.client_id=? AND request_type_id=?";
	private static final String COUNT_BY_CLIENT_ID_DATE_RANGE_REQUEST_TYPE_SQL = "SELECT COUNT(cr.id) FROM client_request cr JOIN client_auth ca ON cr.client_auth_id=ca.id WHERE ca.client_id=? AND request_time BETWEEN ? AND ? AND request_type_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public ClientRequestDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientRequest get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientRequest> clientRequest = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientRequestRowMapper());
		if(null != clientRequest && !clientRequest.isEmpty()) return clientRequest.get(0);
		return null;
	}
	
	@Override
	public List<ClientRequest> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientRequest> clientRequest = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientRequestRowMapper());
		return (null == clientRequest ? new ArrayList<ClientRequest>() : clientRequest);
	}
	
	@Override
	public Long getCount(Integer clientId, RequestType requestType) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		Long count = jdbcTemplate.queryForObject(COUNT_BY_CLIENT_ID_SQL, Long.class, clientId, requestType.getId());
		return count;
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
				Types.TIMESTAMP, // request time
				Types.TIMESTAMP, // response time
				Types.BIT, // error
				Types.VARCHAR, // error message
				Types.TIMESTAMP, // created date
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
				Types.TIMESTAMP, // request time
				Types.TIMESTAMP, // response time
				Types.BIT, // error
				Types.VARCHAR, // error message
				Types.TIMESTAMP, // updated date
				Types.BIGINT // client ID
		};
	}
	protected Integer create(ClientRequest clientRequest) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientRequest.getClientAuthId(),
				clientRequest.getSourceIp(),
				clientRequest.getRequestUrl(),
				clientRequest.getRequestBody(),
				clientRequest.getRequestType().getId(),
				clientRequest.getResponseStatus(),
				clientRequest.getResponseBody(),
				clientRequest.getRequestTime(),
				clientRequest.getResponseTime(),
				clientRequest.getError(),
				clientRequest.getErrorMessage(),
				new Date()
		}), keyHolder);
		
		clientRequest.setId(keyHolder.getKey().intValue());
		return clientRequest.getId();
	}
	
	protected Integer update(ClientRequest clientRequest) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientRequest.getClientAuthId(),
				clientRequest.getSourceIp(),
				clientRequest.getRequestUrl(),
				clientRequest.getRequestBody(),
				clientRequest.getRequestType().getId(),
				clientRequest.getResponseStatus(),
				clientRequest.getResponseBody(),
				clientRequest.getRequestTime(),
				clientRequest.getResponseTime(),
				clientRequest.getError(),
				clientRequest.getErrorMessage(),
				new Date(),
				clientRequest.getId(),
		}));
		
		return clientRequest.getId();
	}
	
}
