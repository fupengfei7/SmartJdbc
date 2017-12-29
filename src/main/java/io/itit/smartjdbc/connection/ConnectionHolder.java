package io.itit.smartjdbc.connection;

import java.sql.Connection;

/**
 * 
 * @author skydu
 *
 */
public class ConnectionHolder {
	//
	private Connection connection;
	//
	private boolean useTransaction;
	//
	/**
	 * @return the useTransaction
	 */
	public boolean isUseTransaction() {
		return useTransaction;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @param useTransaction the useTransaction to set
	 */
	public void setUseTransaction(boolean useTransaction) {
		this.useTransaction = useTransaction;
	}
}
