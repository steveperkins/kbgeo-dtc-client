package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientAuthIp;

public class ClientAuthIpRowMapper implements RowMapper<ClientAuthIp> {
	
	@Override
	public ClientAuthIp mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientAuthIp ip = new ClientAuthIp();
		ip.setId(rs.getInt("id"));
		ip.setClientAuthId(rs.getInt("client_auth_id"));
		ip.setName(rs.getString("name"));
		ip.setIp(rs.getString("ip"));
		
		ip.setCreated(rs.getTimestamp("created"));
		ip.setUpdated(rs.getTimestamp("updated"));
		return ip;
	}
}
