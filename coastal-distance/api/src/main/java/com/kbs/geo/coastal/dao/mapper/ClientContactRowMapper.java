package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientContact;

public class ClientContactRowMapper implements RowMapper<ClientContact> {
	
	@Override
	public ClientContact mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientContact contact = new ClientContact();
		contact.setId(rs.getInt("id"));
		contact.setClientId(rs.getInt("client_id"));
		contact.setLastName(rs.getString("last_name"));
		contact.setFirstName(rs.getString("first_name"));
		contact.setEmail(rs.getString("email"));
		contact.setAddress(rs.getString("address"));
		contact.setCity(rs.getString("city"));
		contact.setState(rs.getString("state"));
		contact.setZip(rs.getString("zip"));
		contact.setPhone(rs.getString("phone"));
		contact.setPrimary(rs.getBoolean("is_primary"));
		
		contact.setCreated(rs.getTimestamp("created"));
		contact.setUpdated(rs.getTimestamp("updated"));
		return contact;
	}
}
