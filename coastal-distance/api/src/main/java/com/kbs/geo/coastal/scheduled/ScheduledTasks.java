package com.kbs.geo.coastal.scheduled;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.model.billing.Client;
import com.kbs.geo.coastal.model.billing.ClientStatistics;
import com.kbs.geo.coastal.model.billing.RequestType;
import com.kbs.geo.coastal.service.ClientService;
import com.kbs.geo.coastal.service.ClientStatisticsService;

@Component
public class ScheduledTasks {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientStatisticsService clientStatisticsService;
	
	/**
	 * Calculates client statistics (# requests per type, # errors per type) every night at 11:00 PM
	 */
//	@Scheduled(cron="0 21 * * * *")
//	@Scheduled(fixedDelay=120000)
	public void aggregateClientStatistics() {
		Calendar cal = Calendar.getInstance();
		Integer year = cal.get(Calendar.YEAR);
		Integer month = cal.get(Calendar.MONTH) + 1;
		
		// First get all active clients
		List<Client> activeClients = clientService.getAllActive();
		
		// Aggregate statistics for each client
		for(Client client: activeClients) {
			List<ClientStatistics> newStatistics = clientStatisticsService.generateStatistics(client.getId(), year, month);
			// See if there's already a statistics entry for this month
			List<ClientStatistics> currentStatistics = clientStatisticsService.getCurrentByClientId(client.getId());
			if(null == currentStatistics || currentStatistics.isEmpty()) {
				currentStatistics = newStatistics;
			} else {
				// Update the existing statistics for this month
				for(ClientStatistics newStatistic: newStatistics) {
					ClientStatistics currentStatistic = getByRequestType(currentStatistics, newStatistic.getRequestType());
					if(null == currentStatistic) {
						currentStatistics.add(newStatistic);
					} else {
						currentStatistic.setRequestCount(newStatistic.getRequestCount());
						currentStatistic.setErrorCount(newStatistic.getErrorCount());
					}
				}
			}
			clientStatisticsService.save(currentStatistics);
			
		}
	}
	
	private ClientStatistics getByRequestType(List<ClientStatistics> list, RequestType requestType) {
		for(ClientStatistics statistics: list) {
			if(null != statistics.getRequestType() && statistics.getRequestType().getId().equals(requestType.getId())) {
				return statistics;
			}
		}
		return null;
	}
}
