package com.kbs.geo.coastal.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.ClientDao;
import com.kbs.geo.coastal.dao.ClientRequestDao;
import com.kbs.geo.coastal.dao.ClientStatisticsDao;
import com.kbs.geo.coastal.dao.RequestErrorDao;
import com.kbs.geo.coastal.model.billing.ClientStatistics;
import com.kbs.geo.coastal.model.billing.RequestType;
import com.kbs.geo.coastal.service.ClientStatisticsService;

@Service
public class ClientStatisticsServiceImpl implements ClientStatisticsService {

	@Autowired
	private ClientStatisticsDao clientStatisticsDao;
	
	@Autowired
	private ClientRequestDao clientRequestDao;
	
	@Autowired
	private RequestErrorDao requestErrorDao;
	
	@Autowired
	private ClientDao clientDao;

	@Override
	public Integer save(ClientStatistics clientAuth) {
		return clientStatisticsDao.save(clientAuth);
	}
	
	@Override
	public void save(List<ClientStatistics> statistics) {
		clientStatisticsDao.save(statistics);
	}

	@Override
	public ClientStatistics get(Integer id) {
		return populateObjectProperties(clientStatisticsDao.get(id));
	}

	@Override
	public List<ClientStatistics> getByClientId(Integer clientId) {
		List<ClientStatistics> list = clientStatisticsDao.getByClientId(clientId);
		for(ClientStatistics obj: list) {
			populateObjectProperties(obj);
		}
		return list;
	}
	
	@Override
	public List<ClientStatistics> getCurrentByClientId(Integer clientId) {
		Calendar today = Calendar.getInstance();
		return getByClientIdYearMonth(clientId, today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1);
	}

	@Override
	public List<ClientStatistics> getByClientIdYearMonth(Integer clientId,
			Integer year, Integer month) {
		return clientStatisticsDao.getByClientIdYearMonth(clientId, year, month);
	}

	@Override
	public List<ClientStatistics> getByClientIdRequestType(Integer clientId,
			RequestType requestType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<ClientStatistics> generateStatistics(Integer clientId, Integer year, Integer month) {
		List<ClientStatistics> clientStatistics = new ArrayList<ClientStatistics>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date beginDate = cal.getTime();
		
		cal.add(Calendar.MONTH, 1);
		Date endDate = cal.getTime();
		cal = null;
		
		for(RequestType requestType: RequestType.values()) {
			ClientStatistics clientStatistic = new ClientStatistics();
			clientStatistic.setClientId(clientId);
			clientStatistic.setRequestType(requestType);
			clientStatistic.setYear(year);
			clientStatistic.setMonth(month);
			
			Long successCount = clientRequestDao.getCount(clientId, beginDate, endDate, requestType);
			clientStatistic.setRequestCount(null == successCount ? 0 : successCount);
			
			Long errorCount = requestErrorDao.getCount(clientId, beginDate, endDate, requestType);
			clientStatistic.setErrorCount( null == errorCount ? 0 : errorCount );
			
			clientStatistics.add(clientStatistic);
		}
		return clientStatistics;
	}

	private ClientStatistics populateObjectProperties(ClientStatistics clientStatistics) {
		if(null == clientStatistics) return null;
		clientStatistics.setClient(clientDao.get(clientStatistics.getClientId()));
		return clientStatistics;
	}

}
