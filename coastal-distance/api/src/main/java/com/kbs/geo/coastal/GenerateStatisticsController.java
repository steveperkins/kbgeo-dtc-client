package com.kbs.geo.coastal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.scheduled.ScheduledTasks;

@RestController
public class GenerateStatisticsController {

	private static final Logger LOG = LoggerFactory.getLogger(GenerateStatisticsController.class);
	
	@Autowired
	private ScheduledTasks scheduledTasks;
	
	@RequestMapping(value="runGenerateStatistics", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void process() {
		scheduledTasks.aggregateClientStatistics();
		
		LOG.info("Done.");
	}
}
