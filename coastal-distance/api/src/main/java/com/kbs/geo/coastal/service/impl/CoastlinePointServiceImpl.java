package com.kbs.geo.coastal.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.dao.CoastlinePointDao;
import com.kbs.geo.coastal.dao.GridPointDao;
import com.kbs.geo.coastal.http.exception.InvalidLatLngException;
import com.kbs.geo.coastal.math.CloseDistanceCalculatorImpl;
import com.kbs.geo.coastal.math.DefaultDistanceCalculatorImpl;
import com.kbs.geo.coastal.math.DistanceCalculator;
import com.kbs.geo.coastal.math.DistanceCalculatorResult;
import com.kbs.geo.coastal.math.RoundingUtil;
import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.DistanceToCoastResult;
import com.kbs.geo.coastal.model.GridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.model.MinMaxCoastlinePointSortOrder;
import com.kbs.geo.coastal.service.CoastlinePointService;
import com.kbs.geo.http.security.KbApiAuthContext;

@Service
public class CoastlinePointServiceImpl implements CoastlinePointService {

//	private static final Double EARTH_CIRCUMFERENCE_IN_STATUTE_MILES = 3963.1; // See http://www8.nau.edu/cvm/latlon_formula.html
//	private static final Double EARTH_RADIUS_IN_MILES = 3959.87433;	
	private static final Logger LOG = Logger.getLogger(CoastlinePointServiceImpl.class);
	
	@Autowired
	private CoastlinePointDao coastlinePointDao;
	
	@Autowired
	private GridPointDao gridPointDao;
	
	@Autowired
	private KbApiAuthContext kbContext;

	@Override
	public Integer save(CoastlinePoint client) {
		return coastlinePointDao.save(client);
	}

	@Override
	public CoastlinePoint get(Integer id) {
		return coastlinePointDao.get(id);
	}

	@Override
	public List<CoastlinePoint> getAll(Integer clientId) {
		return coastlinePointDao.getAll(clientId);
	}
	
	@Override
	public List<CoastlinePoint> getBetween(Integer clientId, Double startOrder, Double endOrder) {
		return coastlinePointDao.getPointsBetween(clientId, startOrder, endOrder);
	}
	
	@Override
	public DistanceToCoastResult getDistanceToCoast(LatLng targetPoint) {
		Integer clientId = kbContext.getClientAuth().getClientId();
		// Find the minimum and maximum coastline sort orders that are nearest to the points on the bounding box
		MinMaxCoastlinePointSortOrder boundingCoastlinePointSortOrders = gridPointDao.getBoundingCoastlinePointSortOrders(clientId, targetPoint);
		if(null == boundingCoastlinePointSortOrders) throw new InvalidLatLngException("Lat/lng is out of bounds");
		
		LOG.trace(String.format("Getting sort orders between %f and %f", boundingCoastlinePointSortOrders.getMin(), boundingCoastlinePointSortOrders.getMax()));
		
		// Get the points surrounding the target point
		List<GridPoint> boundingGridPoints = gridPointDao.getPointsSurrounding(kbContext.getClientAuth().getClientId(), targetPoint);
		if(null == boundingGridPoints || boundingGridPoints.isEmpty()) throw new InvalidLatLngException("Lat/lng is out of bounds");
		
		// Obtain the coastline points between the min and the max sort orders
		List<CoastlinePoint> coastlinePoints = coastlinePointDao.getPointsBetweenSortOrders(clientId, boundingCoastlinePointSortOrders.getMin(), boundingCoastlinePointSortOrders.getMax());
		if(null == coastlinePoints || coastlinePoints.isEmpty()) throw new InvalidLatLngException("Lat/lng is unbounded by known coastline");
		
		LOG.debug(String.format("%d coastline points found", coastlinePoints.size()));
		// Now calculate the distance to coast from the target point to each coastline point. The minimum distance is the final Distance to Coast.
		CoastlinePoint closestCoastlinePoint = null;
		Double minimumMiles = 9999.0;
		for(CoastlinePoint coastlinePoint: coastlinePoints) {
			Double milesBetween = getMilesBetween(targetPoint, coastlinePoint);
			LOG.trace(String.format("Coastline point %s with sort order %f is %f miles from target", coastlinePoint, coastlinePoint.getSortOrder(), milesBetween));
			if(milesBetween < minimumMiles) {
				LOG.trace(String.format("Coastline point %s with sort order %f is the new winner at %f miles from target", coastlinePoint, coastlinePoint.getSortOrder(), milesBetween));
				minimumMiles = milesBetween;
				closestCoastlinePoint = coastlinePoint;
			}
		}
		
		if(minimumMiles < 10.0) {
			// The closer we are to the coast, the more likely the actual nearest point is between two defined coastal points.
			// Here we use the Mengelt Intersection formula to approximate the shortest distance to any point between the known coastal points
			List<CoastlinePoint> pointsBeforeAndAfter = coastlinePointDao.getPointsBeforeAndAfter(kbContext.getClientAuth().getClientId(), closestCoastlinePoint);
			DistanceCalculator distanceCalculator = new CloseDistanceCalculatorImpl(targetPoint, pointsBeforeAndAfter.get(1), closestCoastlinePoint, pointsBeforeAndAfter.get(0));
			
			// Get the coastal lat/lng as well as miles
			DistanceCalculatorResult result = distanceCalculator.calculate();
			if(null != result && result.getDistanceInMiles() < minimumMiles) {
				closestCoastlinePoint = result.getCoastlinePoint();
				minimumMiles = result.getDistanceInMiles();
			}
		}
		
		DistanceToCoastResult result = new DistanceToCoastResult();
		result.setDistanceInMiles(RoundingUtil.round(minimumMiles, 3));
		result.setCoastlinePoint(closestCoastlinePoint);
		result.setTargetPoint(targetPoint);
		return result;
	}
	@Override
	public Double getMilesBetween(LatLng targetPoint, CoastlinePoint coastlinePoint) {
		DistanceCalculator distanceCalculator = new DefaultDistanceCalculatorImpl(targetPoint, coastlinePoint);
		Double distance = distanceCalculator.calculate().getDistanceInMiles();
		return distance;
	}
	
}
