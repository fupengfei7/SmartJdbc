package io.itit.smartjdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import io.itit.smartjdbc.connection.ConnectionManager;
import io.itit.smartjdbc.connection.TransactionManager;

/**
 * 
 * @author skydu
 *
 */
public class Config {
	//
	public static final String DEFAULT_DATASOURCE_INDEX="master";
	/**
	 * 
	 */
	private static Map<String,DataSource> dataSources=new HashMap<>();
	/**
	 * 
	 */
	private static List<DAOInterceptor> daoInterceptors=new ArrayList<>();
	/**
	 * 
	 */
	private static List<SqlInterceptor> sqlInterceptors=new ArrayList<>();
	//
	/**
	 * default domainClass's name
	 */
	private static Function<Class<?>,String> tableNameFunc=(domainClass)->{
		return domainClass.getSimpleName();
	};
	
	/**
	 * javaFieldName->dbName
	 */
	private static Function<String,String> convertFieldNameFunc=(fieldName)->{
		return fieldName;	
	};
	//
	public static String getTableName(Class<?> domainClass) {
		return tableNameFunc.apply(domainClass);
	}
	//
	/**
	 * @return the tableNameFunc
	 */
	public static Function<Class<?>, String> getTableNameFunc() {
		return tableNameFunc;
	}
	/**
	 * @param tableNameFunc the tableNameFunc to set
	 */
	public static void setTableNameFunc(Function<Class<?>, String> tableNameFunc) {
		Config.tableNameFunc = tableNameFunc;
	}
	/**
	 * @return the convertFieldNameFunc
	 */
	public static Function<String, String> getConvertFieldNameFunc() {
		return convertFieldNameFunc;
	}
	/**
	 * @param convertFieldNameFunc the convertFieldNameFunc to set
	 */
	public static void setConvertFieldNameFunc(Function<String, String> convertFieldNameFunc) {
		Config.convertFieldNameFunc = convertFieldNameFunc;
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String convertFieldName(String name) {
		return convertFieldNameFunc.apply(name);
	}
	
	/**
	 * @return the daoInterceptors
	 */
	public static List<DAOInterceptor> getDaoInterceptors() {
		return daoInterceptors;
	}
	
	/**
	 * @param daoInterceptors the daoInterceptors to set
	 */
	public static void setDaoInterceptors(List<DAOInterceptor> daoInterceptors) {
		Config.daoInterceptors = daoInterceptors;
	}
	
	/**
	 * 
	 * @param interceptor
	 */
	public static void addDAOInterceptor(DAOInterceptor interceptor) {
		daoInterceptors.add(interceptor);
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<String, DataSource> getDataSources() {
		return dataSources;
	}
	
	/**
	 * 
	 * @param dataSource
	 */
	public static void addDataSource(DataSource dataSource) {
		dataSources.put(DEFAULT_DATASOURCE_INDEX,dataSource);
	}
	/**
	 * 
	 * @param dataSourceIndex
	 * @param dataSource
	 */
	public static void addDataSource(String dataSourceIndex,DataSource dataSource) {
		dataSources.put(dataSourceIndex,dataSource);
	}
	/**
	 * 
	 * @param transactionManager
	 */
	public static void setTransactionManager(TransactionManager transactionManager) {
		ConnectionManager.setTransactionManager(transactionManager);
	}
	//
	/**
	 * @return the sqlInterceptors
	 */
	public static List<SqlInterceptor> getSqlInterceptors() {
		return sqlInterceptors;
	}
	/**
	 * @param sqlInterceptors the sqlInterceptors to set
	 */
	public static void setSqlInterceptors(List<SqlInterceptor> sqlInterceptors) {
		Config.sqlInterceptors = sqlInterceptors;
	}
	/**
	 * 
	 * @param sqlInterceptor
	 */
	public static void addSqlInterceptor(SqlInterceptor sqlInterceptor) {
		sqlInterceptors.add(sqlInterceptor);
	}
}
