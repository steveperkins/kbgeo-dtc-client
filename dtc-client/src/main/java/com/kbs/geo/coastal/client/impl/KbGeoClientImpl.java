package com.kbs.geo.coastal.client.impl;

import java.io.IOException;
import java.net.URLEncoder;

import com.kbs.geo.coastal.client.KbGeoClient;
import com.kbs.geo.coastal.client.exception.InvalidUrlParameterException;
import com.kbs.geo.coastal.client.exception.NoApiKeySetException;
import com.kbs.geo.coastal.client.model.DtcResult;
import com.kbs.geo.coastal.client.model.LatLng;

/**
 * If you are using this client from outside a web container and receive an SSLHandshakeException about PKIX path building,
 * you will need to import the COMODO RSA Domain Validation Secure Server CA certificate into your JVM:
 * 1. Download comodorsacertificationauthority.crt from https://support.comodo.com/index.php?/Knowledgebase/Article/View/969/108/root-comodo-rsa-certification-authority-sha-2
 * 2. Run "%JAVA_HOME/bin/keytool" -import -alias comodocacert -keystore "JAVA_HOME/jre/lib/security/cacerts" -trustcacerts -file comodorsacertificationauthority.crt
 * 
 * @author http://www.kbgeo.com
 *
 */
public class KbGeoClientImpl extends AbstractHttpClient implements KbGeoClient {
	private static final String DTC_BY_COORDS_SUFFIX = "/coord?lat=%s&lng=%s";
	private static final String DTC_BY_ADDRESS_SUFFIX = "/address?address=%s";
	public static KbGeoClientImpl createClient(String apiKey) {
		return new KbGeoClientImpl(apiKey);
	}
	
	public KbGeoClientImpl(String apiKey) {
		setApiKey(apiKey);
	}
	
	/**
	 * Retrieves distance to coast by geographic coordinates
	 * @param latLng the target inland point
	 * @return
	 * @throws NoApiKeySetException
	 * @throws InvalidUrlParameterException
	 * @throws IOException
	 */
	public DtcResult getDistanceToCoast(LatLng latLng) throws NoApiKeySetException, InvalidUrlParameterException, IOException {
		String suffix = String.format(DTC_BY_COORDS_SUFFIX, latLng.getLat(), latLng.getLng());
		return getResult(suffix, DtcResult.class);
	}
	
	/**
	 * Retrieves distance to coast by address. <code>address</code> is reverse geo-coded and its coordinates used to determine distance to coast, 
	 * so you may receive an error or an empty response if the given address is invalid.
	 * @param address the address for which to find distance to coast
	 * @return
	 * @throws NoApiKeySetException
	 * @throws InvalidUrlParameterException
	 * @throws IOException
	 */
	public DtcResult getDistanceToCoast(String address) throws NoApiKeySetException, InvalidUrlParameterException, IOException {
		String suffix = String.format(DTC_BY_ADDRESS_SUFFIX, URLEncoder.encode(address, "UTF-8"));
		return getResult(suffix, DtcResult.class);
	}
}
