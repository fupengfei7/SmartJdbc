package io.itit.smartjdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.connection.ConnectionHolder;
import io.itit.smartjdbc.util.JdbcUtil;

/**
 * 
 * @author skydu
 *
 */
public class ConnectionManager {
	//
	private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
	//
	private static ThreadLocal<ConnectionHolder> connectionHolder = new ThreadLocal<>();
	/**
	 * 
	 * @param useTransaction
	 */
	public static void startTransaction(boolean useTransaction) {
		if (logger.isDebugEnabled()) {
			logger.debug("startTransaction useTransaction:{}", useTransaction);
		}
		ConnectionHolder holder = new ConnectionHolder();
		holder.setUseTransaction(useTransaction);
		connectionHolder.set(holder);
	}

	/**
	 * 
	 */
	public static void commit() {
		ConnectionHolder holder = connectionHolder.get();
		connectionHolder.set(null);
		if (holder == null || holder.getConnection() == null) {
			return;
		}
		try {
			if (holder.isUseTransaction()) {// use Transaction
				try {
					holder.getConnection().commit();
				} catch (SQLException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		} finally {
			JdbcUtil.close(holder.getConnection());
		}
	}

	/**
	 * 
	 */
	public static void rollback() {
		ConnectionHolder holder = connectionHolder.get();
		connectionHolder.set(null);
		if (holder == null || holder.getConnection() == null) {
			return;
		}
		try {
			if (holder.isUseTransaction()) {// use Transaction
				try {
					holder.getConnection().rollback();
				} catch (SQLException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}

		} finally {
			JdbcUtil.close(holder.getConnection());
		}
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection openConnection(String dataSourceIndex) throws SQLException {
		if(dataSourceIndex==null) {
			dataSourceIndex=Config.DEFAULT_DATASOURCE_INDEX;
		}
		DataSource dataSource=Config.getDataSources().get(dataSourceIndex);
		if(dataSource==null) {
			throw new RuntimeException("DataSource not found with index "+dataSourceIndex);
		}
		return dataSource.getConnection();
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnecton(String datasourceIndex) throws SQLException {
		ConnectionHolder holder = connectionHolder.get();
		if (holder == null) {
			throw new SmartJdbcException("Connection not found");
		}
		Connection conn=holder.getConnection();
		if(conn==null) {
			conn=openConnection(datasourceIndex);
			if(holder.isUseTransaction()==conn.getAutoCommit()) {
				conn.setAutoCommit(!holder.isUseTransaction());
			}
			holder.setConnection(conn);
		}
		return conn;
	}
}
