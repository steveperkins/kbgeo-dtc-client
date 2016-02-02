package com.kbs.geo.coastal.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.coastal.model.billing.ClientStatistics;
import com.kbs.geo.coastal.model.billing.RequestType;

public class ClientStatisticsRowMapper implements RowMapper<ClientStatistics> {
	
	@Override
	public ClientStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClientStatistics statistics = new ClientStatistics();
		statistics.setId(rs.getInt("id"));
		statistics.setClientId(rs.getInt("client_id"));
		statistics.setYear(rs.getInt("year"));
		statistics.setMonth(rs.getInt("month"));
		statistics.setRequestCount(rs.getLong("request_count"));
		statistics.setErrorCount(rs.getLong("error_count"));
		
		Integer requestTypeId = rs.getInt("request_type_id");
		if(null != requestTypeId) {
			statistics.setRequestType(RequestType.fromId(requestTypeId));
		}
		
		statistics.setCreated(rs.getTimestamp("created"));
		statistics.setUpdated(rs.getTimestamp("updated"));
		return statistics;
	}
}
