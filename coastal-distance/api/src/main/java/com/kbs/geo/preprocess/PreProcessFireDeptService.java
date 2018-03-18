package com.kbs.geo.preprocess;

public interface PreProcessFireDeptService {

	void process(String gridPointTableSuffix, String writeGridPointTableSuffix);

	void createIntermediaryGridPoints(String tableSuffix, Double boundingBoxSize, Double latLngIncrement);

	void findRootFireDepartmentAssociations(String tableSuffix, Double boundingBoxSize, Double latLngIncrement,
			String resolution);

}