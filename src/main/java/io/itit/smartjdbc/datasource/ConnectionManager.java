package io.itit.smartjdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.util.JdbcUtil;

/**
 * 
 * @author skydu
 *
 */
public class ConnectionManager {
	//
	private static final Logger logger=LoggerFactory.getLogger(ConnectionManager.class);
	/**
	 * 
	 */
	private static DataSource dataSource;
	/**
	 * 
	 */
	private static ThreadLocal<ConnectionHolder> connectionHolder = new ThreadLocal<>();
	//
	private static Integer transactionIsolationLevel;
	/**
	 * 
	 * @param useTransaction
	 */
	public static void startTransaction(boolean useTransaction){
		ConnectionHolder holder=new ConnectionHolder();
		holder.useTransaction=useTransaction;
		connectionHolder.set(holder);
	}
	
	/**
	 * 
	 */
	public static void endTransaction() {
		ConnectionHolder holder=connectionHolder.get();
		connectionHolder.set(null);
		if(holder==null){
			return;
		}
		JdbcUtil.close(holder.connection);
	}
	
	/**
	 * 
	 */
	public  static void commit() {
		ConnectionHolder holder=connectionHolder.get();
		if(holder==null){
			return;
		}
		try {
			if(holder.connection!=null&&holder.useTransaction){
				holder.connection.commit();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 */
	public  static void rollback() {
		ConnectionHolder holder=connectionHolder.get();
		if(holder==null){
			return;
		}
		try {
			if(holder.connection!=null&&holder.useTransaction){
				holder.connection.rollback();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		ConnectionHolder holder = connectionHolder.get();
		if(holder==null){
			throw new SmartJdbcException("startTransaction first");
		}
		if(holder.connection==null) {
			try {
				Connection conn=openConnection();
				if(conn.getAutoCommit()==holder.useTransaction) {
					conn.setAutoCommit(!holder.useTransaction);
				}
				holder.connection=conn;
			} catch (Exception e) {
				throw new SmartJdbcException(e.getMessage(), e);
			}
		}
		return holder.connection;
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	private static Connection openConnection() throws SQLException {
		if(dataSource==null){
			throw new SmartJdbcException("DataSource not found");
		}
		Connection conn=dataSource.getConnection();
		if(transactionIsolationLevel!=null) {
			conn.setTransactionIsolation(transactionIsolationLevel);
		}
		return conn;
	}
	//

	/**
	 * @return the dataSource
	 */
	public static DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public static void setDataSource(DataSource dataSource) {
		ConnectionManager.dataSource = dataSource;
	}

	/**
	 * @return the transactionIsolationLevel
	 */
	public static Integer getTransactionIsolationLevel() {
		return transactionIsolationLevel;
	}

	/**
	 * @param transactionIsolationLevel the transactionIsolationLevel to set
	 */
	public static void setTransactionIsolationLevel(Integer transactionIsolationLevel) {
		ConnectionManager.transactionIsolationLevel = transactionIsolationLevel;
	}
	
}
