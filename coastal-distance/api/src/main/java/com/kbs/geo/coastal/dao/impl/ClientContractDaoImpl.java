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

import com.kbs.geo.coastal.dao.ClientContractDao;
import com.kbs.geo.coastal.dao.mapper.ClientContractRowMapper;
import com.kbs.geo.coastal.model.billing.ClientContract;

@Component
public class ClientContractDaoImpl extends AbstractDao<ClientContract> implements ClientContractDao {
	private static final String INSERT_SQL = "INSERT INTO client_contract (client_id, name, request_type_id, max_requests, cents_per_request, starts, expires, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_contract SET client_id=?, name=?, request_type_id=? max_requests=?, cents_per_request=?, starts=?, expires=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_contract WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT * FROM client_contract WHERE client_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public ClientContractDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientContract get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientContract> clientContract = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientContractRowMapper());
		if(null != clientContract && !clientContract.isEmpty()) return clientContract.get(0);
		return null;
	}
	
	@Override
	public List<ClientContract> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientContract> clientContracts = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientContractRowMapper());
		return (null == clientContracts ? new ArrayList<ClientContract>() : clientContracts);
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.BIGINT, // client ID
				Types.VARCHAR, // client name
				Types.INTEGER, // request type ID
				Types.BIGINT, // max requests
				Types.INTEGER, // cents per request
				Types.DATE, // start date
				Types.DATE, // expiration date
				Types.DATE, // created date
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.BIGINT, // client id
				Types.VARCHAR, // client name
				Types.INTEGER, // request type ID
				Types.BIGINT, // max requests
				Types.INTEGER, // cents per request
				Types.DATE, // start date
				Types.DATE, // expiration date
				Types.DATE, // updated date
				Types.BIGINT // id
		};
	}
	
	protected Integer create(ClientContract clientContract) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientContract.getClientId(),
				clientContract.getName(),
				clientContract.getRequestType().getId(),
				clientContract.getMaxRequests(),
				clientContract.getCentsPerRequest(),
				clientContract.getStarts(),
				clientContract.getExpires(),
				new Date()
		}), keyHolder);
		
		clientContract.setId(keyHolder.getKey().intValue());
		return clientContract.getId();
	}
	
	protected Integer update(ClientContract clientContract) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientContract.getClientId(),
				clientContract.getName(),
				clientContract.getRequestType().getId(),
				clientContract.getMaxRequests(),
				clientContract.getCentsPerRequest(),
				clientContract.getStarts(),
				clientContract.getExpires(),
				new Date(),
				clientContract.getId()
		}));
		
		return clientContract.getId();
	}
	
}
