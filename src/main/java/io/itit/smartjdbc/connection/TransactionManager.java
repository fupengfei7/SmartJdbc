package io.itit.smartjdbc.connection;

import java.sql.Connection;

/**
 * 
 * @author skydu
 *
 */
public interface TransactionManager {

	/**
	 * 
	 * @param datasourceIndex
	 * @return
	 */
	Connection getConnecton(String datasourceIndex);
	
	/**
	 * 
	 */
	void commit();
	
	/**
	 * 
	 */
	void rollback();
}
