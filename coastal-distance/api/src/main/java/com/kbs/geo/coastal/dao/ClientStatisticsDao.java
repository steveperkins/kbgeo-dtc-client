package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientStatistics;

public interface ClientStatisticsDao {
	Integer save(ClientStatistics statistics);
	void save(List<ClientStatistics> list);
	ClientStatistics get(Integer id);
	List<ClientStatistics> getByClientId(Integer clientId);
	List<ClientStatistics> getByClientIdYearMonth(Integer clientId, Integer year, Integer month);
}
