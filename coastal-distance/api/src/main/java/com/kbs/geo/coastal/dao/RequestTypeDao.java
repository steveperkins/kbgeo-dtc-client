package com.kbs.geo.coastal.dao;

import java.util.List;

import com.kbs.geo.coastal.model.billing.RequestType;

public interface RequestTypeDao {
	Long save(RequestType requestType);
	RequestType get(Integer id);
	List<RequestType> getAll();
}
