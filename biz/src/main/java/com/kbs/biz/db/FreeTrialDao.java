package com.kbs.biz.db;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.kbs.biz.model.ContactUs;

@Component
public class FreeTrialDao {
	
//	private static final String INSERT_CLIENT_SQL = "INSERT INTO kbs_client (name, address, city, state, zip, phone, created) VALUES(?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
//	private static final String INSERT_CLIENT_CONTACT_SQL = "INSERT INTO client_contact (client_id, last_name, first_name, email, address, city, state, zip, phone, is_primary, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
//	private static final String INSERT_CLIENT_CONTRACT_SQL = "INSERT INTO client_contract (client_id, name, request_type_id, max_requests, cents_per_request, starts, expires, created) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
//	private static final String INSERT_CLIENT_AUTH_SQL = "INSERT INTO client_auth (client_id, name, token, expires, created, updated) VALUES(?, ?, ?, ?, ?, ?)";
	
	@Autowired
	@Qualifier("apiDataSource")
	private DataSource apiDatasource;
	
	public String createFreeTrial(ContactUs contactUs) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(apiDatasource)
                .withTableName("kbs_client")
                .usingGeneratedKeyColumns("id");
		
		Map<String, Object> parameters = new HashMap<String, Object>(7);
        parameters.put("name", contactUs.getName());
        parameters.put("address", "No address");
        parameters.put("city", "No city");
        parameters.put("state", "WI");
        parameters.put("zip", "53716");
        parameters.put("phone", "No phone");
        parameters.put("created", new DateTime().toDate());
        Number newId = insert.executeAndReturnKey(parameters);
        
        long clientId = newId.intValue();
        
        insert = new SimpleJdbcInsert(apiDatasource)
                .withTableName("client_contact")
                .usingGeneratedKeyColumns("id");
		
		parameters = new HashMap<String, Object>(11);
        parameters.put("client_id", clientId);
        parameters.put("last_name", contactUs.getName());
        parameters.put("first_name", contactUs.getName());
        parameters.put("email", contactUs.getEmail());
        parameters.put("address", "No address");
        parameters.put("city", "No city");
        parameters.put("state", "WI");
        parameters.put("zip", "53716");
        parameters.put("phone", "No phone");
        parameters.put("is_primary", true);
        parameters.put("created", new DateTime().toDate());
        newId = insert.executeAndReturnKey(parameters);
        
        long clientContactId = newId.intValue();
        
        insert = new SimpleJdbcInsert(apiDatasource)
                .withTableName("client_contract")
                .usingGeneratedKeyColumns("id");
		
        int contractType = 2; // Distance to Coast
        if(StringUtils.isNotBlank(contactUs.getContractType()) && "dtf".equalsIgnoreCase(contactUs.getContractType())) {
        	contractType = 4; // Distance to Fire Station
        }
		parameters = new HashMap<String, Object>(8);
        parameters.put("client_id", clientId);
        parameters.put("name", contactUs.getCompany());
        parameters.put("request_type_id", contractType);
        parameters.put("max_requests", 100L);
        parameters.put("cents_per_request", 0);
        parameters.put("starts", new DateTime().toDate());
        parameters.put("expires", new DateTime().plusDays(30).toDate());
        parameters.put("created", new DateTime().toDate());
        newId = insert.executeAndReturnKey(parameters);
        
        long clientContractId = newId.intValue();
        
        String apiKey = UUID.randomUUID().toString();
        insert = new SimpleJdbcInsert(apiDatasource)
                .withTableName("client_auth")
                .usingGeneratedKeyColumns("id");
		
		parameters = new HashMap<String, Object>(6);
        parameters.put("client_id", clientId);
        parameters.put("name", contactUs.getCompany());
        parameters.put("token", apiKey);
        parameters.put("expires", new DateTime().plusDays(30).toDate());
        parameters.put("created", new DateTime().toDate());
        parameters.put("updated", new DateTime().toDate());
        newId = insert.executeAndReturnKey(parameters);
        
        long clientAuthId = newId.intValue();
		
		return apiKey;
	}
	
	
}
