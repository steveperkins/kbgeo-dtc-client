package com.kbs.geo.coastal.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.model.DistanceGridPoint;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.coastal.service.DistanceGridService;

@RestController("distance-grid")
public class DistanceGridController {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceGridController.class);
	
	@Autowired
	DistanceGridService distanceGridService;
	
	/*@RequestMapping(value="/", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody DistanceGridPoint getCoastalDistance(@RequestParam("lat") Double lat, @RequestParam("lon") Double lon) {
    	LOG.info("GET JSON coastal-distance/distance-grid?lat={}&lon={}", lat, lon);
    	return distanceGridService.getNearestPoint(new LatLng(lat, lon));
    }*/
}
