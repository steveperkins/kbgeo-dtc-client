package com.kbs.geo.http.security;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.model.billing.ClientAuthWeb;

/**
 * Spring-injectable container for authenticated user information. This context object is automatically populated 
 * in the context of a request to a console URL.
 * 
 * @author Steve
 *
 */
@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class KbConsoleUserContext {
	private ClientAuthWeb clientAuthWeb;

	public ClientAuthWeb getClientAuthWeb() {
		return clientAuthWeb;
	}

	public void setClientAuthWeb(ClientAuthWeb clientAuthWeb) {
		this.clientAuthWeb = clientAuthWeb;
	}
	
}
