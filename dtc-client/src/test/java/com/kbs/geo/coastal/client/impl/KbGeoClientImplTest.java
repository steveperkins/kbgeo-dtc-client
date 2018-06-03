package com.kbs.geo.coastal.client.impl;


import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.kbs.geo.coastal.client.exception.InvalidUrlParameterException;
import com.kbs.geo.coastal.client.exception.NoApiKeySetException;
import com.kbs.geo.coastal.client.model.DtcResult;
import com.kbs.geo.coastal.client.model.LatLng;

public class KbGeoClientImplTest {

	private String testApiKey = "4e8fa748-ada8-43ed-977e-4ab487d8de64";
	
	@Test
	public void testGetDistanceToCoastByAddress() throws NoApiKeySetException, InvalidUrlParameterException, IOException {
		KbGeoClientImpl client = KbGeoClientImpl.createClient(testApiKey);
		DtcResult result = client.getDistanceToCoast("1919 Alliant Energy Center Way Madison WI 53713");
		Assert.assertNotNull(result);
		Assert.assertEquals(702.4893212372758, result.getDistanceInMiles(), 0.5);
		Assert.assertEquals(43.045583d, result.getTargetPoint().getLat(), 0.5);
		Assert.assertEquals(-89.3799951d, result.getTargetPoint().getLng(), 0.5);
		Assert.assertEquals(38.662389d, result.getCoastlinePoint().getLat(), 0.5);
		Assert.assertEquals(-77.236111d, result.getCoastlinePoint().getLng(), 0.5);
	}
	
	@Test
	public void testGetDistanceToCoastByCoordinates() throws NoApiKeySetException, InvalidUrlParameterException, IOException {
		KbGeoClientImpl client = KbGeoClientImpl.createClient(testApiKey);
		DtcResult result = client.getDistanceToCoast(new LatLng(43.045583, -89.3799951));
		Assert.assertNotNull(result);
		Assert.assertEquals(702.4893212372758, result.getDistanceInMiles(), 0.5);
		Assert.assertEquals(43.045583, result.getTargetPoint().getLat(), 0.5);
		Assert.assertEquals(-89.3799951, result.getTargetPoint().getLng(), 0.5);
		Assert.assertEquals(38.662389, result.getCoastlinePoint().getLat(), 0.5);
		Assert.assertEquals(-77.236111, result.getCoastlinePoint().getLng(), 0.5);
	}

}
