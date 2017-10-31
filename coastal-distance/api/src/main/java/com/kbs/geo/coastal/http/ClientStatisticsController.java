package com.kbs.geo.coastal.http;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.model.billing.ClientStatistics;
import com.kbs.geo.coastal.service.ClientStatisticsService;
import com.kbs.geo.http.security.KbConsoleUserContext;

@RestController
public class ClientStatisticsController {
	private static final Logger LOG = Logger.getLogger(ClientStatisticsController.class);
	
	@Autowired
	private KbConsoleUserContext kbContext;
	
	@Autowired
	private ClientStatisticsService clientStatisticsService;
	
	@RequestMapping(value="console/statistics", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ClientStatistics> getCurrentStatistics (HttpServletResponse response) {
		LOG.info(String.format("GET /console/statistics for user %s", kbContext.getClientAuthWeb().getUsername()));
		List<ClientStatistics> clientStatistics = clientStatisticsService.getByClientId(kbContext.getClientAuthWeb().getClientAuth().getClientId());
		return clientStatistics;
    }
	
}
