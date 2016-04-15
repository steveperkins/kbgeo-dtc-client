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

import com.kbs.geo.coastal.dao.ClientAuthDao;
import com.kbs.geo.coastal.dao.mapper.ClientAuthRowMapper;
import com.kbs.geo.coastal.model.billing.ClientAuth;

@Component
public class ClientAuthDaoImpl extends AbstractDao<ClientAuth> implements ClientAuthDao {
	private static final String INSERT_SQL = "INSERT INTO client_auth (client_id, name, token, expires, created, updated) VALUES(?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_contact SET client_id=?, name=?, token=? expires=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_auth WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT * FROM client_auth WHERE client_id=?";
	private static final String SELECT_BY_TOKEN_SQL = "SELECT * FROM client_auth WHERE token=?";
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private ClientAuthRowMapper rowMapper;
	
	public ClientAuthDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientAuth get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuth> clientAuth = jdbcTemplate.query(SELECT_BY_ID_SQL, new Integer[]{ id }, rowMapper);
		if(null != clientAuth && !clientAuth.isEmpty()) return clientAuth.get(0);
		return null;
	}
	
	@Override
	public List<ClientAuth> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuth> clientAuth = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Integer[]{ clientId }, rowMapper);
		return (null == clientAuth ? new ArrayList<ClientAuth>() : clientAuth);
	}
	
	@Override
	public ClientAuth getByToken(String token) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuth> clientAuth = jdbcTemplate.query(SELECT_BY_TOKEN_SQL, new String[]{ token }, rowMapper);
		if(null != clientAuth && !clientAuth.isEmpty()) {
			return clientAuth.get(0);
		}
		return null;
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
	
	protected Integer create(ClientAuth clientAuth) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuth.getClientId(),
				clientAuth.getName(),
				clientAuth.getToken(),
				clientAuth.getExpires(),
				new Date(),
				clientAuth.getUpdated()
		}), keyHolder);
		
		clientAuth.setId(keyHolder.getKey().intValue());
		return clientAuth.getId();
	}
	
	protected Integer update(ClientAuth clientAuth) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuth.getClientId(),
				clientAuth.getName(),
				clientAuth.getToken(),
				clientAuth.getExpires(),
				new Date(),
				clientAuth.getClient().getId()
		}));
		
		return clientAuth.getId();
	}

}
