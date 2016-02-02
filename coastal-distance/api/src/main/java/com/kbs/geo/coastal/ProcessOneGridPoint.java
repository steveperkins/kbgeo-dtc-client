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
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.CoastlinePointService;

//@RestController("admin")
@RestController
public class ProcessOneGridPoint {

	public static void main(String[] args) {
		new ProcessOneGridPoint().process();
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(ProcessOneGridPoint.class);
	private static final Double MILES_PER_DEGREE = 57.295780; // was 60
	
	@Autowired
	private CoastlinePointService coastlinePointDao;
	@Autowired
	private GridPointDao gridPointDao;
	
	List<GridPoint> gridPointsToUpdate = Collections.synchronizedList(new ArrayList<GridPoint>());
	
//	@RequestMapping(value="preprocessOnePoint", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void process() {
		GridPoint gridPoint = gridPointDao.get(17764);
		
		// Get all coastline points
		List<CoastlinePoint> coastlinePoints = coastlinePointDao.getAll(1);
		LOG.info("Retrieved {} coastline points", coastlinePoints.size());
		
		CoastlinePoint currentWinner = null;
		Double minDistance = 9999.0;
		Long y = 0L;
		for(CoastlinePoint coastlinePoint: coastlinePoints) {
			Double miles = coastlinePointDao.getMilesBetween(new LatLng(gridPoint.getLat(), gridPoint.getLng()), coastlinePoint);
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
		
		LOG.info("Closest point to " + gridPoint.getLat() + ", " + gridPoint.getLng() + " is " + currentWinner.getId() + " at " + currentWinner.getLat() + ", " + currentWinner.getLng() + " " + minDistance + " miles away");
		gridPointDao.save(gridPoint);
		
		LOG.info("Done.");
		
	}
	
/*	private Double getMilesBetween(Double gridLatitude, Double gridLongitude, Double coastalLatitude, Double coastalLongitude) {
        Double gridLat = Math.toRadians(gridLatitude);
        Double gridLon = Math.toRadians(gridLongitude);
        Double coastalLat = Math.toRadians(coastalLatitude);
        Double coastalLon = Math.toRadians(coastalLongitude);

       *//*************************************************************************
        * Compute using law of cosines
        *************************************************************************//*
        // great circle distance in radians
        Double angle1 = Math.acos(Math.sin(gridLat) * Math.sin(coastalLat)
                      + Math.cos(gridLat) * Math.cos(coastalLat) * Math.cos(gridLon - coastalLon));

        // convert back to degrees
        angle1 = Math.toDegrees(angle1);

        // each degree on a great circle of Earth is 60 nautical miles
//        Double distance1 = 60 * angle1;


       *//*************************************************************************
        * Compute using Haversine formula
        *************************************************************************//*
        Double a = Math.pow(Math.sin((coastalLat-gridLat)/2), 2)
                 + Math.cos(gridLat) * Math.cos(coastalLat) * Math.pow(Math.sin((coastalLon-gridLon)/2), 2);

        // great circle distance in radians. UsesMath.min to prevent rounding error as distances approach 12,000 km (http://www.movable-type.co.uk/scripts/gis-faq-5.1.html) 
        Double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        // convert back to degrees
        angle2 = Math.toDegrees(angle2);

        // each degree on a great circle of Earth is 60 nautical miles
        Double distanceInMiles = MILES_PER_DEGREE * angle2;

        return distanceInMiles;
    }*/
	
}
