package com.kbs.geo.coastal.http.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

public class MediaTypeHelper {
	private static final Logger LOG = Logger.getLogger(MediaTypeHelper.class);
	
	public static MediaType getMediaType(String t) {
		if(StringUtils.isBlank(t) || "*/*".equals(t)) return MediaType.APPLICATION_JSON;
		
		MediaType mediaType = null;
		try {
			mediaType = MediaType.parseMediaType(t);
		} catch(InvalidMediaTypeException e) {
			LOG.error(String.format("Could not parse media type '%s', defaulting to application/json", t), e);
			mediaType = MediaType.APPLICATION_JSON;
		}
		return mediaType;
	}
	
	public static String getFirstValidType(String list) {
		String defaultType = MediaType.APPLICATION_JSON_VALUE;
		if(null == list || "*/*".equals(list)) return defaultType;
		String[] types = list.split(",");
		for(String t: types) {
			t = t.toLowerCase();
			if(MediaType.APPLICATION_XML_VALUE.equals(t) || MediaType.APPLICATION_JSON_VALUE.equals(t)) return t;
		}
		return defaultType;
	}
}
