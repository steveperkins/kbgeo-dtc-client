package com.kbs.geo.preprocess.impl;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.firedept.dao.FireDeptDao;
import com.kbs.geo.firedept.dao.FireDeptGridPointDao;
import com.kbs.geo.firedept.dao.mapper.FireDepartmentRowMapper;
import com.kbs.geo.firedept.dao.mapper.FireGridPointRowMapper;
import com.kbs.geo.firedept.model.FireDepartment;
import com.kbs.geo.firedept.model.FireGridPoint;
import com.kbs.geo.math.DistanceCalculatorUtil;
import com.kbs.geo.preprocess.PreProcessFireDeptService;

/**
 * Runs through ALL grid points in a table, then through ALL available fire department points to find the closest to each grid point. Updates the source table with
 * a reference to the closest fire department.
 * @author Steve
 *
 */
@Service
public class PreProcessFireDeptServiceImpl implements PreProcessFireDeptService {

	private static final Logger LOG = Logger.getLogger(PreProcessFireDeptServiceImpl.class);
	private static final int BATCH_THRESHOLD = 30;
	private static final Double MAX_LATITUDE = 50.0;
	private static final Double MIN_LATITUDE = 25.0;
	
	private Integer BATCH_SIZE = 200;
	private Double LAT_LNG_INCREMENT;
	private Double BOUNDING_BOX_OFFSET;
	
	@Autowired
	private FireDeptDao fireDeptDao;
	
	@Autowired
	private FireDeptGridPointDao gridPointDao;
	
	@Autowired
	private DataSource datasource;
	
	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<FireGridPoint> gridPointRowMapper = new FireGridPointRowMapper();
	private RowMapper<FireDepartment> fireDepartmentRowMapper = new FireDepartmentRowMapper();
	
