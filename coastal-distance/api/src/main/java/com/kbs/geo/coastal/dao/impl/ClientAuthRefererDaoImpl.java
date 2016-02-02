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

import com.kbs.geo.coastal.dao.ClientAuthRefererDao;
import com.kbs.geo.coastal.dao.mapper.ClientAuthRefererRowMapper;
import com.kbs.geo.coastal.model.billing.ClientAuthReferer;

@Component
public class ClientAuthRefererDaoImpl extends AbstractDao<ClientAuthReferer> implements ClientAuthRefererDao {
	private static final String INSERT_SQL = "INSERT INTO client_auth_referer (client_auth_id, name, referers, created) VALUES(?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_auth_referer SET client_auth_id=?, name=?, referers=? updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_auth_referer WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT car.* FROM client_auth_referer car JOIN client_auth ca car.client_auth_id=ca.id ON WHERE ca.client_id=?";
	private static final String SELECT_BY_CLIENT_AUTH_ID_SQL = "SELECT * FROM client_auth_referer WHERE client_auth_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public ClientAuthRefererDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientAuthReferer get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthReferer> clientAuth = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientAuthRefererRowMapper());
		if(null != clientAuth && !clientAuth.isEmpty()) return clientAuth.get(0);
		return null;
	}
	
	@Override
	public List<ClientAuthReferer> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthReferer> clientAuth = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientAuthRefererRowMapper());
		return (null == clientAuth ? new ArrayList<ClientAuthReferer>() : clientAuth);
	}
	
	@Override
	public List<ClientAuthReferer> getByClientAuthId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthReferer> clientAuth = jdbcTemplate.query(SELECT_BY_CLIENT_AUTH_ID_SQL, new Object[]{ clientId }, new ClientAuthRefererRowMapper());
		return (null == clientAuth ? new ArrayList<ClientAuthReferer>() : clientAuth);
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.BIGINT, // id
				Types.VARCHAR, // referer header value
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
				Types.VARCHAR, // referer header value
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
	
	protected Integer create(ClientAuthReferer clientAuth) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuth.getClientAuthId(),
				clientAuth.getName(),
				clientAuth.getReferers(),
				new Date(),
				clientAuth.getUpdated()
		}), keyHolder);
		
		clientAuth.setId(keyHolder.getKey().intValue());
		return clientAuth.getId();
	}
	
	protected Integer update(ClientAuthReferer clientAuth) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuth.getClientAuthId(),
				clientAuth.getName(),
				clientAuth.getReferers(),
				new Date(),
				clientAuth.getClientAuth().getId()
		}));
		
		return clientAuth.getId();
	}
	
}
