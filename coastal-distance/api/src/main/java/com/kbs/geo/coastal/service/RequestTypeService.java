package com.kbs.geo.coastal.service;

import java.util.List;

import com.kbs.geo.coastal.model.billing.RequestType;

public interface RequestTypeService {
	Long save(RequestType requestType);
	RequestType get(Long id);
	List<RequestType> getAll();
}
