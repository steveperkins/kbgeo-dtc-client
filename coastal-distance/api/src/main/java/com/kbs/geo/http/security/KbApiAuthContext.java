package com.kbs.geo.http.security;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.model.billing.ClientAuth;

/**
 * Spring-injectable container for authenticated client information. This context object is automatically populated 
 * in the context of a request to an API URL.
 * 
 * @author Steve
 *
 */
@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class KbApiAuthContext {
	private ClientAuth clientAuth;

	public ClientAuth getClientAuth() {
		return clientAuth;
	}

	public void setClientAuth(ClientAuth clientAuth) {
		this.clientAuth = clientAuth;
	}
	
}
