package com.kbs.geo.coastal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.preprocess.GridPointCreationService;
import com.kbs.geo.preprocess.PreProcessFireDeptService;

//@RestController("admin")
public class CreateUSGridPointsInitialController {
	private static final Logger LOG = Logger.getLogger(CreateUSGridPointsInitialController.class);
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
	
	private Double INCREMENT = 1.0;
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private GridPointCreationService gridPointService;
	
	@Autowired
	private PreProcessFireDeptService preprocessService;
	
	List<GridPoint> gridPoints = Collections.synchronizedList(new ArrayList<GridPoint>());
	Long pointCount = 0L;
	Long recordCount = 0L;
	
/*	public CreateUSGridPointsInitialController() {
		SpringContextHolder.context = new ClassPathXmlApplicationContext("classpath:servlet-context.xml");
		datasource = (DataSource)SpringContextHolder.context.getBean("dataSource");
	}*/
	private String INSERT_GRID_POINT_SQL = "";
	
//	@RequestMapping(value="createGridPointsFire", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void createGridPointsFire() {
		//gridPointService.createInitialGridPoints("fire", 4.830188679245283); // 256-mile increment
//		gridPointService.createInitialGridPoints("fire", 1/0.26953125); // 128-mile increment
//		preprocessService.process("fire", "fire"); // Because this is the first generation of grid points, the read and write tables are the same
//		preprocessService.createIntermediaryGridPoints("fire", 10.0, 1/0.5390625); // 128-mile increment
//		preprocessService.createIntermediaryGridPoints("fire", 8.0, 1/1.078125); // 64-mile increment
//		preprocessService.createIntermediaryGridPoints("fire", 4.0, 1/2.15625); // 32-mile increment
//		preprocessService.createIntermediaryGridPoints("fire", 2.0, 1/4.3125); // 16-mile increment
//		preprocessService.createIntermediaryGridPoints("fire", 1.0, 1/8.265); // 8-mile increment
//		preprocessService.createIntermediaryGridPoints("fire", 0.6, 1/16.53); // 4-mile increment
//		preprocessService.createIntermediaryGridPoints("fire", 0.4, 1/33.06); // 2-mile increment (creates bounding boxes of 4 square miles)

		preprocessService.findRootFireDepartmentAssociations("fire", 15.0, 1/0.5390625, "1.855072463768116");
		preprocessService.findRootFireDepartmentAssociations("fire", 4.0, 1/1.078125, "0.927536231884058");
		preprocessService.findRootFireDepartmentAssociations("fire", 2.0, 1/0.26953125, "0.463768115942029");
		preprocessService.findRootFireDepartmentAssociations("fire", 2.0, 1/2.15625, "0.2318840579710145");
		preprocessService.findRootFireDepartmentAssociations("fire", 2.0, 1/8.265, "0.12099213551119177");
		preprocessService.findRootFireDepartmentAssociations("fire", 1.0, 1/16.53, "0.060496067755595885");
		preprocessService.findRootFireDepartmentAssociations("fire", 1.0, 1/33.06, "0.0302480338777979");
		
	}
	
//	@RequestMapping(value="createGridPointsInitial/{table}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void createGridPoints(@PathVariable("table") Integer table, @RequestParam("increment") Double increment) {
		INCREMENT = increment;
		INSERT_GRID_POINT_SQL = String.format("INSERT INTO grid_point_%d (lat, lon) VALUES(?, ?)", table);
		
		Double currentLat = STARTING_LAT;
		Double currentLon = STARTING_LON;
		
		while(true) {
			while(currentLat >= MIN_LATITUDE && currentLat <= MAX_LATITUDE) {
				LOG.info("Testing [" + currentLat + ", " + currentLon + "]");
				while(currentLon >= MIN_LONGITUDE && currentLon <= MAX_LONGITUDE) {
					LOG.info("(point=" + pointCount + ") Coord is in range");
					addGridPoint(new GridPoint(currentLat, currentLon, null));
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
		return currentLat + INCREMENT;
	}
	private Double incrementLon(Double currentLon) {
		return currentLon + INCREMENT;
	}
	
	private void addGridPoint(GridPoint gridPoint) {
		LOG.info("Adding grid point [" + gridPoint.getLat() + ", " + gridPoint.getLng() + "]");
		gridPoints.add(gridPoint);
		if(gridPoints.size() >= BATCH_THRESHOLD) {
			LOG.info("Inserting records " + recordCount + "-" + (recordCount + gridPoints.size()));
			createGridPoints(gridPoints);
			recordCount += gridPoints.size();
			gridPoints.clear();
		}
	}
	
	private void createGridPoints(List<GridPoint> gridPoints) {
		LOG.info("Inserting " + gridPoints.size() + " gridPointsToUpdate");
		List<Object[]> params = new ArrayList<Object[]>();
		for(GridPoint gridPoint: gridPoints) {
			params.add(new BigDecimal[] { gridPoint.getLat(), gridPoint.getLng() });
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.batchUpdate(INSERT_GRID_POINT_SQL, params);
	}
	
}