package com.kbs.geo.coastal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.dao.CoastlinePointDao;
import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.service.impl.DistanceGridServiceImpl;

//@RestController("admin")
//@RestController
public class PreProcessNearestCoastlinePointGridPointsFirst {

	public static void main(String[] args) {
		new PreProcessNearestCoastlinePointGridPointsFirst().process();
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(PreProcessNearestCoastlinePointGridPointsFirst.class);
	private static final int BATCH_THRESHOLD = 10;
//	private static final Double EARTH_CIRCUMFERENCE_IN_STATUTE_MILES = 3963.1; // See http://www8.nau.edu/cvm/latlon_formula.html
	private static final Double MILES_PER_DEGREE = 57.295780; // was 60
	private final Long INCREMENT = 100L;
	
	@Autowired
	private CoastlinePointDao coastlinePointDao;
	@Autowired
	private GridPointDao gridPointDao;
	
	Integer kbsClientId = 1;
	Integer currentClientId = 2;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	
/*	public PreProcessNearestCoastlinePointGridPointsFirst() {
		SpringContextHolder.context = new ClassPathXmlApplicationContext("servlet-context.xml");
		coastlinePointDao  = (CoastlinePointDao)SpringContextHolder.context.getBean(CoastlinePointDao.class);
		gridPointDao = (GridPointDao)SpringContextHolder.context.getBean(GridPointDao.class);
	}*/
//	@RequestMapping(value="preprocessCoastline", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void process() {
		Integer  lastGridPointId = -1;
		
		long x = 0;
//		List<CoastlinePoint> coastlinePoints = coastlinePointDao.getAll();
		List<CoastlinePoint> coastlinePoints = coastlinePointDao.getAll(1);
//		Collections.reverse(coastlinePoints);
		
		List<GridPoint> gridPoints = null;
//		ExecutorService executorService = Executors.newFixedThreadPool(30);
		/*do {
			lastGridPointId = lastGridPointId + 1;
			gridPoints = gridPointDao.getPointsBetween(currentClientId, 6371L, 19764L);
//			gridPointsToUpdate = gridPointDao.getPointsBetween(lastGridPointId, lastGridPointId + INCREMENT);
			for(GridPoint gridPoint: gridPoints) {
				System.out.println("x: " + x + " [" + gridPoint.getId() + "]");
				
				CoastlinePoint currentWinner = null;
				Double minDistance = 9999.0;
				Long y = -1L;
				for(CoastlinePoint coastlinePoint: coastlinePoints) {
					BigDecimal miles = getMilesBetween(gridPoint.getLat(), gridPoint.getLng(), coastlinePoint.getLat(), coastlinePoint.getLng());
					System.out.println(y + ": [" + gridPoint.getId() + "] - " + miles + " miles");
					if(miles < minDistance) {
						LOG.info("New winner for " + gridPoint.getLat() + "," + gridPoint.getLng() + " -   " + coastlinePoint.getLat() + "," + coastlinePoint.getLng() + ": " + miles + " miles");
						minDistance = miles;
						currentWinner = coastlinePoint;
					}
					y++;
				}
				
				gridPoint.setClosestCoastlinePointId(currentWinner.getId());
				gridPoint.setDistanceInMiles(minDistance);
				updateGridPoint(gridPoint);
				lastGridPointId = gridPoint.getId();
				x++;
				System.out.println("LOOPING");
				
				CoastlinePoint currentWinner = null;
				Integer minDistance = 9999;
				for(CoastlinePoint coastlinePoint: coastlinePoints) {
					Integer miles = getMilesBetween(gridPoint.getLat(), gridPoint.getLon(), coastlinePoint.getLat(), coastlinePoint.getLon());
					// Provide an early exit if we've already found the nearest coastline point
//					if(miles - minDistance > 1000) break;
					if(miles < minDistance) {
						LOG.info("New winner for " + gridPoint.getLat() + "," + gridPoint.getLon() + "   " + coastlinePoint.getLat() + "," + coastlinePoint.getLon() + ": " + miles + " miles");
						minDistance = miles;
						currentWinner = coastlinePoint;
					}
				}
				gridPoint.setClosestCoastlinePointId(currentWinner.getId());
				addGridPoint(gridPoint);
//				executorService.execute(new BruteForceDistanceToCoastCalculator(gridPoint));
				x++;
				lastGridPointId = gridPoint.getId();
			}
		} while(gridPoints.size() > 0);
		if(gridPointsToUpdate.size() > 0) {
			LOG.info("Inserting...");
			gridPointDao.update(gridPointsToUpdate);
		}*/
		
//		LOG.info("Queued " + x + " threads, waiting for executor service to shut down...");
//		try {
//			executorService.awaitTermination(20, TimeUnit.DAYS);
//			if(!gridPointsToUpdate.isEmpty()) {
//				gridPointDao.update(gridPointsToUpdate);
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		LOG.info("Done.");
		
	}
	
