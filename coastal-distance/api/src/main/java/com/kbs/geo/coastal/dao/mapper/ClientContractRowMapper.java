package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientContract;
import com.kbs.geo.coastal.model.billing.RequestType;

public class ClientContractRowMapper implements RowMapper<ClientContract> {
	
	@Override
	public ClientContract mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientContract contract = new ClientContract();
		contract.setId(rs.getInt("id"));
		contract.setClientId(rs.getInt("client_id"));
		contract.setName(rs.getString("name"));
		
		contract.setMaxRequests(rs.getLong("max_requests"));
		contract.setCentsPerRequest(rs.getInt("cents_per_request"));
		
		Integer requestTypeId = rs.getInt("request_type_id");
		if(null != requestTypeId) {
			contract.setRequestType(RequestType.fromId(requestTypeId));
		}
		
		contract.setStarts(rs.getTimestamp("starts"));
		contract.setExpires(rs.getTimestamp("expires"));
		contract.setCreated(rs.getTimestamp("created"));
		contract.setUpdated(rs.getTimestamp("updated"));
		return contract;
	}
}
