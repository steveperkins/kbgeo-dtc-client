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

import com.kbs.geo.coastal.dao.ClientAuthWebDao;
import com.kbs.geo.coastal.dao.mapper.ClientAuthWebRowMapper;
import com.kbs.geo.coastal.model.billing.ClientAuthWeb;

@Component
public class ClientAuthWebDaoImpl extends AbstractDao<ClientAuthWeb> implements ClientAuthWebDao {
	private static final String INSERT_SQL = "INSERT INTO client_auth_web (client_auth_id, client_contact_id, username, password, starts, expires, created) VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_auth_web SET client_auth_id=?, client_contact_id=?, username=?, password=?, starts=?, expires=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_auth_web WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT caw.* FROM client_auth_web caw JOIN client_auth ca caw.client_auth_id=ca.id ON WHERE ca.client_id=?";
	private static final String SELECT_BY_CLIENT_AUTH_ID_SQL = "SELECT * FROM client_auth_web WHERE client_auth_id=?";
	private static final String SELECT_BY_CLIENT_CONTACT_ID_SQL = "SELECT * FROM client_auth_web WHERE client_contact_id=?";
	private static final String SELECT_BY_USERNAME_SQL = "SELECT * FROM client_auth_web WHERE username=?";
	private static final String SELECT_BY_USERNAME_PASSWORD_SQL = "SELECT * FROM client_auth_web WHERE username=? AND password=?";
	
	@Autowired
	private DataSource datasource;
	
	public ClientAuthWebDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientAuthWeb get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthWeb> clientAuthWeb = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientAuthWebRowMapper());
		if(null != clientAuthWeb && !clientAuthWeb.isEmpty()) return clientAuthWeb.get(0);
		return null;
	}
	
	@Override
	public List<ClientAuthWeb> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthWeb> clientAuthWeb = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientAuthWebRowMapper());
		return (null == clientAuthWeb ? new ArrayList<ClientAuthWeb>() : clientAuthWeb);
	}
	
	@Override
	public List<ClientAuthWeb> getByClientAuthId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthWeb> clientAuthWeb = jdbcTemplate.query(SELECT_BY_CLIENT_AUTH_ID_SQL, new Object[]{ clientId }, new ClientAuthWebRowMapper());
		return (null == clientAuthWeb ? new ArrayList<ClientAuthWeb>() : clientAuthWeb);
	}
	
	@Override
	public ClientAuthWeb getByClientContactId(Integer clientContactId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthWeb> clientAuthWeb = jdbcTemplate.query(SELECT_BY_CLIENT_CONTACT_ID_SQL, new Object[]{ clientContactId }, new ClientAuthWebRowMapper());
		if(null != clientAuthWeb && !clientAuthWeb.isEmpty()) return clientAuthWeb.get(0);
		return null;
	}
	
	@Override
	public ClientAuthWeb getByUsername(String username) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthWeb> clientAuthWeb = jdbcTemplate.query(SELECT_BY_USERNAME_SQL, new Object[]{ username }, new ClientAuthWebRowMapper());
		if(null != clientAuthWeb && !clientAuthWeb.isEmpty()) return clientAuthWeb.get(0);
		return null;
	}
	
	@Override
	public ClientAuthWeb getByUsernamePassword(String username, String encodedPassword) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthWeb> clientAuthWeb = jdbcTemplate.query(SELECT_BY_USERNAME_PASSWORD_SQL, new Object[]{ username, encodedPassword }, new ClientAuthWebRowMapper());
		if(null != clientAuthWeb && !clientAuthWeb.isEmpty()) return clientAuthWeb.get(0);
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
				Types.DATE, // updated date
				Types.BIGINT // client ID
		};
	}
	
	protected Integer create(ClientAuthWeb clientAuthWeb) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuthWeb.getClientAuthId(),
				clientAuthWeb.getClientContactId(),
				clientAuthWeb.getUsername(),
				clientAuthWeb.getPassword(),
				clientAuthWeb.getStarts(),
				clientAuthWeb.getExpires(),
				new Date()
		}), keyHolder);
		
		clientAuthWeb.setId(keyHolder.getKey().intValue());
		return clientAuthWeb.getId();
	}
	
	protected Integer update(ClientAuthWeb clientAuthWeb) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuthWeb.getClientAuthId(),
				clientAuthWeb.getClientContactId(),
				clientAuthWeb.getUsername(),
				clientAuthWeb.getPassword(),
				clientAuthWeb.getStarts(),
				clientAuthWeb.getExpires(),
				new Date(),
				clientAuthWeb.getId()
		}));
		
		return clientAuthWeb.getId();
	}

}
