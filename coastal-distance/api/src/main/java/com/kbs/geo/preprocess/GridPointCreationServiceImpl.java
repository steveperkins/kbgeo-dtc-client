package com.kbs.geo.preprocess;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.kbs.geo.firedept.model.FireGridPoint;

@Service
public class GridPointCreationServiceImpl implements GridPointCreationService {

	private static final Logger LOG = Logger.getLogger(GridPointCreationServiceImpl.class);
	// Record only lat/lon within the continental US
	private static final Double MAX_LATITUDE = 50.0;
	private static final Double MAX_LONGITUDE = -65.0;
	private static final Double MIN_LATITUDE = 25.0;
	private static final Double MIN_LONGITUDE = -125.0;
	private static final Double STARTING_LAT = 25.0;
	private static final Double STARTING_LON = -125.0;
	
	private static final int BATCH_THRESHOLD = 50;
	// One degree of latitude = 69 miles
	// One degree / 69 miles = 1-mile increments
	// One degree / 34.5 miles = 2-mile increments
	// One degree / 17.25 miles = 4-mile increments
	// One degree / 8.265 miles = 8-mile increments
	// One degree / 4.3125 miles = 16-mile increments
	// One degree / 2.15625 miles = 32-mile increments
	// One degree / 1.078125 miles = 64-mile increments
	// One degree / 0.5390625 miles = 128-mile increments
//	private static final Double LAT_INCREMENT = 1 / 0.5390625;
	// One degree of longitude = 53 miles
	// One degree / 53 miles = 1-mile increments
	// One degree / 26.5 miles = 2-mile increments
	// One degree / 13.25 miles = 4-mile increments
	// One degree / 6.625 miles = 8-mile increments
	// One degree / 3.3125 miles = 16-mile increments
	// One degree / 1.65625 miles = 32-mile increments
	// One degree / 0.671875 miles = 64-mile increments
	// One degree / 0.4140625 miles = 128-mile increments
//	private static final Double LON_INCREMENT = 1 / 0.4140625; //100x100 mile grid
	
	private Double LAT_LNG_INCREMENT = 1.0;
	
	@Autowired
	private DataSource datasource;
	
	private JdbcTemplate jdbcTemplate;
	
	List<FireGridPoint> gridPoints = Collections.synchronizedList(new ArrayList<FireGridPoint>());
	Long pointCount = 0L;
	Long recordCount = 0L;
	
	private String INSERT_GRID_POINT_SQL = "";
	
	@PostConstruct
	public void postConstruct() {
		jdbcTemplate = new JdbcTemplate(datasource);
	}
	
	/* (non-Javadoc)
	 * @see com.kbs.geo.preprocess.GridPointCreationService#createGridPoints(java.lang.Integer, java.lang.Double)
	 */
	@Override
	public void createInitialGridPoints(String tableSuffix, Double latLngIncrement) {
		LAT_LNG_INCREMENT = latLngIncrement;
		INSERT_GRID_POINT_SQL = String.format("INSERT INTO grid_point_%s (lat, lon) VALUES(?, ?)", tableSuffix);
		
		Double currentLat = STARTING_LAT;
		Double currentLon = STARTING_LON;
		
		while(true) {
			while(currentLat >= MIN_LATITUDE && currentLat <= MAX_LATITUDE) {
				LOG.info("Testing [" + currentLat + ", " + currentLon + "]");
				while(currentLon >= MIN_LONGITUDE && currentLon <= MAX_LONGITUDE) {
					LOG.info("(point=" + pointCount + ") Coord is in range");
					addGridPoint(new FireGridPoint(currentLat, currentLon, null));
					currentLon = incrementLon(currentLon);
					
					pointCount++;
				}
				// Start over at the bottom, but one grid increment to the right
				currentLon = MIN_LONGITUDE;
				currentLat = incrementLat(currentLat);
			}
			if(currentLat < MIN_LATITUDE || currentLat > MAX_LATITUDE) break;
		}
		createGridPoints(gridPoints);
		gridPoints.clear();
	}
	
	private Double incrementLat(Double currentLat) {
		return currentLat + LAT_LNG_INCREMENT;
	}
	private Double incrementLon(Double currentLon) {
		return currentLon + LAT_LNG_INCREMENT;
	}
	
	private void addGridPoint(FireGridPoint gridPoint) {
		LOG.info("Adding grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "]");
		gridPoints.add(gridPoint);
		if(gridPoints.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting records " + recordCount + "-" + (recordCount + gridPoints.size()));
			createGridPoints(gridPoints);
			recordCount += gridPoints.size();
			gridPoints.clear();
		}
	}
	
	private void createGridPoints(List<FireGridPoint> gridPoints) {
		LOG.info("Inserting " + gridPoints.size() + " gridPointsToUpdate");
		List<Object[]> params = new ArrayList<Object[]>();
		for(FireGridPoint gridPoint: gridPoints) {
			params.add(new BigDecimal[] { gridPoint.getLat().setScale(16, BigDecimal.ROUND_HALF_DOWN), gridPoint.getLng().setScale(16, BigDecimal.ROUND_HALF_DOWN) });
		}
		
		try {
			jdbcTemplate.batchUpdate(INSERT_GRID_POINT_SQL, params);
		} catch(BadSqlGrammarException e) {
			LOG.error(e);
			e.printStackTrace();
			if(e.getMostSpecificCause() instanceof BatchUpdateException) {
				LOG.error(((BatchUpdateException) e.getMostSpecificCause()).getNextException());
			} else {
				LOG.error(e.getMostSpecificCause());
			}
		}
	}
	
}
