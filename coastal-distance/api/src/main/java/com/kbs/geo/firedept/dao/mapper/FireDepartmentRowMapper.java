package com.kbs.geo.firedept.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kbs.geo.firedept.model.FireDepartment;

public class FireDepartmentRowMapper implements RowMapper<FireDepartment> {
	
	@Override
	public FireDepartment mapRow(ResultSet rs, int rowNum) throws SQLException {
		FireDepartment dept = new FireDepartment();
		dept.setId(rs.getInt("id"));
		dept.setLat(rs.getBigDecimal("lat"));
		dept.setLng(rs.getBigDecimal("lon"));
		
		dept.setStateId(rs.getString("state_id"));
		dept.setName(rs.getString("name"));
		dept.setAddress(rs.getString("address"));
		dept.setCity(rs.getString("city"));
		dept.setState(rs.getString("state"));
		dept.setZip(rs.getString("zip"));
		dept.setCounty(rs.getString("county"));
		dept.setStaffType(rs.getString("staff_type"));
		dept.setOrganizationType(rs.getString("org_type"));
		dept.setWebsite(rs.getString("website"));
		dept.setStationCount(rs.getInt("station_count"));
		dept.setCareerFirefighterCount(rs.getInt("ff_career_count"));
		dept.setVolunteerFirefighterCount(rs.getInt("ff_volunteer_count"));
		dept.setPaidPerCallFirefighterCount(rs.getInt("ff_ppc_count"));
		dept.setIsEmergencyManagementPrimary(rs.getBoolean("is_emergency_mgmt_primary"));
		
		return dept;
	}

}