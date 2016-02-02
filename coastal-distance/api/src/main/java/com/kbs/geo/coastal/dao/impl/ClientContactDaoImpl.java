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

import com.kbs.geo.coastal.dao.ClientContactDao;
import com.kbs.geo.coastal.dao.mapper.ClientContactRowMapper;
import com.kbs.geo.coastal.model.billing.ClientContact;

@Component
public class ClientContactDaoImpl extends AbstractDao<ClientContact> implements ClientContactDao {
	private static final String INSERT_SQL = "INSERT INTO client_contact (client_id, last_name, first_name, email, address, city, state, zip, phone, is_primary, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE client_contact SET client_id=?, last_name=?, first_name=? email=?, address=?, city=?, state=?, zip=?, phone=?, updated=? WHERE id=?";
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM client_contact WHERE id=?";
	private static final String SELECT_BY_CLIENT_ID_SQL = "SELECT * FROM client_contact WHERE client_id=?";
	
	@Autowired
	private DataSource datasource;
	
	public ClientContactDaoImpl() {
		super.INSERT_SQL = INSERT_SQL;
		super.UPDATE_SQL = UPDATE_SQL;
	}
	
	@Override
	public ClientContact get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientContact> clientContact = jdbcTemplate.query(SELECT_BY_ID_SQL, new Object[]{ id }, new ClientContactRowMapper());
		if(null != clientContact && !clientContact.isEmpty()) return clientContact.get(0);
		return null;
	}
	
	@Override
	public List<ClientContact> getByClientId(Integer clientId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		List<ClientContact> clientContacts = jdbcTemplate.query(SELECT_BY_CLIENT_ID_SQL, new Object[]{ clientId }, new ClientContactRowMapper());
		return (null == clientContacts ? new ArrayList<ClientContact>() : clientContacts);
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
	
	protected Integer create(ClientContact clientContact) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientContact.getClientId(),
				clientContact.getLastName(),
				clientContact.getFirstName(),
				clientContact.getEmail(),
				clientContact.getAddress(),
				clientContact.getCity(),
				clientContact.getState(),
				clientContact.getZip(),
				clientContact.getPhone(),
				clientContact.isPrimary(),
				new Date()
		}), keyHolder);
		
		clientContact.setId(keyHolder.getKey().intValue());
		return clientContact.getId();
	}
	
	protected Integer update(ClientContact clientContact) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getUpdateStatementFactory().newPreparedStatementCreator(new Object[] { 
				clientContact.getClientId(),
				clientContact.getLastName(),
				clientContact.getFirstName(),
				clientContact.getEmail(),
				clientContact.getAddress(),
				clientContact.getCity(),
				clientContact.getState(),
				clientContact.getZip(),
				clientContact.getPhone(),
				clientContact.isPrimary(),
				new Date(),
				clientContact.getId()
		}));
		
		return clientContact.getId();
	}
	
}
