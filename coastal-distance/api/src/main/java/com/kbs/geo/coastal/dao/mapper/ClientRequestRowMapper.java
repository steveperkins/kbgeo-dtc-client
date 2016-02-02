package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.model.billing.RequestType;

public class ClientRequestRowMapper implements RowMapper<ClientRequest> {
	
	@Override
	public ClientRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientRequest request = new ClientRequest();
		request.setId(rs.getInt("id"));
		request.setClientAuthId(rs.getInt("client_auth_id"));
		request.setSourceIp(rs.getString("source_ip"));
		request.setRequestUrl(rs.getString("request_url"));
		
		Integer requestTypeId = rs.getInt("request_type_id");
		if(null != requestTypeId) {
			request.setRequestType(RequestType.fromId(requestTypeId));
		}
		request.setRequestBody(rs.getString("request_body"));
		
		request.setResponseStatus(rs.getInt("response_status"));
		request.setResponseBody(rs.getString("response_body"));
		
		request.setRequestTime(rs.getTimestamp("request_time"));
		request.setResponseTime(rs.getTimestamp("response_time"));
		request.setError(rs.getBoolean("error"));
		request.setCreated(rs.getTimestamp("created"));
		return request;
	}
}
