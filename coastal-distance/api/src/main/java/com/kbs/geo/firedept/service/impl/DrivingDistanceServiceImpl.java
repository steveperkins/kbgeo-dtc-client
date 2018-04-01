package com.kbs.geo.firedept.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.maps.DirectionsApi.RouteRestriction;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.kbs.geo.coastal.http.exception.DistanceMatrixException;
import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.firedept.model.DrivingDistanceResult;
import com.kbs.geo.firedept.service.DrivingDistanceService;

@Service
public class DrivingDistanceServiceImpl implements DrivingDistanceService {
	private static final Logger LOG = LoggerFactory.getLogger(DrivingDistanceServiceImpl.class);
	
	@Resource(name="apiGoogleMapsKey")
	private String apiGoogleMapsKey;
	
	/* (non-Javadoc)
	 * @see com.kbs.geo.firedept.service.impl.DrivingDistanceService#getDistanceBetween(com.kbs.geo.coastal.model.GeoCoordinate, com.kbs.geo.coastal.model.GeoCoordinate)
	 */
	@Override
	public DrivingDistanceResult getDistanceBetween(GeoCoordinate origin, GeoCoordinate destination) throws DistanceMatrixException {
		// Request driving distance between the given points
		GeoApiContext context = new GeoApiContext.Builder().apiKey(apiGoogleMapsKey).build();
		try {
			DistanceMatrixApiRequest matrixRequest = 
					DistanceMatrixApi.newRequest(context)
					.origins(origin.getLat().doubleValue() + "," + origin.getLng().doubleValue())
					.destinations(destination.getLat().doubleValue() + "," + destination.getLng().doubleValue())
					.avoid(RouteRestriction.FERRIES);
			
			DistanceMatrix matrix = matrixRequest.await();
			
			LOG.debug("Google distance matrix returned for [" + origin.getLat().doubleValue() + "," + origin.getLng().doubleValue() + "], " + destination.getLat().doubleValue() + "," + destination.getLng().doubleValue() + "]");
			if(null == matrix) throw new DistanceMatrixException("No route found for given address");
			if(null == matrix.rows || matrix.rows.length < 1 || null == matrix.rows[0].elements || matrix.rows[0].elements.length < 1) throw new DistanceMatrixException("Driving distance could not be determined");
			
			DrivingDistanceResult drivingDistanceResult = new DrivingDistanceResult();
			DistanceMatrixElement element = matrix.rows[0].elements[0];
			drivingDistanceResult.setDistanceMiles(element.distance.inMeters * 0.000621371);
			drivingDistanceResult.setDurationSeconds(element.duration.inSeconds);
			if(null != element.durationInTraffic) {
				drivingDistanceResult.setDurationAdjustedForTraffic(element.durationInTraffic.inSeconds);
			}
			
			return drivingDistanceResult;
		} catch (Exception e) {
			String msg = String.format("Could not find driving route for [" + origin.getLat().doubleValue() + "," + origin.getLng().doubleValue() + "]");
			LOG.error(msg, e);
			throw new DistanceMatrixException(msg, e);
		}
	}
}
