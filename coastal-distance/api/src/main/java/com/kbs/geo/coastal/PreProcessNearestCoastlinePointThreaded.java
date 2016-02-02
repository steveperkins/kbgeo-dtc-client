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

import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.CoastlinePointService;

//@RestController
public class PreProcessNearestCoastlinePointThreaded {

	public static void main(String[] args) {
		new PreProcessNearestCoastlinePointThreaded().process();
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(PreProcessNearestCoastlinePointThreaded.class);
	private static final int BATCH_THRESHOLD = 30;
//	private static final Double EARTH_CIRCUMFERENCE_IN_STATUTE_MILES = 3963.1; // See http://www8.nau.edu/cvm/latlon_formula.html
//	private static final Double MILES_PER_DEGREE = 57.295780; // was 60
	private final Long INCREMENT = 200L;
	
	@Autowired
	private CoastlinePointService coastlinePointDao;
	@Autowired
	private GridPointDao gridPointDao;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	List<CoastlinePoint> coastlinePoints;
	
//	@RequestMapping(value="preprocessCoastlineThreaded", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void process() {
		
		long x = 0;
		long offset = INCREMENT;
		coastlinePoints = coastlinePointDao.getAll(1);
		
		List<GridPoint> gridPoints = gridPointDao.getOffset(0L, offset);
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		do {
			if(null == gridPoints || gridPoints.isEmpty()) break;
			executorService.execute(new DistanceToCoastCalculator(gridPoints));
			x++;
			
//			lastGridPointId = lastGridPointId + 1;
			LOG.info("Retrieving grid points " + offset + " to " + (offset + INCREMENT));
			gridPoints = gridPointDao.getOffset(offset, offset+ INCREMENT);
			offset += INCREMENT;
			if(null == gridPoints || gridPoints.isEmpty()) break;
			LOG.info("LOOPING");
		} while(null != gridPoints && gridPoints.size() > 0);
		
		LOG.info("Queued " + x + " threads, waiting for executor service to shut down...");
		try {
			executorService.shutdown();
			executorService.awaitTermination(20, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!gridPointsToUpdate.isEmpty()) {
			gridPointDao.update(gridPointsToUpdate);
		}
		LOG.info("Done.");
		
	}
	
	private void updateGridPoint(GridPoint gridPoint) {
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting...");
			gridPointDao.update(new ArrayList<GridPoint>(gridPointsToUpdate));
			gridPointsToUpdate.clear();
		}
	}
	
	class DistanceToCoastCalculator implements Runnable {
		private List<GridPoint> gridPoints;
		public DistanceToCoastCalculator(List<GridPoint> gridPoints) {
			this.gridPoints = gridPoints;
			LOG.info("Thread created for " + gridPoints.size() + " points");
		}
		
		@Override
		public void run() {
			if(null == gridPoints) return;
			for(GridPoint gridPoint: gridPoints) {
				CoastlinePoint currentWinner = null;
				Double minDistance = 99999999.0;
				for(CoastlinePoint coastlinePoint: coastlinePoints) {
					LOG.info("Calculating DTC for gridPoint [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "] coastline point [" + coastlinePoint.getLat() + ", " + coastlinePoint.getLng() + "]");
					// Use Great Circle formula to calculate minimum distance to coast from the current grid point
					Double milesBetween = coastlinePointDao.getMilesBetween(new LatLng(gridPoint.getLat(), gridPoint.getLng()), coastlinePoint);
					if(milesBetween < minDistance) {
						minDistance = milesBetween;
						currentWinner = coastlinePoint;
					} else if(minDistance < 1000 && milesBetween - minDistance > 300) { // If we're more than 300 miles away from the yet-closest point, we probably won't find a closer one
						break;
					}
				}
				
				gridPoint.setClosestCoastlinePointId(currentWinner.getId());
				updateGridPoint(gridPoint);
			}
		}
	}

}
