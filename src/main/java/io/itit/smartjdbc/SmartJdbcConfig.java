package io.itit.smartjdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.sql.DataSource;

/**
 * 
 * @author skydu
 *
 */
public class SmartJdbcConfig {

	/**
	 * 
	 */
	private static List<DataSource> dataSources=new ArrayList<>();
	
	/**
	 * 
	 */
	private static List<DAOInterceptor> daoInterceptors=new ArrayList<>();
	//
	/**
	 * 
	 */
	public static void setup() {
		
	}
	/**
	 * default domainClass's name
	 */
	private static Function<Class<?>,String> tableNameFunc=(domainClass)->{
		return domainClass.getSimpleName();
	};
	
	/**
	 * javaFieldName->dbName
	 */
	private static Function<String,String> convertFieldNameFunc=(name)->{
		return name;	
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
		SmartJdbcConfig.tableNameFunc = tableNameFunc;
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
		SmartJdbcConfig.convertFieldNameFunc = convertFieldNameFunc;
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
		SmartJdbcConfig.daoInterceptors = daoInterceptors;
	}
	
	/**
	 * @return the dataSources
	 */
	public static List<DataSource> getDataSources() {
		return dataSources;
	}
	/**
	 * @param dataSources the dataSources to set
	 */
	public static void setDataSources(List<DataSource> dataSources) {
		SmartJdbcConfig.dataSources = dataSources;
	}
	
	/**
	 * 
	 * @param dataSource
	 */
	public static void addDataSource(DataSource dataSource) {
		dataSources.add(dataSource);
	}
	
	/**
	 * 
	 * @param interceptor
	 */
	public static void addDAOInterceptor(DAOInterceptor interceptor) {
		daoInterceptors.add(interceptor);
	}
}
