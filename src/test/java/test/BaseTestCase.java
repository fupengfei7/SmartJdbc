package test;

import javax.sql.DataSource;

import io.itit.smartjdbc.SmartJdbcConfig;
import io.itit.smartjdbc.datasource.DriverManagerDataSource;
import io.itit.smartjdbc.datasource.TransactionManager;
import junit.framework.TestCase;

/**
 * 
 * @author skydu
 *
 */
public abstract class BaseTestCase extends TestCase{
	//
	private static final String dbName="db_test1";
	private static final String dbHost="119.29.22.219";
	private static final String dbPort="8001";
	private static final String dbUser="db_test";
	private static final String dbPwd="db_test";
	//
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SmartJdbcConfig.addDataSource(createDriverManagerDataSource(dbName));
		TransactionManager.startTransaction(true);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TransactionManager.commit();
	}
	//
	private String getJdbcUrl(String dbName) {
		return "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName;
	}
	//
	private DataSource createDriverManagerDataSource(String dbName) throws Exception{
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setUrl(getJdbcUrl(dbName));
		dataSource.setUsername(dbUser);
		dataSource.setPassword(dbPwd);  
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		return dataSource;
	}
}
