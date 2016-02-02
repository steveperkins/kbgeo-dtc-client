package com.kbs.geo.coastal.http.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Provides access to the request body without preventing the input stream from being read again
 * @author Steve
 *
 */
public class BufferedHttpServletRequest extends HttpServletRequestWrapper {
	
	private String body;
	public BufferedHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);

		// read the original payload into the payload variable
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try (InputStream inputStream = request.getInputStream()) {
			// read the payload into the StringBuilder
			if(null == inputStream) {
				// make an empty string since there is no payload
				stringBuilder.append("");
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
		}
		body = stringBuilder.toString();
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream inputStream = new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
		return inputStream;
	}
	
	public String getBody() {
		return body;
	}
}
