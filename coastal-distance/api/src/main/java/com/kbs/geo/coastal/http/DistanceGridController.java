package com.kbs.geo.coastal.http;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.kbs.geo.coastal.service.DistanceGridService;

@RestController("distance-grid")
public class DistanceGridController {
	private static final Logger LOG = Logger.getLogger(DistanceGridController.class);
	
	@Autowired
	DistanceGridService distanceGridService;
	
	/*@RequestMapping(value="/", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody DistanceGridPoint getCoastalDistance(@RequestParam("lat") Double lat, @RequestParam("lon") Double lon) {
    	LOG.info("GET JSON coastal-distance/distance-grid?lat={}&lon={}", lat, lon);
    	return distanceGridService.getNearestPoint(new LatLng(lat, lon));
    }*/
}
