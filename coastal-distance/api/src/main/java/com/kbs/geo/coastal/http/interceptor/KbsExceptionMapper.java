package com.kbs.geo.coastal.http.interceptor;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.kbs.geo.coastal.http.exception.KbsRestException;
import com.kbs.geo.coastal.http.model.HttpErrorModel;
import com.kbs.geo.coastal.http.util.MediaTypeHelper;

@EnableWebMvc
@ControllerAdvice
public class KbsExceptionMapper {
	private static final Logger LOG = Logger.getLogger(KbsExceptionMapper.class);
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler({ KbsRestException.class })
	protected ResponseEntity<HttpErrorModel> handleUnauthorized(RuntimeException e, WebRequest request) {
		KbsRestException ex = (KbsRestException)e;
		LOG.error("EXCEPTION THROWN from " + request.getContextPath(), e);
		
		String accept = request.getHeader("Accept");
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaTypeHelper.getMediaType(accept));
        
        HttpErrorModel errorModel = new HttpErrorModel(ex);
        return new ResponseEntity<HttpErrorModel>(errorModel, headers, ex.getHttpStatus());
	}
}
