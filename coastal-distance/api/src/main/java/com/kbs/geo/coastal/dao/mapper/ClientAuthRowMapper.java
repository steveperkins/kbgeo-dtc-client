package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientAuth;

public class ClientAuthRowMapper implements RowMapper<ClientAuth> {
	
	@Override
	public ClientAuth mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientAuth auth = new ClientAuth();
		auth.setId(rs.getInt("id"));
		auth.setClientId(rs.getInt("client_id"));
		auth.setName(rs.getString("name"));
		auth.setToken(rs.getString("token"));
		
		auth.setExpires(rs.getTimestamp("expires"));
		auth.setCreated(rs.getTimestamp("created"));
		auth.setUpdated(rs.getTimestamp("updated"));
		return auth;
	}
}
