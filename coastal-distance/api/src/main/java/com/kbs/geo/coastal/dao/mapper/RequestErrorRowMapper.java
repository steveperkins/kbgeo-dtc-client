package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.RequestError;
import com.kbs.geo.coastal.model.billing.RequestType;

public class RequestErrorRowMapper implements RowMapper<RequestError> {
	
	@Override
	public RequestError mapRow(ResultSet rs, int rowNum) throws SQLException {
		RequestError error = new RequestError();
		error.setId(rs.getInt("id"));
		error.setClientId(rs.getInt("client_id"));
		error.setSourceIp(rs.getString("source_ip"));
		error.setRequestUrl(rs.getString("request_url"));
		
		Integer requestTypeId = rs.getInt("request_type_id");
		if(null != requestTypeId) {
			error.setRequestType(RequestType.fromId(requestTypeId));
		}
		
		error.setResponseStatus(rs.getInt("response_status"));
		error.setResponseBody(rs.getString("response_body"));
		error.setCreated(rs.getTimestamp("created"));
		return error;
	}

}
