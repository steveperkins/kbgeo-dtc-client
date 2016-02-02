package com.kbs.geo.coastal.dao.impl;

import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

import com.kbs.geo.coastal.model.Identifiable;

public abstract class AbstractDao<T extends Identifiable> {
	protected String INSERT_SQL = null;
	protected String UPDATE_SQL = null;
	
	private PreparedStatementCreatorFactory insertStatementFactory;
	private PreparedStatementCreatorFactory updateStatementFactory;
	
	public Integer save(T obj) {
		if(null == obj.getId()) return create(obj);
		return update(obj);
	}
	
	protected abstract Integer create(T obj);
	protected abstract Integer update(T obj);
	
	protected PreparedStatementCreatorFactory getInsertStatementFactory() {
		if(null == insertStatementFactory) {
			insertStatementFactory = new PreparedStatementCreatorFactory(INSERT_SQL, getInsertParamTypes());
			insertStatementFactory.setReturnGeneratedKeys(Boolean.TRUE);
			insertStatementFactory.setGeneratedKeysColumnNames(getGeneratedKeyColumns());
		}
		return insertStatementFactory;
	}
	
	protected PreparedStatementCreatorFactory getUpdateStatementFactory() {
		if(null == updateStatementFactory) {
			updateStatementFactory = new PreparedStatementCreatorFactory(UPDATE_SQL, getUpdateParamTypes());
		}
		return updateStatementFactory;
	}

	/**
	 * Configures the columns containing generated keys after an INSERT statement is executed. Specified "id" by default. Override to use more or different columns. 
	 * @return
	 */
	protected String[] getGeneratedKeyColumns() {
		return new String[] { "id" };
	}
	protected abstract int[] getInsertParamTypes();
	protected abstract int[] getUpdateParamTypes();
	
}
