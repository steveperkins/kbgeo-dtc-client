package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientStatistics;
import com.kbs.geo.coastal.model.billing.RequestType;

public interface ClientStatisticsService {
	Integer save(ClientStatistics clientStatistics);
	void save(List<ClientStatistics> list);
	ClientStatistics get(Integer id);
	List<ClientStatistics> getByClientId(Integer clientId);
	List<ClientStatistics> getCurrentByClientId(Integer clientId);
	List<ClientStatistics> getByClientIdYearMonth(Integer clientId, Integer year, Integer month);
	List<ClientStatistics> getByClientIdRequestType(Integer clientId, RequestType requestType);
	List<ClientStatistics> generateStatistics(Integer clientId, Integer year, Integer month);
}
