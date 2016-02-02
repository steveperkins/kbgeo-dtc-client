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

import com.kbs.geo.coastal.dao.ClientAuthIpDao;
import com.kbs.geo.coastal.dao.mapper.ClientAuthIpRowMapper;
import com.kbs.geo.coastal.model.billing.ClientAuthIp;

@Component
public class ClientAuthIpDaoImpl extends AbstractDao<ClientAuthIp> implements ClientAuthIpDao {
	private static final String INSERT_SQL = "INSERT INTO client_auth_ip (client_auth_id, name, ip, created) VALUES(?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_auth_ip SET client_auth_id=?, name=?, ip=? updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_auth_ip WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT cap.* FROM client_auth_ip cap JOIN client_auth ca cap.client_auth_id=ca.id ON WHERE ca.client_id=?";
	private static final String SELECT_BY_CLIENT_AUTH_ID_SQL = "SELECT * FROM client_auth_ip WHERE client_auth_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public ClientAuthIpDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientAuthIp get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthIp> clientAuth = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientAuthIpRowMapper());
		if(null != clientAuth && !clientAuth.isEmpty()) return clientAuth.get(0);
		return null;
	}
	
	@Override
	public List<ClientAuthIp> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthIp> clientAuth = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientAuthIpRowMapper());
		return (null == clientAuth ? new ArrayList<ClientAuthIp>() : clientAuth);
	}
	
	@Override
	public List<ClientAuthIp> getByClientAuthId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientAuthIp> clientAuth = jdbcTemplate.query(SELECT_BY_CLIENT_AUTH_ID_SQL, new Object[]{ clientId }, new ClientAuthIpRowMapper());
		return (null == clientAuth ? new ArrayList<ClientAuthIp>() : clientAuth);
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
	
	protected Integer create(ClientAuthIp clientAuthIp) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuthIp.getClientAuthId(),
				clientAuthIp.getName(),
				clientAuthIp.getIp(),
				new Date(),
				clientAuthIp.getUpdated()
		}), keyHolder);
		
		clientAuthIp.setId(keyHolder.getKey().intValue());
		return clientAuthIp.getId();
	}
	
	protected Integer update(ClientAuthIp clientAuthIp) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientAuthIp.getClientAuthId(),
				clientAuthIp.getName(),
				clientAuthIp.getIp(),
				new Date(),
				clientAuthIp.getClientAuth().getId()
		}));
		
		return clientAuthIp.getId();
	}
	
}
