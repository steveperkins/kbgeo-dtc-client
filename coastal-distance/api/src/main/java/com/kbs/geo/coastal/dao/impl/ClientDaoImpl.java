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

import com.kbs.geo.coastal.dao.ClientDao;
import com.kbs.geo.coastal.dao.mapper.ClientRowMapper;
import com.kbs.geo.coastal.model.billing.Client;

@Component
public class ClientDaoImpl extends AbstractDao<Client> implements ClientDao {
	private static final String INSERT_SQL = "INSERT INTO kbs_client (name, address, city, state, zip, phone, created) VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE kbs_client SET name=?, address=?, city=?, state=?, zip=?, phone=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM kbs_client WHERE id=?";
	private static final String SELECT_BY_ACTIVE_SQL = "SELECT kc.* FROM kbs_client kc INNER JOIN client_contract cc ON kc.id=cc.client_id WHERE CURRENT_TIMESTAMP() BETWEEN starts AND expires";
	private static final String SELECT_BY_INACTIVE_SQL = "SELECT kc.* FROM kbs_client kc INNER JOIN client_contract cc ON kc.id=cc.client_id WHERE expires < CURRENT_TIMESTAMP()";
	
	@Autowired
	private DataSource datasource;
	
	public ClientDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public Client get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<Client> client = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientRowMapper());
		if(null != client && !client.isEmpty()) return client.get(0);
		return null;
	}
	
	@Override
	public List<Client> getAllActive(Boolean active) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<Client> clients;
		if(null != active && active) {
			clients = jdbcTemplate.query(SELECT_BY_ACTIVE_SQL, new ClientRowMapper());
		} else {
			clients = jdbcTemplate.query(SELECT_BY_INACTIVE_SQL, new ClientRowMapper());
		}
		return (null == clients ? new ArrayList<Client>() : clients);
	}
	
	@Override
	protected int[] getInsertParamTypes() {
		return new int[] { 
				Types.VARCHAR, // name
				Types.VARCHAR, // address
				Types.VARCHAR, // city
				Types.VARCHAR, // state
				Types.VARCHAR, // zip
				Types.VARCHAR, // phone
				Types.DATE // created date
		};
	}
	
	@Override
	protected int[] getUpdateParamTypes() {
		return new int[] { 
				Types.VARCHAR, // name
				Types.VARCHAR, // address
				Types.VARCHAR, // city
				Types.VARCHAR, // state
				Types.VARCHAR, // zip
				Types.VARCHAR, // phone
				Types.DATE, // created date
				Types.BIGINT
		};
	}
	
	protected Integer create(Client client) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		Date dateCreated = new Date();
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				client.getName(),
				client.getAddress(),
				client.getCity(),
				client.getState(),
				client.getZip(),
				client.getPhone(),
				dateCreated
		}), keyHolder);
		
		client.setId(keyHolder.getKey().intValue());
		return client.getId();
	}
	
	protected Integer update(Client client) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				client.getName(),
				client.getAddress(),
				client.getCity(),
				client.getState(),
				client.getZip(),
				client.getPhone(),
				new Date(),
				client.getId()
		}));
		
		return client.getId();
	}

}