	private List<FireGridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<FireGridPoint>());
	private List<FireDepartment> fireDeptPoints;
	
	@PostConstruct
	public void postConstruct() {
		jdbcTemplate = new JdbcTemplate(datasource);
	}
	
	/* (non-Javadoc)
	 * @see com.kbs.geo.preprocess.PreProcessFireDeptService#process(java.lang.String)
	 */
	@Override
	public void process(String readGridPointTableSuffix, String writeGridPointTableSuffix) {
		gridPointDao.setReadTableNameSuffix(readGridPointTableSuffix);
		gridPointDao.setWriteTableNameSuffix(writeGridPointTableSuffix);
		
		long x = 0;
		int offset = BATCH_SIZE;
		fireDeptPoints = fireDeptDao.getAll();
		List<FireGridPoint> gridPoints = gridPointDao.getAll();
		int masterListLength = gridPoints.size() - 1;
		// Split up the list of grid points into smaller lists
		if(gridPoints.size() < BATCH_SIZE) {
			BATCH_SIZE = gridPoints.size() - 1;
		}
		List<FireGridPoint> rollingGridPoints = gridPoints.subList(0, BATCH_SIZE);

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		do {
			if(null == rollingGridPoints || rollingGridPoints.isEmpty()) break;
			
			executorService.execute(new DistanceToFireDeptCalculator(rollingGridPoints));
			x++;

			LOG.info("Retrieving grid points " + offset + " to " + (offset + BATCH_SIZE));
			
			if((offset + BATCH_SIZE) > gridPoints.size()) {
				offset = masterListLength;
				rollingGridPoints = gridPoints.subList(offset, offset);
			} else {
				rollingGridPoints = gridPoints.subList(offset, BATCH_SIZE);
				offset += BATCH_SIZE;
			}
			
			if(null == rollingGridPoints || rollingGridPoints.isEmpty()) break;
			LOG.info("LOOPING");
		} while(offset <= masterListLength);
		
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
	
	private String resolution;
	
	@Override
	public void createIntermediaryGridPoints(String tableSuffix, Double boundingBoxSize, Double latLngIncrement) {
		LAT_LNG_INCREMENT = latLngIncrement;
		BOUNDING_BOX_OFFSET = boundingBoxSize;
		
		int lastGridPointId = -1;
		List<FireGridPoint> gridPoints = Collections.synchronizedList(new ArrayList<FireGridPoint>());
//		gridPoints = getNextProcessedGridPoints(tableSuffix, lastGridPointId);
//		
//		while(!gridPoints.isEmpty()) {
//			// Find all existing grid points
//			// For each grid point, create a new grid point halfway between it and the next closest grid point. We can cheat here by forcing the caller to tell us where the midpoint should be. This value should get smaller and smaller the further we go in this process.
//			for(int x = 0; x < gridPoints.size(); x++) {
//				FireGridPoint currentPoint = gridPoints.get(x);
//				// Create a new intermediary grid point at the same horizontal axis as the current grid point but vertically 1/2 the distance to the next vertical grid point
//				FireGridPoint newVerticalGridPoint = new FireGridPoint(currentPoint.getLat(), currentPoint.getLng().add(new BigDecimal(LAT_LNG_INCREMENT)), "" + latLngIncrement);
//				if(newVerticalGridPoint.getLat().doubleValue() >= MIN_LATITUDE && newVerticalGridPoint.getLng().doubleValue() <= MAX_LATITUDE) {
//					// Insert the new grid point without a closest fire dept ID. The null closest fire dept ID property signals that the record has not been processed.
//					addGridPoint(tableSuffix, newVerticalGridPoint);
//				}
//				
//				// Create a new intermediary grid point at the same vertical axis as the current grid point but horizontally 1/2 the distance to the next horizontal grid point
//				FireGridPoint newHorizontalGridPoint = new FireGridPoint(currentPoint.getLat().add(new BigDecimal(LAT_LNG_INCREMENT)), currentPoint.getLng(), "" + latLngIncrement);
//				if(newHorizontalGridPoint.getLat().doubleValue() >= MIN_LATITUDE && newHorizontalGridPoint.getLng().doubleValue() <= MAX_LATITUDE) {
//					addGridPoint(tableSuffix, newHorizontalGridPoint);
//				}
//				
//				// Create a final intermediary grid point at the horizontally 1/2 the distance to the next horizontal grid point and 1/2 the distance to the next vertical grid point
//				FireGridPoint newCornerGridPoint = new FireGridPoint(newHorizontalGridPoint.getLat(), newVerticalGridPoint.getLng(), "" + latLngIncrement);
//				if(newCornerGridPoint.getLat().doubleValue() >= MIN_LATITUDE && newHorizontalGridPoint.getLng().doubleValue() <= MAX_LATITUDE) {
//					addGridPoint(tableSuffix, newCornerGridPoint);
//				}
//				
//				lastGridPointId = currentPoint.getId();
//			}
//			// Get the next batch of grid points
//			gridPoints = getNextProcessedGridPoints(tableSuffix, lastGridPointId);
//		}
//		createGridPoints(tableSuffix, gridPoints);
//
//		lastGridPointId = -1;
		
		// THEN...
		// Get all the existing grid points that have yet to be processed (don't have a fire dept ID)
		gridPoints = getNextUnprocessedGridPoints(tableSuffix, lastGridPointId);
		while(!gridPoints.isEmpty()) {
			// For each grid point, locate the four closest grid points surrounding it using the provided boundingBoxSize to determine how far to seek
			for(int x = 0; x < gridPoints.size(); x++) {
				FireGridPoint targetPoint = gridPoints.get(x);
				List<FireDepartment> closestFireDepartments = getFireDepartmentsFromPointsSurrounding(tableSuffix, targetPoint);
				LOG.info("Found " + closestFireDepartments.size() + " fire departments");
				// Measure the distance between the target grid point and the fire departments closest to each of the grid points. Attach the winning ID to the target grid point and save.
				FireDepartment closestFireDepartment = findClosestPoint(targetPoint, closestFireDepartments);
				if(null != closestFireDepartment) {
					// Save the grid point. It's janky but findClosestPoint updates the grid point with the fire dept ID and distance in miles.
					updateIntermediaryGridPoint(tableSuffix, targetPoint);
				}
				lastGridPointId = targetPoint.getId();
			}
			gridPoints = getNextUnprocessedGridPoints(tableSuffix, lastGridPointId);
		}
		updateIntermediaryGridPoints(tableSuffix, gridPoints);
	}
	
	@Override
	public void findRootFireDepartmentAssociations(String tableSuffix, Double boundingBoxSize, Double latLngIncrement, String resolution) {
		BOUNDING_BOX_OFFSET = boundingBoxSize;
		this.resolution = resolution;
		
		int lastGridPointId = -1;
		List<FireGridPoint> gridPoints = Collections.synchronizedList(new ArrayList<FireGridPoint>());
		
		// THEN...
		// Get all the existing grid points that have yet to be processed (don't have a fire dept ID)
		gridPoints = getUnprocessedPointsByResolution(tableSuffix, lastGridPointId, resolution);
		while(!gridPoints.isEmpty()) {
			// For each grid point, spin through the fire departments that fit within a bounding box around the grid point and determine which is closest
			for(int x = 0; x < gridPoints.size(); x++) {
				FireGridPoint targetPoint = gridPoints.get(x);
				List<FireDepartment> closestFireDepartments = getFireDepartmentsWithinBoundary(tableSuffix, targetPoint);
				LOG.info("Found " + closestFireDepartments.size() + " fire departments");
				// Measure the distance between the target grid point and the fire departments closest to each of the grid points. Attach the winning ID to the target grid point and save.
				FireDepartment closestFireDepartment = findClosestPoint(targetPoint, closestFireDepartments);
				if(null != closestFireDepartment) {
					// Save the grid point. It's janky but findClosestPoint updates the grid point with the fire dept ID and distance in miles.
					updateIntermediaryGridPoint(tableSuffix, targetPoint);
				}
				lastGridPointId = targetPoint.getId();
			}
			gridPoints = getNextUnprocessedGridPoints(tableSuffix, lastGridPointId);
		}
		updateIntermediaryGridPoints(tableSuffix, gridPointsToUpdate);
	}
	
	private void addGridPoint(String tableSuffix, FireGridPoint gridPoint) {
		LOG.info("Adding grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + ", " + gridPoint.getResolution() + "]");
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			createGridPoints(tableSuffix, gridPointsToUpdate);
			gridPointsToUpdate.clear();
		}
	}
	
	private void updateGridPoint(FireGridPoint gridPoint) {
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting...");
			gridPointDao.update(new ArrayList<FireGridPoint>(gridPointsToUpdate));
			gridPointsToUpdate.clear();
		}
	}
	
	private String INSERT_GRID_POINT_SQL = "INSERT INTO grid_point_%s (lat, lon, resolution) VALUES(?, ?, ?)";
	private void createGridPoints(String tableSuffix, List<FireGridPoint> gridPoints) {
		LOG.info("Inserting " + gridPoints.size() + " gridPointsToUpdate");
		List<Object[]> params = new ArrayList<Object[]>();
		for(FireGridPoint gridPoint: gridPoints) {
			LOG.info("Inserting grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + ", " + gridPoint.getResolution() + "]");
			
			params.add(new Object[] { gridPoint.getLat(), gridPoint.getLng(), gridPoint.getResolution() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		try {
			jdbcTemplate.batchUpdate(String.format(INSERT_GRID_POINT_SQL, tableSuffix), params);
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
	
	private void updateIntermediaryGridPoint(String tableSuffix, FireGridPoint gridPoint) {
		LOG.info("Updating intermediary grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "]");
		gridPointsToUpdate.add(gridPoint);
		if(gridPointsToUpdate.size() >= BATCH_THRESHOLD) {
			LOG.info("Updating intermediary grid_point_" + tableSuffix + "...");
			updateIntermediaryGridPoints(tableSuffix, gridPointsToUpdate);
		}
	}
	
	private void updateIntermediaryGridPoints(String tableSuffix, List<FireGridPoint> points) {
		if(!points.isEmpty()) {
			List<FireGridPoint> newPoints = new ArrayList<FireGridPoint>(points);
			points.clear();
			List<Object[]> params = new ArrayList<Object[]>();
			int x;
			for(x = 0; x < newPoints.size(); x++) {
				FireGridPoint point = newPoints.get(x);
				System.out.println("Processing intermediary point " + point.getId());
				params.add(new Object [] { point.getDistanceInMiles(), point.getClosestFireDeptId(), point.getId() });
			}
			
			String sql = String.format("UPDATE grid_point_%s SET distance_in_miles=?, fire_dept_id=? WHERE id=?", tableSuffix);
			jdbcTemplate.batchUpdate(sql, params);
			LOG.info("Updated " + newPoints.size() + " intermediary grid points");
		}
	}
	
	private FireDepartment findClosestPoint(FireGridPoint targetGridPoint, List<FireDepartment> departments) {
		Double minDistance = 999999999.0;
		FireDepartment currentWinner = null;
		for(FireDepartment fireDept: departments) {
			// Use Great Circle formula to calculate minimum distance from the target point to any of the given departments
			Double milesBetween = DistanceCalculatorUtil.getMilesBetween(targetGridPoint, fireDept);
			if(milesBetween < minDistance) {
				minDistance = milesBetween;
				currentWinner = fireDept;
			}
		}
		if(null != currentWinner) {
			targetGridPoint.setDistanceInMiles(minDistance);
			targetGridPoint.setClosestFireDeptId(currentWinner.getId());
			LOG.info("Fire dept winner is " + targetGridPoint.getClosestFireDeptId() + " at " + targetGridPoint.getDistanceInMiles() + "mi");
		}
		return currentWinner;
	}
	
	/***** DAO STUFF *****/
	private String GET_NEXT_UNPROCESSED_SQL = "SELECT gp.id, gp.lat, gp.lon, fire_dept_id, distance_in_miles FROM grid_point_%s gp WHERE id > ? AND fire_dept_id is null order by id LIMIT " + BATCH_THRESHOLD;
	private List<FireGridPoint> getNextUnprocessedGridPoints(String tableNameSuffix, Integer lastId) {
		String sql = String.format(GET_NEXT_UNPROCESSED_SQL, tableNameSuffix);
		return jdbcTemplate.query(sql, new Object[]{ lastId }, gridPointRowMapper);
	}
	
	private String GET_NEXT_PROCESSED_SQL = "SELECT gp.id, gp.lat, gp.lon, fire_dept_id, distance_in_miles FROM grid_point_%s gp WHERE id > ? AND fire_dept_id is not null order by id LIMIT " + BATCH_THRESHOLD;
	private List<FireGridPoint> getNextProcessedGridPoints(String tableNameSuffix, Integer lastId) {
		String sql = String.format(GET_NEXT_PROCESSED_SQL, tableNameSuffix);
		return jdbcTemplate.query(sql, new Object[]{ lastId }, gridPointRowMapper);
	}
	
	private String GET_FIRE_DEPTS_FROM_SURROUNDING_POINTS_SQL = "SELECT DISTINCT fd.* FROM grid_point_%s gp INNER JOIN fire_dept fd ON gp.fire_dept_id=fd.id WHERE gp.lat BETWEEN (? - %f) AND (? + %f) AND gp.lon BETWEEN (? - %f) AND (? + %f)";
	private List<FireDepartment> getFireDepartmentsFromPointsSurrounding(String tableNameSuffix, FireGridPoint point) {
		LOG.info("Looking for fire departments near " + point.getLat() + ", " + point.getLng());
		String sql = String.format(GET_FIRE_DEPTS_FROM_SURROUNDING_POINTS_SQL, tableNameSuffix, BOUNDING_BOX_OFFSET, BOUNDING_BOX_OFFSET, BOUNDING_BOX_OFFSET, BOUNDING_BOX_OFFSET);
		List<FireDepartment> departments = jdbcTemplate.query(sql, new Double[]{ point.getLat().doubleValue(), point.getLat().doubleValue(), point.getLng().doubleValue(), point.getLng().doubleValue() }, fireDepartmentRowMapper);
		return departments;
	}
	
	private String GET_POINTS_BY_RESOLUTION_SQL = "SELECT gp.id, gp.lat, gp.lon, fire_dept_id, distance_in_miles FROM grid_point_%s gp WHERE id > ? AND resolution = ? AND fire_dept_id is null order by id LIMIT " + BATCH_THRESHOLD;
	private List<FireGridPoint> getUnprocessedPointsByResolution(String tableNameSuffix, Integer lastId, String resolution) {
		String sql = String.format(GET_POINTS_BY_RESOLUTION_SQL, tableNameSuffix);
		return jdbcTemplate.query(sql, new Object[]{ lastId, resolution }, gridPointRowMapper);
	}
	
	private String GET_FIRE_DEPTS_WITHIN_BOUNDARY_SQL = "SELECT fd.* FROM fire_dept fd WHERE fd.lat BETWEEN (? - %f) AND (? + %f) AND fd.lon BETWEEN (? - %f) AND (? + %f)";
	private List<FireDepartment> getFireDepartmentsWithinBoundary(String tableNameSuffix, FireGridPoint point) {
		LOG.info("Looking for fire departments near " + point.getLat() + ", " + point.getLng());
		String sql = String.format(GET_FIRE_DEPTS_WITHIN_BOUNDARY_SQL, BOUNDING_BOX_OFFSET, BOUNDING_BOX_OFFSET, BOUNDING_BOX_OFFSET, BOUNDING_BOX_OFFSET);
		List<FireDepartment> departments = jdbcTemplate.query(sql, new Double[]{ point.getLat().doubleValue(), point.getLat().doubleValue(), point.getLng().doubleValue(), point.getLng().doubleValue() }, fireDepartmentRowMapper);
		return departments;
	}
	
	class DistanceToFireDeptCalculator implements Runnable {
		private List<FireGridPoint> gridPoints;
		public DistanceToFireDeptCalculator(List<FireGridPoint> gridPoints) {
			this.gridPoints = gridPoints;
			LOG.info("Thread created for " + gridPoints.size() + " points");
		}
		
		@Override
		public void run() {
			if(null == gridPoints) return;
			int x;
			for(x = 0; x < gridPoints.size(); x++) {
				FireGridPoint gridPoint = gridPoints.get(x);
				FireDepartment currentWinner = null;
				Double minDistance = 99999999.0;
				int y;
				for(y = 0; y < fireDeptPoints.size(); y++) {
					FireDepartment dept = fireDeptPoints.get(y);
					LOG.info("Calculating distance between grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "] and fire department at [" + dept.getLat() + ", " + dept.getLng() + "]");
					// Use Great Circle formula to calculate minimum distance to fire station from the current grid point
					Double milesBetween = DistanceCalculatorUtil.getMilesBetween(new LatLng(gridPoint.getLat(), gridPoint.getLng()), dept);
					if(milesBetween < minDistance) {
						minDistance = milesBetween;
						currentWinner = dept;
					}
				}
				
				gridPoint.setDistanceInMiles(minDistance);
				gridPoint.setClosestFireDeptId(currentWinner.getId());
				updateGridPoint(gridPoint);
			}
		}
	}

}
