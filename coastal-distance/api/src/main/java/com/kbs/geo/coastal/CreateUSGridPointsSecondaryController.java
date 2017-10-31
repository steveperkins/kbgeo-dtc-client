package com.kbs.geo.coastal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kbs.geo.coastal.dao.GridPoint128Dao;
import com.kbs.geo.coastal.dao.GridPoint64Dao;
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
public class CreateUSGridPointsSecondaryController {

	private static final Logger LOG = Logger.getLogger(CreateUSGridPointsSecondaryController.class);
	// Record only lat/lon within the continental US
	private static final BigDecimal MAX_LATITUDE = new BigDecimal(50.0);
	private static final BigDecimal MAX_LONGITUDE = new BigDecimal(-70.0);
	private static final BigDecimal MIN_LATITUDE = new BigDecimal(25.0);
	private static final BigDecimal MIN_LONGITUDE = new BigDecimal(-125.0);
	private static final Double LAT_DEGREE_IN_MILES = 69.0;
	private static final Double LON_DEGREE_IN_MILES = 53.0;
	
	private static final Double INCREMENT_MILES = 64.0;
	// One degree of latitude = 69 miles
	// One degree / 69 miles = 1-mile increments
	// One degree / 34.5 miles = 2-mile increments
	// One degree / 17.25 miles = 4-mile increments
	// One degree / 8.265 miles = 8-mile increments
	// One degree / 4.3125 miles = 16-mile increments
	// One degree / 2.15625 miles = 32-mile increments
	// One degree / 1.078125 miles = 64-mile increments
	// One degree / 0.5390625 miles = 128-mile increments
	private static final BigDecimal LAT_INCREMENT = new BigDecimal(1 / (LAT_DEGREE_IN_MILES / INCREMENT_MILES));
	// One degree of longitude = 53 miles
	// One degree / 53 miles = 1-mile increments
	// One degree / 26.5 miles = 2-mile increments
	// One degree / 13.25 miles = 4-mile increments
	// One degree / 6.625 miles = 8-mile increments
	// One degree / 3.3125 miles = 16-mile increments
	// One degree / 1.65625 miles = 32-mile increments
	// One degree / 0.671875 miles = 64-mile increments
	// One degree / 0.4140625 miles = 128-mile increments
	private static final BigDecimal LON_INCREMENT = new BigDecimal(1 / (LON_DEGREE_IN_MILES / INCREMENT_MILES));
	
	private static final int BATCH_THRESHOLD = 100;
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private GridPoint128Dao gridPointWDao;
	
	@Autowired
	private GridPoint64Dao gridPointXDao;
	
	@Autowired
	private CoastlinePointService coastlinePointService;
	
	List<GridPoint> gridPointsToInsert = Collections.synchronizedList(new ArrayList<GridPoint>());
	Long pointCount = 0L;
	Long recordCount = 0L;
	
	Integer kbsClientId = 1;
	
//	@RequestMapping(value="createGridPointsSecondary", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void createGridPoints() {
		long lastGridPointId = 0;
		List<GridPoint> gridPoints = gridPointWDao.getOffset(lastGridPointId, lastGridPointId + BATCH_THRESHOLD);
		
		while(null != gridPoints && gridPoints.size() > 0) {
			int x;
			for(x = 0; x < gridPoints.size(); x++) {
				GridPoint gridPoint = gridPoints.get(x);
				// Copy this grid point from the lower-resolution table to the higher-resolution table
				addGridPoint(gridPoint);
				// "split" this gridpoint's bounding "box" in half by adding half the original increment to create a new grid point in the "center" of the "box"
				GridPoint newGridPoint = new GridPoint(incrementLat(gridPoint.getLat()), incrementLon(gridPoint.getLng()), null);
				if( (newGridPoint.getLat().compareTo(MIN_LATITUDE) >= 0 && newGridPoint.getLat().compareTo(MAX_LATITUDE) <= 0)
						&& (newGridPoint.getLng().compareTo(MIN_LONGITUDE) >= 0 && newGridPoint.getLng().compareTo(MAX_LONGITUDE) <= 0) ) {
					
					LatLng newGridPointlatLng = new LatLng(newGridPoint.getLat(), newGridPoint.getLng());
					List<GridPoint> pointsSurrounding = gridPointWDao.getPointsSurrounding(newGridPointlatLng);
					int y;
					Double minimumCoastlinePointId = 999999.0;
					Double maximumCoastlinePointId = -1.0;
					/*for(y = 0; y < pointsSurrounding.size(); y++) {
						GridPoint calculatedPoint = pointsSurrounding.get(y);
						if(minimumCoastlinePointId > calculatedPoint.getClosestCoastlinePointId()) minimumCoastlinePointId = calculatedPoint.getClosestCoastlinePointId();
						if(maximumCoastlinePointId < calculatedPoint.getClosestCoastlinePointId()) maximumCoastlinePointId = calculatedPoint.getClosestCoastlinePointId();
					}*/
					
					CoastlinePoint currentWinner = null;
					Double minDistance = 99999999.0;
					int z;
					List<CoastlinePoint> coastlinePoints = coastlinePointService.getBetween(kbsClientId, minimumCoastlinePointId, maximumCoastlinePointId);
					for(z = 0; z < coastlinePoints.size(); z++) {
						CoastlinePoint coastlinePoint = coastlinePoints.get(z);
						LOG.info("Calculating DTC for gridPoint [" + newGridPoint.getLat() + ", " + newGridPoint.getLng() + "] coastline point [" + coastlinePoint.getLat() + ", " + coastlinePoint.getLng() + "]");
						// Use Great Circle formula to calculate minimum distance to coast from the target point
						Double milesBetween = coastlinePointService.getMilesBetween(newGridPointlatLng, coastlinePoint);
						if(milesBetween < minDistance) {
							minDistance = milesBetween;
							currentWinner = coastlinePoint;
						}
					}
					
					newGridPoint.setDistanceInMiles(minDistance);
					newGridPoint.setClosestCoastlinePointId(currentWinner.getId());
					addGridPoint(newGridPoint);
				}
				lastGridPointId = gridPoint.getId();
			}
			
			gridPoints = gridPointWDao.getOffset(lastGridPointId, lastGridPointId + BATCH_THRESHOLD);
		}
		createGridPoints(gridPointsToInsert);
		LOG.info("Done");
	}
	
	private BigDecimal incrementLat(BigDecimal currentLat) {
		return currentLat.add(LAT_INCREMENT);
	}
	private BigDecimal incrementLon(BigDecimal currentLon) {
		return currentLon.add(LON_INCREMENT);
	}
	
	private void addGridPoint(GridPoint gridPoint) {
		LOG.info("Adding grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "]");
		gridPointsToInsert.add(gridPoint);
		if(gridPointsToInsert.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting records " + recordCount + "-" + (recordCount + gridPointsToInsert.size()));
			createGridPoints(gridPointsToInsert);
			recordCount += gridPointsToInsert.size();
			gridPointsToInsert.clear();
		}
	}
	
	private void createGridPoints(List<GridPoint> gridPoints) {
		LOG.info("Inserting " + gridPoints.size() + " gridPoints");
		gridPointXDao.save(gridPoints);
	}
	
}
