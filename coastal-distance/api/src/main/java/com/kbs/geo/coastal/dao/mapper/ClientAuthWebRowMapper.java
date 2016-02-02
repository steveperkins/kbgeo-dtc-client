package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientAuthWeb;

public class ClientAuthWebRowMapper implements RowMapper<ClientAuthWeb> {
	
	@Override
	public ClientAuthWeb mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientAuthWeb ip = new ClientAuthWeb();
		ip.setId(rs.getInt("id"));
		ip.setClientAuthId(rs.getInt("client_auth_id"));
		ip.setClientContactId(rs.getInt("client_contact_id"));
		ip.setUsername(rs.getString("username"));
		ip.setPassword(rs.getString("password"));
		ip.setStarts(rs.getTimestamp("starts"));
		ip.setExpires(rs.getTimestamp("expires"));
		
		ip.setCreated(rs.getTimestamp("created"));
		ip.setUpdated(rs.getTimestamp("updated"));
		return ip;
	}
}
