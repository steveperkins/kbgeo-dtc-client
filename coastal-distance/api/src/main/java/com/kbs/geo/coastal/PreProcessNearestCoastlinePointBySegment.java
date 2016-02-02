package com.kbs.geo.coastal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.dao.CoastlinePointDao;
import com.kbs.geo.coastal.dao.CoastlineSegmentDao;
import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.CoastlineSegment;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.CoastlinePointService;
import com.kbs.geo.coastal.service.impl.DistanceGridServiceImpl;
//@RestController
public class PreProcessNearestCoastlinePointBySegment {

	public static void main(String[] args) {
		new PreProcessNearestCoastlinePointBySegment().process();
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(DistanceGridServiceImpl.class);
	private static final int BATCH_THRESHOLD = 50;
	
	@Autowired
	private CoastlinePointDao coastlinePointDao;
	@Autowired
	private GridPointDao gridPointDao;
	@Autowired
	private CoastlineSegmentDao coastlineSegmentDao;
	@Autowired
	private CoastlinePointService coastlinePointService;
	
	Integer kbsClientId = 1;
	Integer currentClientId = 2;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	
	public PreProcessNearestCoastlinePointBySegment() {
//		SpringContextHolder.context = new ClassPathXmlApplicationContext("servlet-context.xml");
//		coastlinePointDao  = (CoastlinePointDao)SpringContextHolder.context.getBean(CoastlinePointDao.class);
//		gridPointDao = (GridPointDao)SpringContextHolder.context.getBean(GridPointDao.class);
//		coastlineSegmentDao = (CoastlineSegmentDao)SpringContextHolder.context.getBean(CoastlineSegmentDao.class);
//		coastlinePointService = (CoastlinePointService)SpringContextHolder.context.getBean(CoastlinePointService.class);
	}
	
//	@RequestMapping(value="preprocessBySegment", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void process() {
		long x = 0;
		
		List<CoastlineSegment> clientSegments = coastlineSegmentDao.getAll(currentClientId);
		List<GridPoint> gridPoints = gridPointDao.getAll();
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		for(GridPoint gridPoint: gridPoints) {
			executorService.execute(new DistanceToCoastCalculator(gridPoint, clientSegments));
			x++;
		}
		
		LOG.info("Queued " + x + " threads, waiting for executor service to shut down...");
		try {
			executorService.shutdown();
			executorService.awaitTermination(20, TimeUnit.DAYS);
			if(!gridPointsToUpdate.isEmpty()) {
				gridPointDao.update(gridPointsToUpdate);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.info("Done.");
		
	}
	
	private void addGridPoint(GridPoint gridPoint) {
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting...");
			gridPointDao.update(gridPointsToUpdate);
			gridPointsToUpdate.clear();
		}
	}
	
	class DistanceToCoastCalculator implements Runnable {
		private GridPoint gridPoint;
		private List<CoastlineSegment> segments;
		public DistanceToCoastCalculator(GridPoint gridPoint, List<CoastlineSegment> segments) {
			this.gridPoint = gridPoint;
			this.segments = segments;
			LOG.info("Thread created for " + gridPoint.getLat() + ", " + gridPoint.getLng());
		}
		
		@Override
		public void run() {
			CoastlinePoint currentWinner = null;
			Double minDistance = 999999999.0;
			
			for(CoastlineSegment segment: segments) {
				List<CoastlinePoint> coastlinePoints = coastlinePointDao.getByClientSegment(currentClientId, segment.getId());
				for(CoastlinePoint coastlinePoint: coastlinePoints) {
					// Use Great Circle formula to calculate minimum distance to coast from the current grid point
					Double milesBetween = coastlinePointService.getMilesBetween(new LatLng(gridPoint.getLat(), gridPoint.getLng()), coastlinePoint);
					if(milesBetween < minDistance) {
						minDistance = milesBetween;
						currentWinner = coastlinePoint;
					}
				}
			}
			gridPoint.setClosestCoastlinePointId(currentWinner.getId());
			gridPoint.setDistanceInMiles(minDistance);
			gridPoint.setClientId(currentClientId);
			addGridPoint(gridPoint);
			LOG.info("Done with grid point {}", gridPoint);
		}
	}

}
