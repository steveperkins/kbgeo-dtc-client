package com.kbs.biz.db;

import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import com.kbs.biz.model.ContactUs;

@Component
public class ContactUsDao {
	
	private static final String INSERT_SQL = "INSERT INTO contact_us(name, company, email, phone, message) VALUES(?, ?, ?, ?, ?)";
	@Autowired
	private DataSource datasource;
	
	private PreparedStatementCreatorFactory insertStatementFactory;
	
	public ContactUs save(ContactUs contactUs) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		jdbcTemplate.update(getInsertStatementFactory().newPreparedStatementCreator(new String[] { 
				contactUs.getName(),
				contactUs.getCompany(),
				contactUs.getEmail(),
				contactUs.getPhone(),
				contactUs.getMessage()
		}), keyHolder);
		contactUs.setId(keyHolder.getKey().longValue());
		
		return contactUs;
	}
	
	private PreparedStatementCreatorFactory getInsertStatementFactory() {
		if(null == insertStatementFactory) {
			insertStatementFactory = new PreparedStatementCreatorFactory(INSERT_SQL, new int[] { 
					Types.VARCHAR, // client ID
					Types.VARCHAR, // request type
					Types.VARCHAR, // year
					Types.VARCHAR, // month
					Types.VARCHAR, // request count
			});
			insertStatementFactory.setReturnGeneratedKeys(Boolean.TRUE);
			insertStatementFactory.setGeneratedKeysColumnNames(new String[] { "id" });
		}
		return insertStatementFactory;
	}
	
	
}
