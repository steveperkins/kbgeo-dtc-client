package com.kbs.geo.coastal.dao;

import java.util.Date;

import com.kbs.geo.coastal.model.billing.RequestError;
import com.kbs.geo.coastal.model.billing.RequestType;

public interface RequestErrorDao {
	Integer save(RequestError request);
	RequestError get(Integer id);
	Long getCount(Integer clientId, Date beginDate, Date endDate, RequestType requestType);
}
