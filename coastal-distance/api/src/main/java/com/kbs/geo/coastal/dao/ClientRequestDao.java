package com.kbs.geo.coastal.dao;

import java.util.Date;
import java.util.List;

import com.kbs.geo.coastal.model.billing.ClientRequest;
import com.kbs.geo.coastal.model.billing.RequestType;

public interface ClientRequestDao {
	Integer save(ClientRequest request);
	ClientRequest get(Integer id);
	List<ClientRequest> getByClientId(Integer clientId);
	Long getCount(Integer clientId, Date beginDate, Date endDate, RequestType requestType);
}
