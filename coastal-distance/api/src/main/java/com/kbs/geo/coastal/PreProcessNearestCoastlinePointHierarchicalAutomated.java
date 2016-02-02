package com.kbs.geo.coastal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.dao.CoastlinePointDao;
import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.dao.mapper.ClientGridPointRowMapper;
import com.kbs.geo.coastal.dao.mapper.MinMaxCoastlinePointSortOrderRowMapper;
import com.kbs.geo.coastal.math.DefaultDistanceCalculatorImpl;
import com.kbs.geo.coastal.math.DistanceCalculator;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.MinMaxCoastlinePointSortOrder;
import com.kbs.geo.coastal.service.impl.DistanceGridServiceImpl;

/**
 * Preprocesses nearest coastline point by first brute-forcing nearest coastline point for grid_point_128,
 * then using that information to limit the number of coastline points needed to calculate for grid_point_64,
 * grid_point_32, etc. PROCESSES ALL THE WAY THROUGH - DO NOT RUN MULTIPLE TIMES. Takes over every grid_point_*
 * table for its own use - do not run concurrent processes using these tables.
 * 
 * When this processing is finished, the grid_point_16 table will hold the current closest coastline point IDs,
 * ready to be processed into the more complex main coastline_point_<client ID> system.
 * 
 * @author Steve
 *
 */
@RestController
public class PreProcessNearestCoastlinePointHierarchicalAutomated {
	
	/*
	    -- First remove any previously-calculated points and their calculations:
	    
	    truncate grid_point_client_coastline_point_2;
		truncate coastline_point_2;
		SELECT pg_catalog.setval(pg_get_serial_sequence('coastline_point', 'id'), (SELECT MAX(id) FROM coastline_point)+1);
		
		-- Reset all grid points as unprocessed
		update grid_point_256 set distance_in_miles=null, coastline_point_id=null;
		update grid_point_128 set distance_in_miles=null, coastline_point_id=null;
		update grid_point_64 set distance_in_miles=null, coastline_point_id=null;
		update grid_point_32 set distance_in_miles=null, coastline_point_id=null;
		update grid_point_16 set distance_in_miles=null, coastline_point_id=null;
		update grid_point_8 set distance_in_miles=null, coastline_point_id=null;
		
		-- Do coastline point import...
		
		-- Then update the coastline point sort orders
		UPDATE coastline_point_2 set sort_order = v_table_name.rn
		FROM  
		(
		    SELECT row_number() over (partition by client_id order by id) AS rn, id
		    FROM coastline_point_2
		) AS v_table_name
		WHERE coastline_point_2.id = v_table_name.id;
	 */
	public static void main(String[] args) {
		new PreProcessNearestCoastlinePointHierarchicalAutomated().process();
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(PreProcessNearestCoastlinePointHierarchicalAutomated.class);
	private static final int BATCH_THRESHOLD = 200;
//	private static final Double EARTH_CIRCUMFERENCE_IN_STATUTE_MILES = 3963.1; // See http://www8.nau.edu/cvm/latlon_formula.html
/*	private static final Double MILES_PER_LATITUDE = 69.0;
	private static final Double MILES_PER_LONGITUDE = 53.0;
	private Double BOUNDING_BOX_MILES_PER_SIDE = 8.0;*/
	// Expand the search area so that it will encompass any box that encompasses our target point, and add a little slush
	private Double BOUNDING_BOX_OFFSET = 0.5; // about 8 miles (69 * 0.125)
//	private Double BOUNDING_BOX_OFFSET_LAT = (1 / (MILES_PER_LATITUDE / BOUNDING_BOX_MILES_PER_SIDE)) + 0.1;
//	private Double BOUNDING_BOX_OFFSET_LON = (1 / (MILES_PER_LONGITUDE / BOUNDING_BOX_MILES_PER_SIDE)) + 0.1;
	private static final int TOTAL_GRID_POINTS = 16985;
	private int GRID_POINTS_UPDATED = 0;
	
	private final Long INCREMENT = 200L;
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private CoastlinePointDao coastlinePointDao;
	@Autowired
	private GridPointDao gridPointDao;
	
//	private String previousGridPointLevel = "256";
//	private String targetGridPointLevel = "256";
	
	private Integer kbsClientId = 1;
	private Integer currentClientId = 2;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	
	public PreProcessNearestCoastlinePointHierarchicalAutomated() {
		/*SpringContextHolder.context = new ClassPathXmlApplicationContext("servlet-context.xml");
		dataSource = (DataSource)SpringContextHolder.context.getBean(DataSource.class);
		coastlinePointDao = (CoastlinePointDao)SpringContextHolder.context.getBean(CoastlinePointDao.class);
		gridPointDao = (GridPointDao)SpringContextHolder.context.getBean(GridPointDao.class);*/
	}
	
	@RequestMapping("/preprocessHierarchical")
	public void process() {
		if(process256Target()) {
			BOUNDING_BOX_OFFSET = 2.2;
			if(processHierarchicalTarget("256", "128")) {
				BOUNDING_BOX_OFFSET = 2.0;
				if(processHierarchicalTarget("128", "64")) {
					BOUNDING_BOX_OFFSET = 1.0;
					if(processHierarchicalTarget("64", "32")) {
						BOUNDING_BOX_OFFSET = 0.6;
						if(processHierarchicalTarget("32", "16")) {
							BOUNDING_BOX_OFFSET = 0.4;
							if(processHierarchicalTarget("16", "8")) {
								moveGridPointsToFinalRestingSpot("8");
								BOUNDING_BOX_OFFSET = 0.3;
								//processFinalGridPoints("8");
							}
						} else LOG.error("Processing grid_point_16 returned error!");
					} else LOG.error("Processing grid_point_32 returned error!");
				} else LOG.error("Processing grid_point_64 returned error!");
			}
		} else LOG.error("Processing grid_point_128 returned error!");
	
		
		LOG.info("Finished with the whole potato.");
		
	}
	
	private void addIntermediaryGridPoint(String targetGridPointLevel, GridPoint gridPoint) {
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Updating grid_point_" + targetGridPointLevel + "...");
			updateIntermediaryGridPoints(targetGridPointLevel, gridPointsToUpdate);
		}
	}
	
