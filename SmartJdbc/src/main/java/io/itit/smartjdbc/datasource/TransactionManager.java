package io.itit.smartjdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.SmartJdbcConfig;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.connection.Connections;
import io.itit.smartjdbc.util.JdbcUtil;

/**
 * 
 * @author skydu
 *
 */
public class TransactionManager {
	//
	private static Logger logger=LoggerFactory.getLogger(TransactionManager.class);
	//
	private static ThreadLocal<Connections> threadLocal = new ThreadLocal<>();
	//
	public static interface Callback{
		Object excute() throws Throwable;
	}
	//
	public static Connections startTransaction(boolean useTransaction){
		if(logger.isDebugEnabled()){
			logger.debug("startTransaction useTransaction:{}",useTransaction);
		}
		Connections conns=null;
		try {
			conns=getConnections();
			conns.setUseTransaction(useTransaction);
			for (Connection conn : conns.getConnections()) {
				if(conn.getAutoCommit()==useTransaction){
					conn.setAutoCommit(!useTransaction);
				}
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			for (Connection conn : conns.getConnections()) {
				if(conn!=null){
					JdbcUtil.close(conn);
				}
			}
			threadLocal.set(null);
			throw new RuntimeException("Could not open JDBC Connection for transaction",e);
		}
		return conns;
	}
	
	/**
	 * 
	 * @param useTransaction
	 * @param callback
	 * @return
	 */
	public static Object excute(boolean useTransaction,Callback callback){
		Connections conns=startTransaction(useTransaction);
		//
		try {
			Object ret=callback.excute();
			if(useTransaction){
				for (Connection conn : conns.getConnections()) {
					conn.commit();
				}
			}
			commit();
			return ret;
		} catch (Throwable e) {
			rollback();
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	/**
	 * 
	 */
	public static void commit(){
		Connections conns=threadLocal.get();
		if(conns==null){
			return;
		}
		try {
			for (Connection conn : conns.getConnections()) {
				if(conns.isUseTransaction()){//use Transaction
					try {
						conn.commit();
					} catch (SQLException e1) {
						logger.error(e1.getMessage(),e1);
					}
				}
			}
		}finally {
			for (Connection conn : conns.getConnections()) {
				JdbcUtil.close(conn);
			}
			threadLocal.set(null);
		}
	}
	
	/**
	 * 
	 */
	public static void rollback(){
		Connections conns=threadLocal.get();
		if(conns==null){
			return;
		}
		try {
			for (Connection conn : conns.getConnections()) {
				if(conns.isUseTransaction()){//use Transaction
					try {
						conn.rollback();
					} catch (SQLException e1) {
						logger.error(e1.getMessage(),e1);
					}
				}
			}
		}finally {
			for (Connection conn : conns.getConnections()) {
				JdbcUtil.close(conn);
			}
			threadLocal.set(null);
		}
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	private static List<Connection> getAllConnections() throws SQLException{
		List<Connection> list=new ArrayList<>();
		for (DataSource dataSource : SmartJdbcConfig.getDataSources()) {
			list.add(dataSource.getConnection());
		}
		return list;
		
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connections getConnections() throws SQLException {
		List<DataSource> dataSources=SmartJdbcConfig.getDataSources();
		if(dataSources==null||dataSources.isEmpty()){
			throw new RuntimeException("DataSource not found");
		}
		Connections conns =new Connections();
		conns.setConnections(getAllConnections());
		threadLocal.set(conns);
		return conns;
	}

	/**
	 * 
	 * @param index
	 * @return
	 * @throws SQLException
	 */
	public static Connection getCurrentConnecton(int index) throws SQLException {
		Connections conns = threadLocal.get();
		if(conns==null){
			throw new SmartJdbcException("Connection not found.index:"+index);
		}
		return conns.getConnection(index);
	}
}
