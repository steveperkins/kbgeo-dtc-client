package com.kbs.geo.coastal.client.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbs.geo.coastal.client.exception.InvalidApiKeyException;
import com.kbs.geo.coastal.client.exception.InvalidUrlParameterException;
import com.kbs.geo.coastal.client.exception.MalformedRequestException;
import com.kbs.geo.coastal.client.exception.NoApiKeySetException;
import com.kbs.geo.coastal.client.exception.ServerException;
import com.kbs.geo.coastal.client.exception.TimeoutException;

abstract class AbstractHttpClient {
	private static final String apiRootUrl = "https://api.kbgeo.com/coastal-distance/v1";
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	private static String apiKey;
	
	protected static final String getApiRootUrl() {
		return apiRootUrl;
	}
	
	protected static final String getApiKey() {
		return apiKey;
	}
	
	public static void setApiKey(String apiKey) {
		AbstractHttpClient.apiKey = apiKey;
	}
	
	protected <T> T getResult(String urlSuffix, Class<T> responseObjectType) throws NoApiKeySetException, InvalidUrlParameterException, IOException {
		if(null == getApiKey() || "".equals(getApiKey().trim())) {
			throw new NoApiKeySetException("You must set an API key before requesting Distance to Coast");
		}
		if(null == urlSuffix || "".equals(urlSuffix.trim())) {
			throw new InvalidUrlParameterException("Missing target latitude/longitude pair or address");
		}
		
		URL url = new URL(getApiRootUrl() + urlSuffix);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			conn.setInstanceFollowRedirects(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("kb-auth-token", getApiKey());
			conn.connect();
			
			int status = conn.getResponseCode();
			// Handle redirects
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
			  || status == HttpURLConnection.HTTP_MOVED_PERM) {
			    String location = conn.getHeaderField("Location");
			    url = new URL(location + urlSuffix.trim());
			    conn = (HttpURLConnection) url.openConnection();
			    conn.setInstanceFollowRedirects(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("kb-auth-token", getApiKey());
				conn.connect();
			}
			
			checkResponseCodeForErrors(status, url.toString());
			
			String content = readStream(conn.getInputStream());
			T obj = jsonMapper.readValue(content, responseObjectType);
			return obj;
		} finally {
			conn.disconnect();
		}
	}
	
	private void checkResponseCodeForErrors(int status, String url) {
		if(status == 429) {
			throw new InvalidApiKeyException("Your API key has surpassed its usage threshold. Contact sales@kbgeo.com to modify your plan.");
		}
		if(status == HttpURLConnection.HTTP_FORBIDDEN || status == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new InvalidApiKeyException("Invalid API key. Your key may have expired, or failed request security checks. Contact sales@kbgeo.com to extend trial period or purchase an API plan.");
		}
		if(status == HttpURLConnection.HTTP_NOT_FOUND) {
			throw new InvalidUrlParameterException("Could not connect to API at " + url);
		}
		if(status == HttpURLConnection.HTTP_BAD_REQUEST) {
			throw new MalformedRequestException("Invalid request URL: " + url);
		}
		if(status == HttpURLConnection.HTTP_NOT_FOUND) {
			throw new MalformedRequestException("Invalid URL: " + url + ". The server does not have an endpoint at this address.");
		}
		if(status == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
			throw new TimeoutException("Timed out while waiting for response from URL " + url);
		}
		if(status == HttpURLConnection.HTTP_NOT_ACCEPTABLE || status == HttpURLConnection.HTTP_BAD_METHOD) {
			throw new MalformedRequestException("Invalid request at URL " + url + ". This is likely a bad HTTP verb or Accept header.");
		}
		if(status == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
			throw new TimeoutException("Timed out while waiting for response from URL " + url);
		}
		if(status == HttpURLConnection.HTTP_INTERNAL_ERROR) {
			throw new ServerException("Server returned HTTP 500 for URL: " + url + ". Try the same request again later.");
		}
		if(status == 503) {
			throw new ServerException("API is unavailable. Please wait 10 minutes and try again.");
		}
	}
	
	private String readStream(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		try {
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			return content.toString();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
