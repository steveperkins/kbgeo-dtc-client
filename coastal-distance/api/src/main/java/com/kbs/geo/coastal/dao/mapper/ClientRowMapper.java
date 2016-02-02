package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.Client;

public class ClientRowMapper implements RowMapper<Client> {
	
	@Override
	public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
		Client client = new Client();
		client.setId(rs.getInt("id"));
		client.setName(rs.getString("name"));
		client.setAddress(rs.getString("address"));
		client.setCity(rs.getString("city"));
		client.setState(rs.getString("state"));
		client.setZip(rs.getString("zip"));
		client.setPhone(rs.getString("phone"));
		
		client.setCreated(rs.getTimestamp("created"));
		client.setUpdated(rs.getTimestamp("updated"));
		return client;
	}

}
