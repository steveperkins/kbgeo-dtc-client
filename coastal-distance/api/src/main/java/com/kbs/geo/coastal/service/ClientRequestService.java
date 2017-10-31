package com.kbs.geo.coastal.service;

import java.util.Date;
import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.model.billing.RequestType;

public interface ClientRequestService {
	Integer save(ClientRequest request);
	ClientRequest get(Integer id);
	List<ClientRequest> getByClientId(Integer clientId);
	Long getRequestCount(Integer clientId, Date beginDate, Date endDate, RequestType requestType);
	Long getRequestCount(Integer clientId, RequestType requestType);
}
