package com.kbs.geo.firedept.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.coastal.model.LatLng;
import com.kbs.geo.firedept.dao.FireDeptDao;
import com.kbs.geo.firedept.model.DistanceToFireStationResult;
import com.kbs.geo.firedept.model.FireDepartment;
import com.kbs.geo.firedept.service.FireDepartmentService;
import com.kbs.geo.math.DistanceCalculatorUtil;

@Service
public class FireDepartmentServiceImpl implements FireDepartmentService {

	private static final Logger LOG = Logger.getLogger(FireDepartmentServiceImpl.class);
			
	@Autowired
	private FireDeptDao dao;
	
	@Override
	public DistanceToFireStationResult getNearestFireDept(GeoCoordinate targetPoint) {
		List<FireDepartment> nearestDepartments = dao.getNearestToPoint(targetPoint);
		LOG.info("Searching " + nearestDepartments.size() + " departments for the nearest department to " + targetPoint);
		Double winningDistance = 999999999.0;
		FireDepartment winner = null;
		for(FireDepartment dept: nearestDepartments) {
			Double milesBetween = DistanceCalculatorUtil.getMilesBetween(new LatLng(dept.getLat(), dept.getLng()), targetPoint);
			if(milesBetween < winningDistance) {
				winner = dept;
				winningDistance = milesBetween;
			}
		}
		
		DistanceToFireStationResult result = new DistanceToFireStationResult();
		result.setDistanceInMiles(winningDistance);
		result.setFireDepartment(winner);
		result.setTargetPoint(targetPoint);
		return result;
	}
}