	private void updateGridPoint(GridPoint gridPoint) {
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting...");
			gridPointDao.update(gridPointsToUpdate);
			gridPointsToUpdate.clear();
		}
	}
	
	protected Double getMilesBetween(Double gridLatitude, Double gridLongitude, Double coastalLatitude, Double coastalLongitude) {
        Double gridLat = Math.toRadians(gridLatitude);
        Double gridLon = Math.toRadians(gridLongitude);
        Double coastalLat = Math.toRadians(coastalLatitude);
        Double coastalLon = Math.toRadians(coastalLongitude);

       /*************************************************************************
        * Compute using law of cosines
        *************************************************************************/
        // great circle distance in radians
        Double angle1 = Math.acos(Math.sin(gridLat) * Math.sin(coastalLat)
                      + Math.cos(gridLat) * Math.cos(coastalLat) * Math.cos(gridLon - coastalLon));

        // convert back to degrees
        angle1 = Math.toDegrees(angle1);

        // each degree on a great circle of Earth is 60 nautical miles
//        Double distance1 = 60 * angle1;


       /*************************************************************************
        * Compute using Haversine formula
        *************************************************************************/
        Double a = Math.pow(Math.sin((coastalLat-gridLat)/2), 2)
                 + Math.cos(gridLat) * Math.cos(coastalLat) * Math.pow(Math.sin((coastalLon-gridLon)/2), 2);

        // great circle distance in radians. UsesMath.min to prevent rounding error as distances approach 12,000 km (http://www.movable-type.co.uk/scripts/gis-faq-5.1.html) 
        Double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        // convert back to degrees
        angle2 = Math.toDegrees(angle2);

        // each degree on a great circle of Earth is 60 nautical miles
        Double distanceInMiles = MILES_PER_DEGREE * angle2;

        return distanceInMiles;
    }
	
/*	class DistanceToCoastCalculator implements Runnable {
		private GridPoint gridPoint;
		public DistanceToCoastCalculator(GridPoint gridPoint) {
			this.gridPoint = gridPoint;
			LOG.info("Thread createad for " + gridPoint.getLat() + ", " + gridPoint.getLng());
		}
		
		@Override
		public void run() {
			Double lastCoastalPointSortOrder = 0.0;
			List<CoastlinePoint> coastlinePoints = null;
			CoastlinePoint currentWinner = null;
			Double minDistance = 99999999.0;
			do {
				// Get the next X coastline points
				coastlinePoints = coastlinePointDao.getPointsBetween(currentClientId, lastCoastalPointSortOrder, lastCoastalPointSortOrder + INCREMENT);
				for(CoastlinePoint coastlinePoint: coastlinePoints) {
					// Use Great Circle formula to calculate minimum distance to coast from the current grid point
					Double milesBetween = getMilesBetween(gridPoint.getLat(), gridPoint.getLng(), coastlinePoint.getLat(), coastlinePoint.getLng());
					if(milesBetween < minDistance) {
						minDistance = milesBetween;
						currentWinner = coastlinePoint;
					}
				}
			} while(coastlinePoints.size() > 0);
			
			gridPoint.setClosestCoastlinePointId(currentWinner.getId());
			updateGridPoint(gridPoint);
		}
	}*/

}
