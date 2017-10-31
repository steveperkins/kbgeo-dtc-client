package com.kbs.geo.coastal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.kbs.geo.coastal.model.DistanceGridPoint;
import com.kbs.geo.coastal.service.impl.DistanceGridServiceImpl;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main().convertCoastalDataset();
	}
	
	private static final Logger LOG = Logger.getLogger(DistanceGridServiceImpl.class);
//	private static final String DATA_FILE = "C:/Users/Steve/Downloads/netcdf-3.6.1-beta1-win32dll/dist2coast.txt";
	private static final String DATA_FILE = "C:/Users/Steve/Downloads/netcdf-3.6.1-beta1-win32dll/us.txt";
//	private static final String DATA_FILE = "/home/bitnami/dist2coast/dist2coast.txt";
	// Record only lat/lon within the continental US
	private static final Double MAX_LATITUDE = 49.15;
	private static final Double MAX_LONGITUDE = -70.31;
	private static final Double MIN_LATITUDE = 24.86;
	private static final Double MIN_LONGITUDE = -124.72;
	private static final int BATCH_THRESHOLD = 500;
	private static final String INSERT_GRID_POINT_SQL = "INSERT INTO distance_grid (lat, lon, distance_miles) VALUES(?, ?, ?)";

	private DataSource datasource;
	
	List<DistanceGridPoint> gridPoints = Collections.synchronizedList(new ArrayList<DistanceGridPoint>());
	
	public Main() {
		SpringContextHolder.context = new ClassPathXmlApplicationContext("servlet-context.xml");
		datasource = (DataSource)SpringContextHolder.context.getBean("dataSource");
	}
	
	public void convertCoastalDataset() {
		long x = 0;
		String line = null;
		BufferedReader reader = null;
		
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(1);
			
			reader = new BufferedReader(new FileReader(new File(DATA_FILE)));
			while( (line = reader.readLine()) != null ) {
				executorService.execute(new LatLonLineEvaluator(x, line));
				x++;
				
				/*if(x % 500 == 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/
			}
			LOG.info("Queued " + x + " threads, waiting for executor service to shut down...");
			try {
				executorService.awaitTermination(20, TimeUnit.DAYS);
				if(!gridPoints.isEmpty()) {
					createGridPoints(gridPoints);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOG.info("Done.");
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("Could not close reader", e);
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				LOG.error("Could not close reader", e1);
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				LOG.error("Could not close reader", e1);
			}
		}
	}
	
	private void addGridPoint(DistanceGridPoint gridPoint) {
		gridPoints.add(gridPoint);
		if(gridPoints.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting...");
			createGridPoints(gridPoints);
			gridPoints.clear();
		}
	}
	
	private void createGridPoints(List<DistanceGridPoint> gridPoints) {
		List<Object[]> params = new ArrayList<Object[]>();
		for(DistanceGridPoint gridPoint: gridPoints) {
			params.add(new Double [] { gridPoint.getLat(), gridPoint.getLon(), gridPoint.getDistanceMiles() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(INSERT_GRID_POINT_SQL, params);
	}
	
	class LatLonLineEvaluator implements Runnable {
		private long index;
		private String line;

		public LatLonLineEvaluator(long index, String line) {
			this.index = index;
			this.line = line;
		}
		
		@Override
		public void run() {
			/*
			 * File format is
			 * lat\tlon\tdistance_in_km
			 * where \t is a tab character
			 */
			int previousTabPosition = line.indexOf("\t");
			String lon = line.substring(0, previousTabPosition);
			
			int nextTabPosition = line.indexOf("\t", previousTabPosition + 1);
			String lat = line.substring(previousTabPosition + 1, nextTabPosition);
			
			Double dblLon = Double.parseDouble(lon);
			if(dblLon > MIN_LONGITUDE && dblLon < MAX_LONGITUDE) {
				Double dblLat = Double.parseDouble(lat);
				if(dblLat > MIN_LATITUDE && dblLat < MAX_LATITUDE) {
					LOG.info("FOUND ONE: Keeping " + index + ": " + lat + ", " + lon);
					
					String distance = line.substring(nextTabPosition + 1);
					Double dblDistance = Double.parseDouble(distance);
					addGridPoint(new DistanceGridPoint(dblLat, dblLon, dblDistance, null));
				}
			} else {
				LOG.info("Skipped #" + index + ": " + lat + ", " + lon);
			}
		}
		
	}

}
