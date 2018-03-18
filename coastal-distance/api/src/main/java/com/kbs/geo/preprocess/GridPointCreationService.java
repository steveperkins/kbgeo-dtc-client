package com.kbs.geo.preprocess;

public interface GridPointCreationService {

	void createInitialGridPoints(String tableSuffix, Double latLngIncrement);

}