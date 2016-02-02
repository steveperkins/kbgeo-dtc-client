package com.kbs.geo.coastal.service;

import com.kbs.geo.coastal.model.GeocodeResult;

public interface GeocodeService {
	GeocodeResult geocode(String address);
}
