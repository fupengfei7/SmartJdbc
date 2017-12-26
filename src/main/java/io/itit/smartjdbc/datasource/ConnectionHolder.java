package io.itit.smartjdbc.datasource;

import java.sql.Connection;

/**
 * 
 * @author skydu
 *
 */
public class ConnectionHolder {
	/***/
	public Connection connection;
	
	public boolean useTransaction;
}
