package com.kbs.geo.coastal.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.kbs.geo.coastal.http.exception.GeocodingException;
import com.kbs.geo.coastal.model.GeocodeResult;
import com.kbs.geo.coastal.service.GeocodeService;

@Service
public class GoogleGeocodeServiceImpl implements GeocodeService {
	private static final Logger LOG = LoggerFactory.getLogger(GoogleGeocodeServiceImpl.class);
	
	@Resource(name="apiGoogleMapsKey")
	private String apiGoogleMapsKey;
	
	@Override
	public GeocodeResult geocode(String address) {
		LOG.debug("Geocoding address {}", address);
		
		// Request geocoding for the given address
		GeoApiContext context = new GeoApiContext().setApiKey(apiGoogleMapsKey);
		try {
			GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
			LOG.debug("Google geocoding returned for address {}", address);
			if(null == results || results.length < 1) throw new GeocodingException("Lat/lng could not be found for given address");
			
			GeocodeResult geocodeResult = parseResponse(results[0]);
			return geocodeResult;
		} catch (Exception e) {
			String msg = String.format("Could not geocode address '%s'", address);
			LOG.error(msg, e);
			throw new GeocodingException(msg, e);
		}
	}
	
	private GeocodeResult parseResponse(GeocodingResult response) {
		LOG.debug("Geocoding returned partial match? " + (Boolean.TRUE.equals(response.partialMatch)));
		
		GeocodeResult geocodeResult = new GeocodeResult();
		geocodeResult.setFullAddress(response.formattedAddress);
		
		String addressLine1 = "";
		String zip = "";
		
		// Go through the returned address components and pick out the ones we care about
		for(AddressComponent component: response.addressComponents) {
			for(AddressComponentType componentType: component.types) {
				if(AddressComponentType.STREET_NUMBER.equals(componentType)) {
					addressLine1 = component.shortName + addressLine1;
					break;
				}
				if(AddressComponentType.STREET_ADDRESS.equals(componentType) || AddressComponentType.ROUTE.equals(componentType)) {
					addressLine1 += " " + component.shortName;
					break;
				}
				if(AddressComponentType.LOCALITY.equals(componentType)) {
					geocodeResult.setCity(component.shortName);
					break;
				}
				if(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_2.equals(componentType)) {
					geocodeResult.setCounty(component.shortName);
					break;
				}
				if(AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1.equals(componentType)) {
					geocodeResult.setState(component.shortName);
					break;
				}
				if(AddressComponentType.POSTAL_CODE.equals(componentType)) {
					zip = component.shortName;
					break;
				}
				if(AddressComponentType.POSTAL_CODE_SUFFIX.equals(componentType)) {
					zip += "-" + component.shortName;
					break;
				}
			}
		}
		
		if(StringUtils.isNotBlank(addressLine1)) geocodeResult.setLine1(addressLine1);
		if(StringUtils.isNotBlank(zip)) geocodeResult.setZip(zip);
		
		geocodeResult.setLat(response.geometry.location.lat);
		geocodeResult.setLng(response.geometry.location.lng);
		
		return geocodeResult;
	}

}
