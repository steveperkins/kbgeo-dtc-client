package com.kbs.geo.coastal.http.model;

import java.io.Serializable;
import java.util.Date;

import com.kbs.geo.coastal.http.exception.KbsRestException;

public class HttpErrorModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String error;
	private Date timestamp;
	
	public HttpErrorModel() {
		timestamp = new Date();
	}
	
	public HttpErrorModel(String message) {
		this.error = message;
		this.timestamp = new Date();
	}
	
	public HttpErrorModel(KbsRestException e) {
		this();
		this.error = e.getMessage();
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	// TODO This is the poor man's JSON serialization
	public String toJson() {
		return new StringBuilder().append("{ ")
				.append("\"error\": ").append(error).append(",")
				.append("\"timestamp\": ").append(timestamp)
				.append("}").toString();
	}
}
