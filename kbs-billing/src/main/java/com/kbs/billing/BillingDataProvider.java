package com.kbs.billing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillingDataProvider {
	private final Logger LOG = LoggerFactory.getLogger(BillingDataProvider.class);

	private Connection connection;
	private PreparedStatement clientByIdStatement;
	private PreparedStatement monthRequestsStatement;
	private PreparedStatement successfulRequestsCountStatement;
	
	public String getClientName(Long clientId) throws SQLException {
		PreparedStatement statement = getClientNameByIdStatement();
		statement.setLong(1, clientId);
		ResultSet resultSet = statement.executeQuery();
		if(null == resultSet || !resultSet.next()) throw new SQLException("Couldn't get name for client with ID " + clientId);
		return resultSet.getString("name");
	}
	
	public ResultSet getMonthRequests(Long clientId, Date startDate, Date endDate) throws SQLException {
		PreparedStatement statement = getMonthRequestsStatement();
		statement.setLong(1, clientId);
		statement.setTimestamp(2, new java.sql.Timestamp(startDate.getTime()));
		statement.setTimestamp(3, new java.sql.Timestamp(endDate.getTime()));
		ResultSet resultSet = statement.executeQuery();
		return resultSet;
	}
	
	public Long getSuccessRequestCount(Long clientId, Date startDate, Date endDate) throws SQLException {
		PreparedStatement statement = getSuccessfulRequestsCountStatement();
		statement.setLong(1, clientId);
		statement.setTimestamp(2, new java.sql.Timestamp(startDate.getTime()));
		statement.setTimestamp(3, new java.sql.Timestamp(endDate.getTime()));
		ResultSet resultSet = statement.executeQuery();
		if(null == resultSet || !resultSet.next()) throw new SQLException("No results returned for client ID " + clientId + ", startDate '" + startDate.toString() + "', endDate '" + endDate.toString() + "'");
		return resultSet.getLong("count");
	}
	
	public Connection getConnection() throws SQLException {
		if(null == connection || connection.isClosed()) {
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", "billing");
		    connectionProps.put("password", "5ecUreb1lling");
	
		    connection = DriverManager.getConnection(
	                   "jdbc:postgresql://localhost:5432/coastal_distance",
	                   connectionProps);
	        LOG.debug("Database connection established");
		}
	    return connection;
	}
	
	public void shutdown() {
		try {
			if(null == connection || connection.isClosed()) return;
			connection.close();
		} catch(Exception e) {
		}
	}
	
	private PreparedStatement getMonthRequestsStatement() throws SQLException {
		if(null == monthRequestsStatement) {
			monthRequestsStatement = getConnection().prepareStatement(
					"SELECT kc.id AS client_id, kc.name AS client_name, cr.id AS request_id, cr.request_time, ca.name AS token_name, cr.response_time, cr.client_auth_id, cr.source_ip, cr.request_url, cr.request_type_id, cr.response_status, cr.error, cr.error_message, cr.created " +
					"FROM client_request cr " +
					"JOIN client_auth ca ON cr.client_auth_id = ca.id " +
					"JOIN kbs_client kc ON ca.client_id = kc.id " + 
					"WHERE kc.client_id=? AND request_time BETWEEN ? AND ?");
		}
		return monthRequestsStatement;
	}
	
	private PreparedStatement getSuccessfulRequestsCountStatement() throws SQLException {
		if(null == successfulRequestsCountStatement) {
			successfulRequestsCountStatement = getConnection().prepareStatement(
					"SELECT COUNT(cr.id) AS count FROM client_request cr JOIN client_auth ca ON cr.client_auth_id = ca.id JOIN kbs_client kc ON ca.client_id = kc.id WHERE kc.id=? AND request_time BETWEEN ? AND ? AND response_status=200"
					);
		}
		return successfulRequestsCountStatement;
	}
	
	private PreparedStatement getClientNameByIdStatement() throws SQLException {
		if(null == clientByIdStatement) {
			clientByIdStatement = getConnection().prepareStatement(
					"SELECT name FROM kbs_client WHERE id=?;"
					);
		}
		return clientByIdStatement;
	}
}
