package io.itit.smartjdbc.connection;

import java.sql.Connection;

import io.itit.smartjdbc.SmartJdbcException;

/**
 * 
 * @author skydu
 *
 */
public class ConnectionManager {
	//
	private static TransactionManager transactionManager=new DefaultTransactionManager();
	//
	/**
	 * @return the transactionManager
	 */
	public static TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public static void setTransactionManager(TransactionManager transactionManager) {
		ConnectionManager.transactionManager = transactionManager;
	}
	/**
	 * 
	 * @param datasourceIndex
	 * @return
	 */
	public static Connection getConnecton(String datasourceIndex) {
		if(transactionManager==null) {
			throw new SmartJdbcException("transactionManager is null");
		}
		return transactionManager.getConnecton(datasourceIndex);
	}

	/**
	 * 
	 */
	public static void commit() {
		transactionManager.commit();
	}
	
	/**
	 * 
	 */
	public static void rollback() {
		transactionManager.rollback();
	}

}
