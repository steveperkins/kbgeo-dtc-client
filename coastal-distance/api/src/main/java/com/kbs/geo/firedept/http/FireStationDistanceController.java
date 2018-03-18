package com.kbs.geo.firedept.http;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.AccentStripper;
import com.kbs.geo.coastal.http.exception.GeocodingException;
import com.kbs.geo.coastal.http.exception.InvalidLatLngException;
import com.kbs.geo.coastal.http.exception.NoLatLngResultFoundException;
import com.kbs.geo.coastal.model.GeocodeResult;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.GeocodeService;
import com.kbs.geo.coastal.validator.AddressValidator;
import com.kbs.geo.coastal.validator.LatLngValidator;
import com.kbs.geo.firedept.model.DistanceToFireStationResult;
import com.kbs.geo.firedept.service.FireDepartmentService;

@RestController
@RequestMapping(value="fire-station")
public class FireStationDistanceController {
	private static final Logger LOG = Logger.getLogger(FireStationDistanceController.class);
	
	@Autowired
	private FireDepartmentService fireDepartmentService;
	
	@Autowired
	private GeocodeService geocodeService;
	
	@Autowired
	private LatLngValidator latLngValidator;
	
	@RequestMapping(value="v{version}/coord", method=RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody DistanceToFireStationResult getByCoord(@PathVariable("version") Integer version, @RequestParam(value = "lat", required = false) Double lat, @RequestParam(value = "lng", required = false) Double lng) {
		if(null == lat || null == lng) throw new InvalidLatLngException("Both lat and lng must both be provided");
		
		LatLng latLng = new LatLng(new BigDecimal(lat), new BigDecimal(lng));
		latLngValidator.validate(latLng);
    	LOG.info(String.format("GET JSON /fire-station/v%d/coord?lat=%f&lon=%f", version, lat, lng));
    	DistanceToFireStationResult result = fireDepartmentService.getNearestFireDept(latLng);
    	if(result == null || result.getTargetPoint() == null) throw new NoLatLngResultFoundException(String.format("Could not determine distance to fire station for [%f, %f]", lat, lng));
    	return result;
    }
	
	@RequestMapping(value="v{version}/address", method=RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody DistanceToFireStationResult getByAddress(@PathVariable("version") Integer version, @RequestParam("address") String address) {
		// Strip out any invalid encoding
		address = address.replaceAll(",", "").replaceAll("%2C", "").replaceAll("  ", " ").replaceAll("\\+\\+", "\\+");
		
		new AddressValidator().validate(address);
    	LOG.info(String.format("GET JSON /fire-station/v%d/address?address=%s", version, address));
    	address = AccentStripper.stripAccents(address);
    	LOG.debug(String.format("Address stripped of accents: %s", address));
    	
    	// Geocode this address
    	GeocodeResult geocodeResult = geocodeService.geocode(address);
    	if(null == geocodeResult || null == geocodeResult.getLat() || null == geocodeResult.getLng()) throw new GeocodingException("Could not geocode address");
    	
    	LatLng latLng = new LatLng(new BigDecimal(geocodeResult.getLat()), new BigDecimal(geocodeResult.getLng()));
    	latLngValidator.validate(latLng);
    	
    	// Find the distance to coast for the geocoded address
    	DistanceToFireStationResult result = fireDepartmentService.getNearestFireDept(latLng);
    	if(result == null) throw new NoLatLngResultFoundException(String.format("Could not determine distance to fire station for [%f, %f]", latLng.getLat(), latLng.getLng()));
    	return result;
    }
	
}