	private void addFinalGridPoint(GridPoint gridPoint) {
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting...");
			updateFinalGridPoints(gridPointsToUpdate);
		}
	}
	
	/**
	 * Brute-forces closest coastline point IDs for all grid points in grid_point_256
	 */
	private Boolean process256Target() {
		String previousGridPointLevel = "256";
		String  targetGridPointLevel = "256";
		
		Integer lastGridPointId = 0;
		List<GridPoint> gridPoints;
		do {
			gridPoints = getNextGridPoints(previousGridPointLevel, lastGridPointId);
			for(GridPoint gridPoint: gridPoints) {
				new BruteForceDistanceToCoastCalculator(gridPoint).run();
			}
			if(!gridPoints.isEmpty()) lastGridPointId = gridPoints.get(gridPoints.size() - 1).getId();
		} while(gridPoints.size() > 0);
		
		if(!gridPointsToUpdate.isEmpty()) {
			updateIntermediaryGridPoints(targetGridPointLevel, gridPointsToUpdate);
		}
		
		gridPoints = getNextGridPoints(previousGridPointLevel, 0);
		if(!gridPoints.isEmpty()) {
			LOG.error("There are still " + gridPoints.size() + " null coastline IDs in grid_point_" + targetGridPointLevel + "! Rerunning.");
			process256Target();
		}
		LOG.info("Done processing closest coastline point IDs for grid_point_" + targetGridPointLevel);
		return Boolean.TRUE;
	}
	
	/**
	 * Uses the coastline point IDs already calculated in grid_point_128 to form
	 * coastline point min and max boundaries so we can calculate a smaller subsection
	 * of all possible coastline points
	 * @return
	 */
	private Boolean processHierarchicalTarget(String previousGridPointLevel, String targetGridPointLevel) {		
		Integer lastGridPointId = 0;
		long x = 0;
		List<GridPoint> gridPoints;
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		do {
			gridPoints = getNextGridPoints(targetGridPointLevel, lastGridPointId);
			for(GridPoint gridPoint: gridPoints) {
				executorService.execute(new DistanceToCoastHierarchicalCalculator(gridPoint, previousGridPointLevel, targetGridPointLevel));
			}
			
			if(!gridPoints.isEmpty()) lastGridPointId = gridPoints.get(gridPoints.size() - 1).getId();
		} while(!gridPoints.isEmpty());
		
		/*gridPoints = getNextGridPoints(targetGridPointLevel, lastGridPointId);
		new DistanceToCoastHierarchicalCalculator(gridPoints.get(0), previousGridPointLevel, targetGridPointLevel).run();
		lastGridPointId = gridPoints.get(gridPoints.size() - 1).getId();
		*/
		
		
		LOG.info("Queued " + x + " threads for grid_point_" + targetGridPointLevel + ", waiting for executor service to shut down...");
		try {
			executorService.shutdown();
			// Block until all grid points have been processed
			executorService.awaitTermination(20, TimeUnit.DAYS);
			if(!gridPointsToUpdate.isEmpty()) {
				updateIntermediaryGridPoints(targetGridPointLevel, gridPointsToUpdate);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		
		gridPoints = getNextGridPoints(previousGridPointLevel, 0);
		if(!gridPoints.isEmpty()) {
			LOG.error("There are still " + gridPoints.size() + " null coastline IDs in grid_point_" + targetGridPointLevel + "! Rerunning.");
			processHierarchicalTarget(previousGridPointLevel, targetGridPointLevel);
		}
		
		LOG.info("Done processing closest coastline point IDs for grid_point_" + targetGridPointLevel);
		return Boolean.TRUE;
	}
	
	/**
	 * Copies most granular processed grid point data to into grid_point_client_coastline_point table
	 * @param previousGridPointLevel
	 * @return
	 */
	private final String MOVE_FINAL_POINTS = "INSERT INTO grid_point_client_coastline_point(client_id, grid_point_id, coastline_point_id, distance_in_miles) SELECT %s, id, coastline_point_id, distance_in_miles FROM grid_point_%s gp8 order by id;";
	private Boolean moveGridPointsToFinalRestingSpot(String previousGridPointLevel) {
		String sql = String.format(MOVE_FINAL_POINTS, currentClientId, previousGridPointLevel);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute(sql);
		LOG.info("Done processing closest coastline point IDs for final grid_point");
		return Boolean.TRUE;
	}
	
	/**
	 * Final culmination of all the intermediate processing. Updates main grid_point and related tables.
	 * @param previousGridPointLevel
	 * @return
	 */
	private Boolean processFinalGridPoints(String previousGridPointLevel) {		
		Long lastGridPointId = 0L;
		
		long x = 0;
		List<GridPoint> gridPoints;
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		do {
			gridPoints = gridPointDao.getPointsBetween(currentClientId, lastGridPointId + 1, lastGridPointId + INCREMENT);
			for(GridPoint gridPoint: gridPoints) {
				executorService.execute(new DistanceToCoastFinalCalculator(gridPoint, previousGridPointLevel));
				x++;
			}
			lastGridPointId += INCREMENT;
		} while(gridPoints.size() > 0);
		
		
		LOG.info("Queued " + x + " threads for final grid_point, waiting for executor service to shut down...");
		try {
			executorService.shutdown();
			// Block until all grid points have been processed
			executorService.awaitTermination(20, TimeUnit.DAYS);
			if(!gridPointsToUpdate.isEmpty()) {
				updateFinalGridPoints(gridPointsToUpdate);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		LOG.info("Done processing closest coastline point IDs for final grid_point");
		return Boolean.TRUE;
	}
	
	private Double getMilesBetween(GridPoint targetPoint, CoastlinePoint coastlinePoint) {
		DistanceCalculator distanceCalculator = new DefaultDistanceCalculatorImpl(targetPoint, coastlinePoint);
		Double distance = distanceCalculator.calculate().getDistanceInMiles();
		return distance;
	}
	
	/***** DAO STUFF *****/
	String GET_NEXT_SQL = "SELECT gp.id, gp.lat, gp.lon, coastline_point_id, distance_in_miles FROM grid_point_%s gp WHERE id > ? AND coastline_point_id is null order by id LIMIT " + INCREMENT;
	private List<GridPoint> getNextGridPoints(String gridPointLevel, Integer lastId) {
		String sql = String.format(GET_NEXT_SQL, gridPointLevel);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<GridPoint> gridPoints = jdbcTemplate.query(sql, new Object[]{ lastId }, new ClientGridPointRowMapper());
		return gridPoints;
	}
	
	String GET_BETWEEN_SQL = "SELECT gp.id, gp.lat, gp.lon, coastline_point_id, distance_in_miles FROM grid_point_%s gp WHERE coastline_point_id is null and id BETWEEN ? AND ?";
	private List<GridPoint> getGridPointsBetween(String gridPointLevel, Long startId, Long endId) {
		String sql = String.format(GET_BETWEEN_SQL, gridPointLevel);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<GridPoint> gridPoints = jdbcTemplate.query(sql, new Object[]{ startId, endId }, new ClientGridPointRowMapper());
		return gridPoints;
	}
	
	private MinMaxCoastlinePointSortOrder getBoundingCoastlinePointSortOrders(String gridPointLevel, Integer clientId, GridPoint coordinate) {
		String GET_COASTLINE_POINT_SORT_ORDERS_SURROUNDING_SQL = "SELECT MIN(cp.sort_order) AS min_sort_order, MAX(cp.sort_order) AS max_sort_order FROM grid_point_%s gp JOIN coastline_point_%d cp ON gp.coastline_point_id=cp.id WHERE gp.lat BETWEEN (? - " + BOUNDING_BOX_OFFSET + ") AND (? + " + BOUNDING_BOX_OFFSET + ") AND gp.lon BETWEEN (? - " + BOUNDING_BOX_OFFSET + ") AND (? + " + BOUNDING_BOX_OFFSET + ")";
		String sql = String.format(GET_COASTLINE_POINT_SORT_ORDERS_SURROUNDING_SQL, gridPointLevel, clientId);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		MinMaxCoastlinePointSortOrder boundingSortOrders = jdbcTemplate.queryForObject(sql, new Number[]{ coordinate.getLat(), coordinate.getLat(), coordinate.getLng(), coordinate.getLng() }, new MinMaxCoastlinePointSortOrderRowMapper());
		return boundingSortOrders;
	}
	
	private void updateIntermediaryGridPoints(String targetGridPointLevel, List<GridPoint> points) {
		List<GridPoint> newPoints = new ArrayList<GridPoint>(points);
		points.clear();
		List<Object[]> params = new ArrayList<Object[]>();
		int x;
		for(x = 0; x < newPoints.size(); x++) {
			GridPoint point = newPoints.get(x);
			System.out.println("Processing point " + point.getId());
			params.add(new Object [] { point.getDistanceInMiles(), point.getClosestCoastlinePointId(), point.getId() });
		}
		
		String sql = String.format("UPDATE grid_point_%s SET distance_in_miles=?, coastline_point_id=? WHERE id=?", targetGridPointLevel);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.batchUpdate(sql, params);
		
		GRID_POINTS_UPDATED += newPoints.size();
		printStatus();
	}
	
	private void updateFinalGridPoints(List<GridPoint> points) {
		gridPointDao.update(gridPointsToUpdate);
		GRID_POINTS_UPDATED += points.size();
		gridPointsToUpdate.clear();
		printStatus();
	}
	
	private void printStatus() {
		LOG.info("{} of {} grid points updated ({}%)", new Number[] { GRID_POINTS_UPDATED, TOTAL_GRID_POINTS, (GRID_POINTS_UPDATED / TOTAL_GRID_POINTS) * 100.0 });
	}
	
	class BruteForceDistanceToCoastCalculator implements Runnable {
		private GridPoint gridPoint;
		public BruteForceDistanceToCoastCalculator(GridPoint gridPoint) {
			this.gridPoint = gridPoint;
			LOG.info("Thread created for " + gridPoint.getLat() + ", " + gridPoint.getLng());
		}
		
		@Override
		public void run() {
			CoastlinePoint currentWinner = null;
			Double minDistance = 999999999.0;
			List<CoastlinePoint> coastlinePoints = coastlinePointDao.getAll(currentClientId);
			for(CoastlinePoint coastlinePoint: coastlinePoints) {
				// Use Great Circle formula to calculate minimum distance to coast from the current grid point
				Double milesBetween = getMilesBetween(gridPoint, coastlinePoint);
				if(milesBetween < minDistance) {
					minDistance = milesBetween;
					currentWinner = coastlinePoint;
				}
			}
			
			gridPoint.setClosestCoastlinePointId(currentWinner.getId());
			gridPoint.setDistanceInMiles(minDistance);
			gridPoint.setClientId(currentClientId);
			addIntermediaryGridPoint("256", gridPoint);
			LOG.info("Done with grid_point_256 grid point {}", gridPoint);
		}
	}
	
	class DistanceToCoastHierarchicalCalculator implements Runnable {
		
		private GridPoint targetPoint;
		private String sourceGridPointLevel;
		private String targetGridPointLevel;

		public DistanceToCoastHierarchicalCalculator(GridPoint targetPoint, String sourceGridPointLevel, String targetGridPointLevel) {
			this.targetPoint = targetPoint;
			this.sourceGridPointLevel = sourceGridPointLevel;
			this.targetGridPointLevel = targetGridPointLevel;
		}
		
		@Override
		public void run() {
			MinMaxCoastlinePointSortOrder boundingCoastlinePointSortOrders = getBoundingCoastlinePointSortOrders(sourceGridPointLevel, currentClientId, targetPoint);
			if(null == boundingCoastlinePointSortOrders || boundingCoastlinePointSortOrders.getMax().intValue() == 0) {
				LOG.error("Lat/lng " + targetPoint + " is out of bounds"); //throw new InvalidLatLngException("Lat/lng is out of bounds");
				targetPoint.setClientId(currentClientId);
				addIntermediaryGridPoint(targetGridPointLevel, targetPoint);
				return;
			}
			
			// Obtain the coastline points between the min and the max sort orders
			List<CoastlinePoint> coastlinePoints = coastlinePointDao.getPointsBetweenSortOrders(currentClientId, boundingCoastlinePointSortOrders.getMin(), boundingCoastlinePointSortOrders.getMax());
			if(null == coastlinePoints || coastlinePoints.isEmpty()) {
//				throw new InvalidLatLngException("Lat/lng is unbounded by known coastline");
				LOG.error("Lat/lng " + targetPoint + " is not contained by a grid square with pre-calculated nearest coastline points");
				targetPoint.setClientId(currentClientId);
				addIntermediaryGridPoint(targetGridPointLevel, targetPoint);
				return;
			}
			
			// Now calculate the distance to coast from the target grid point to each coastline point. The minimum distance is the final Distance to Coast.
			CoastlinePoint closestCoastlinePoint = null;
			Double minimumMiles = 9999.0;
			for(CoastlinePoint coastlinePoint: coastlinePoints) {
				Double milesBetween = getMilesBetween(targetPoint, coastlinePoint);
				if(milesBetween < minimumMiles) {
					LOG.trace(String.format("Coastline point %s with sort order %f is the new winner at %f miles from target", coastlinePoint, coastlinePoint.getSortOrder(), milesBetween));
					minimumMiles = milesBetween;
					closestCoastlinePoint = coastlinePoint;
				}
			}
			
			targetPoint.setClosestCoastlinePointId(closestCoastlinePoint.getId());
			targetPoint.setDistanceInMiles(minimumMiles);
			targetPoint.setClientId(currentClientId);
			addIntermediaryGridPoint(targetGridPointLevel, targetPoint);
			LOG.info("Done with grid_point_{} grid point {}", targetGridPointLevel, targetPoint);
		}
		
	}
	
	/**
	 * Updates the production grid point table and associated relations
	 * @author Steve
	 *
	 */
	class DistanceToCoastFinalCalculator implements Runnable {
		
		private GridPoint targetPoint;
		private String sourceGridPointLevel;

		public DistanceToCoastFinalCalculator(GridPoint targetPoint, String sourceGridPointLevel) {
			this.targetPoint = targetPoint;
			this.sourceGridPointLevel = sourceGridPointLevel;
		}
		
		@Override
		public void run() {
			MinMaxCoastlinePointSortOrder boundingCoastlinePointSortOrders = getBoundingCoastlinePointSortOrders(sourceGridPointLevel, currentClientId, targetPoint);
			if(null == boundingCoastlinePointSortOrders || boundingCoastlinePointSortOrders.getMax().intValue() == 0) {
				LOG.error("Lat/lng " + targetPoint + " is out of bounds"); //throw new InvalidLatLngException("Lat/lng is out of bounds");
				targetPoint.setClientId(currentClientId);
				addFinalGridPoint(targetPoint);
				return;
			}
			
			// Obtain the coastline points between the min and the max sort orders
			List<CoastlinePoint> coastlinePoints = coastlinePointDao.getPointsBetweenSortOrders(currentClientId, boundingCoastlinePointSortOrders.getMin(), boundingCoastlinePointSortOrders.getMax());
			if(null == coastlinePoints || coastlinePoints.isEmpty()) {
				LOG.error("Lat/lng " + targetPoint + " is not contained by a grid square with pre-calculated nearest coastline points");
				targetPoint.setClientId(currentClientId);
				addFinalGridPoint(targetPoint);
				return;
			}
			
			// Now calculate the distance to coast from the target grid point to each coastline point. The minimum distance is the final Distance to Coast.
			CoastlinePoint closestCoastlinePoint = null;
			Double minimumMiles = 9999.0;
			for(CoastlinePoint coastlinePoint: coastlinePoints) {
				Double milesBetween = getMilesBetween(targetPoint, coastlinePoint);
				if(milesBetween < minimumMiles) {
					LOG.trace(String.format("Coastline point %s with sort order %f is the new winner at %f miles from target", coastlinePoint, coastlinePoint.getSortOrder(), milesBetween));
					minimumMiles = milesBetween;
					closestCoastlinePoint = coastlinePoint;
				}
			}
			
			targetPoint.setClosestCoastlinePointId(closestCoastlinePoint.getId());
			targetPoint.setDistanceInMiles(minimumMiles);
			targetPoint.setClientId(currentClientId);
			addFinalGridPoint(targetPoint);
			LOG.info("Done with final grid_point grid point {}", targetPoint);
		}
		
	}
}
