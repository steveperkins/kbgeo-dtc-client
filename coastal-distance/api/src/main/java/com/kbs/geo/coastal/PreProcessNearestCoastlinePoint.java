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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.CoastlinePointService;
import com.kbs.geo.coastal.service.impl.DistanceGridServiceImpl;

//@RestController
public class PreProcessNearestCoastlinePoint {

	public static void main(String[] args) {
		new PreProcessNearestCoastlinePoint().process();
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(DistanceGridServiceImpl.class);
	private static final int BATCH_THRESHOLD = 100;
//	private static final Double EARTH_CIRCUMFERENCE_IN_STATUTE_MILES = 3963.1; // See http://www8.nau.edu/cvm/latlon_formula.html
	private final Long INCREMENT = 50L;
	
	@Autowired
	private CoastlinePointService coastlinePointService;
	
	@Autowired
	private GridPointDao gridPointDao;
	
	Integer kbsClientId = 1;
	Integer currentClientId = 2;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	
	public PreProcessNearestCoastlinePoint() {
		/*SpringContextHolder.context = new ClassPathXmlApplicationContext("servlet-context.xml");
		coastlinePointDao  = (CoastlinePointDao)SpringContextHolder.context.getBean(CoastlinePointDao.class);
		gridPointDao = (GridPointDao)SpringContextHolder.context.getBean(GridPointDao.class);*/
	}
	
//	@RequestMapping("/preprocessSingleThread")
	public void process() {
		Long lastGridPointId = 0L;
		
		long x = 0;
		List<GridPoint> gridPoints;
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		do {
			gridPoints = gridPointDao.getPointsBetween(currentClientId, lastGridPointId, lastGridPointId + INCREMENT);
			for(GridPoint gridPoint: gridPoints) {
				executorService.execute(new DistanceToCoastCalculator(gridPoint));
				x++;
			}
			lastGridPointId += INCREMENT;
		} while(gridPoints.size() > 0);
		
		
		LOG.info("Queued " + x + " threads, waiting for executor service to shut down...");
		try {
			executorService.shutdown();
			executorService.awaitTermination(20, TimeUnit.DAYS);
			if(!gridPoints.isEmpty()) {
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
	
/*	protected Integer getMilesBetween(Double lat1, Double lon1, Double lat2, Double lon2) {
		Double coordinateLat = Math.toRadians(lat1);
		Double coordinateLon = Math.toRadians(lon1);
		
		Double coastalLat = Math.toRadians(lat2);
		Double coastalLon = Math.toRadians(lat2);
		
		return (int) Math.round(Math.acos(Math.sin(coordinateLat) * Math.sin(coastalLat) + Math.cos(coordinateLat) * Math.cos(coastalLat) * Math.cos(coastalLon - coordinateLon)) * EARTH_CIRCUMFERENCE_IN_STATUTE_MILES);
	}*/
	
	class DistanceToCoastCalculator implements Runnable {
		private GridPoint gridPoint;
		public DistanceToCoastCalculator(GridPoint gridPoint) {
			this.gridPoint = gridPoint;
			LOG.info("Thread created for " + gridPoint.getLat() + ", " + gridPoint.getLng());
		}
		
		@Override
		public void run() {
			Double lastCoastalPointSortOrder = 0.0;
			List<CoastlinePoint> coastlinePoints = null;
			CoastlinePoint currentWinner = null;
			Double minDistance = 999999999.0;
			do {
				// Get the next X coastline points
				coastlinePoints = coastlinePointService.getBetween(currentClientId, lastCoastalPointSortOrder, lastCoastalPointSortOrder + INCREMENT);
				for(CoastlinePoint coastlinePoint: coastlinePoints) {
					// Use Great Circle formula to calculate minimum distance to coast from the current grid point
					Double milesBetween = coastlinePointService.getMilesBetween(new LatLng(gridPoint.getLat(), gridPoint.getLng()), coastlinePoint);
					if(milesBetween < minDistance) {
						minDistance = milesBetween;
						currentWinner = coastlinePoint;
					}
				}
				lastCoastalPointSortOrder += INCREMENT;
			} while(coastlinePoints.size() > 0);
			
			gridPoint.setClosestCoastlinePointId(currentWinner.getId());
			gridPoint.setDistanceInMiles(minDistance);
			gridPoint.setClientId(currentClientId);
			addGridPoint(gridPoint);
			LOG.info("Done with grid point {}", gridPoint);
		}
	}

}
