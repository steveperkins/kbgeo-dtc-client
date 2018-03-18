package com.kbs.geo.coastal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.dao.GridPoint128Dao;
import com.kbs.geo.coastal.dao.GridPoint16Dao;
import com.kbs.geo.coastal.dao.GridPoint32Dao;
import com.kbs.geo.coastal.dao.GridPoint64Dao;
import com.kbs.geo.coastal.dao.GridPoint8Dao;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.CoastlinePointService;

/**
 * Takes as input a low-resolution grid point table with coastline points mapped. Adds half of that table's grid point distance to each point's lat and lon to create an intermediary
 * point in the middle of the grid point's "box". Uses the low-resolution grid point's min/max nearest coastline point ids to retrieve all coastline points that <i>could</i>
 * be the nearest for the new point. Runs through all returned coastline points to find the nearest and adds the new grid point to a higher-resolution table.
 * 
 * Repeat as many times as necessary with higher and higher-resolution tables. Change:
 * INCREMENT_MILES	to your target resolution in miles
 * gridPointWDao	to your low-resolution table's DAO
 * gridPointXDao	to your high-resolution table's DAO
 * @author Steve
 *
 */
//@RestController
public class CreateUSGridPointsTertiaryController {

	private static final Logger LOG = LoggerFactory.getLogger(CreateUSGridPointsTertiaryController.class);
	// Record only lat/lon within the continental US
	private static final Double MAX_LATITUDE = 50.0;
	private static final Double MAX_LONGITUDE = -70.0;
	private static final Double MIN_LATITUDE = 25.0;
	private static final Double MIN_LONGITUDE = -125.0;
	
	private static final int BATCH_THRESHOLD = 100;
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private GridPoint16Dao gridPointWDao;
	
	@Autowired
	private GridPoint8Dao gridPointXDao;
	
	@Autowired
	private CoastlinePointService coastlinePointService;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	Long pointCount = 0L;
	Long recordCount = 0L;
	
	Integer kbsClientId = 1;
	
//	@RequestMapping(value="createGridPointsTertiary", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void createGridPoints() {
		long lastGridPointId = 0;
		List<GridPoint> gridPoints = gridPointXDao.getOffset(lastGridPointId, lastGridPointId + BATCH_THRESHOLD);
		
	/*	while(null != gridPoints && gridPoints.size() > 0) {
			int x;
			for(x = 0; x < gridPoints.size(); x++) {
				GridPoint gridPoint = gridPoints.get(x);
				if( (gridPoint.getLat()>= MIN_LATITUDE && gridPoint.getLat() <= MAX_LATITUDE)
						&& (gridPoint.getLng()>= MIN_LONGITUDE && gridPoint.getLng() <= MAX_LONGITUDE)) {
					
					LatLng newGridPointlatLng = new LatLng(gridPoint.getLat(), gridPoint.getLng());
					List<GridPoint> pointsSurrounding = gridPointWDao.getPointsSurrounding(newGridPointlatLng);
					int y;
					Double  minimumCoastlinePointId = 999999.0;
					Double maximumCoastlinePointId = -1.0;
					for(y = 0; y < pointsSurrounding.size(); y++) {
						GridPoint calculatedPoint = pointsSurrounding.get(y);
						if(minimumCoastlinePointId > calculatedPoint.getClosestCoastlinePointId()) minimumCoastlinePointId = calculatedPoint.getClosestCoastlinePointId();
						if(maximumCoastlinePointId < calculatedPoint.getClosestCoastlinePointId()) maximumCoastlinePointId = calculatedPoint.getClosestCoastlinePointId();
					}
					
					CoastlinePoint currentWinner = null;
					Double minDistance = 99999999.0;
					int z;
					List<CoastlinePoint> fireDeptPoints = coastlinePointService.getBetween(kbsClientId, minimumCoastlinePointId, maximumCoastlinePointId);
					for(z = 0; z < fireDeptPoints.size(); z++) {
						CoastlinePoint coastlinePoint = fireDeptPoints.get(z);
						LOG.info("Calculating DTC for gridPoint [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "] coastline point [" + coastlinePoint.getLat() + ", " + coastlinePoint.getLng() + "]");
						// Use Great Circle formula to calculate minimum distance to coast from the target point
						Double milesBetween = coastlinePointService.getMilesBetween(newGridPointlatLng, coastlinePoint);
						if(milesBetween < minDistance) {
							minDistance = milesBetween;
							currentWinner = coastlinePoint;
						}
					}
					
					gridPoint.setDistanceInMiles(minDistance);
					gridPoint.setClosestCoastlinePointId(currentWinner.getId());
					updateGridPoint(gridPoint);
				}
				lastGridPointId = gridPoint.getId();
			}
			
			gridPoints = gridPointXDao.getOffset(lastGridPointId, lastGridPointId + BATCH_THRESHOLD);
		}
		updateGridPoints(gridPointsToUpdate);
		LOG.info("Done");*/
	}
	
	private void updateGridPoint(GridPoint gridPoint) {
		LOG.info("Adding grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "]");
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Updating" +
					" records " + recordCount + "-" + (recordCount + gridPointsToUpdate.size()));
			updateGridPoints(gridPointsToUpdate);
			recordCount += gridPointsToUpdate.size();
			gridPointsToUpdate.clear();
		}
	}
	
	private void updateGridPoints(List<GridPoint> gridPoints) {
		LOG.info("Updating " + gridPoints.size() + " gridPoints");
		gridPointXDao.update(gridPoints);
	}
	
}
