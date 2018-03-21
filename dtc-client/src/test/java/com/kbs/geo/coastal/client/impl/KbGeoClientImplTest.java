package com.kbs.geo.coastal.client.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.kbs.geo.coastal.client.exception.InvalidUrlParameterException;
import com.kbs.geo.coastal.client.exception.NoApiKeySetException;
import com.kbs.geo.coastal.client.model.DtcResult;

public class KbGeoClientImplTest {

	private String testApiKey = "vRt2zfxNF3";
	@Test
	public void test() throws NoApiKeySetException, InvalidUrlParameterException, IOException {
		KbGeoClientImpl client = KbGeoClientImpl.createClient(testApiKey);
		DtcResult result = client.getDistanceToCoast("1919 Alliant Energy Center Way Madison WI 53713");
		assertNotNull(result);
		assertEquals(702.4893212372758, result.getDistanceInMiles());
		assertEquals(43.045583, result.getTargetPoint().getLat());
		assertEquals(-89.3799951, result.getTargetPoint().getLng());
		assertEquals(38.662389, result.getCoastlinePoint().getLat());
		assertEquals(-77.236111, result.getCoastlinePoint().getLng());
	}

}
