package com.kbs.geo.firedept.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.kbs.geo.coastal.model.GeoCoordinate;
import com.kbs.geo.firedept.dao.FireDeptDao;
import com.kbs.geo.firedept.dao.mapper.FireDepartmentRowMapper;
import com.kbs.geo.firedept.model.FireDepartment;

@Component
public class FireDeptDaoImpl implements FireDeptDao {
	private static final String SELECT_BY_ID_SQL = "SELECT * FROM fire_dept WHERE id=?";
	private static final String GET_ALL_SQL = "SELECT * FROM fire_dept";
	private static final String GET_FIRE_DEPTS_FROM_SURROUNDING_POINTS_SQL = "SELECT DISTINCT fd.* FROM grid_point_fire gp INNER JOIN fire_dept fd ON gp.fire_dept_id=fd.id WHERE gp.lat BETWEEN (? - 0.1) AND (? + 0.1) AND gp.lon BETWEEN (? - 0.1) AND (? + 0.1)";
	
	@Autowired
	private DataSource datasource;
	
	private JdbcTemplate jdbcTemplate;
	private FireDepartmentRowMapper rowMapper = new FireDepartmentRowMapper();
	
	@PostConstruct
	public void init() {
		jdbcTemplate = new JdbcTemplate(datasource);
	}
	
	@Override
	public FireDepartment get(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		FireDepartment dept = jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, new Object[]{ id }, rowMapper);
		return dept;
	}
	
	@Override
	public List<FireDepartment> getAll() {
		List<FireDepartment> depts = jdbcTemplate.query(GET_ALL_SQL, rowMapper);
		return (null == depts ? new ArrayList<FireDepartment>() : depts);
	}
	
	@Override
	public List<FireDepartment> getNearestToPoint(GeoCoordinate point) {
		List<FireDepartment> depts = jdbcTemplate.query(GET_FIRE_DEPTS_FROM_SURROUNDING_POINTS_SQL, new BigDecimal[] { point.getLat(), point.getLat(), point.getLng(), point.getLng() }, rowMapper);
		if(null == depts) {
			return new ArrayList<FireDepartment>();
		}
		return depts;
	}
	
}
