package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientAuthReferer;

public class ClientAuthRefererRowMapper implements RowMapper<ClientAuthReferer> {
	
	@Override
	public ClientAuthReferer mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientAuthReferer clientAuthReferer = new ClientAuthReferer();
		clientAuthReferer.setId(rs.getInt("id"));
		clientAuthReferer.setClientAuthId(rs.getInt("client_auth_id"));
		clientAuthReferer.setName(rs.getString("name"));
		clientAuthReferer.setReferers(rs.getString("referers"));
		
		clientAuthReferer.setCreated(rs.getTimestamp("created"));
		clientAuthReferer.setUpdated(rs.getTimestamp("updated"));
		return clientAuthReferer;
	}
}
