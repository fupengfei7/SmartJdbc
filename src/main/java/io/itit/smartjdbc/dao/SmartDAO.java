package io.itit.smartjdbc.dao;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryTerms;
import io.itit.smartjdbc.ResultSetHandler;
import io.itit.smartjdbc.SmartJdbcConfig;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryDefine.OrderBy;
import io.itit.smartjdbc.annotations.QueryField.WhereSql;
import io.itit.smartjdbc.util.IOUtil;
import io.itit.smartjdbc.util.JSONUtil;
import io.itit.smartjdbc.util.QueryUtil;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 */
public class SmartDAO extends BaseDAO{
	//
	private static Logger logger=LoggerFactory.getLogger(SmartDAO.class);
	//
	//
	public int insert(Object o,boolean withGenerateKey,String... excludeProperties){
		beforeInsert(o, withGenerateKey, excludeProperties);
		SqlBean sqlBean=SqlProvider.createInsertSql(o, excludeProperties);
		String sql=sqlBean.sql;
		Object[] parameters=sqlBean.parameters;
		int result=0;
		if(withGenerateKey){
			result=executeWithGenKey(sql,true,parameters);		
		}else{
			executeUpdate(sql,parameters);
		}
		afterInsert(result, o, withGenerateKey, excludeProperties);
		return result;
	}

	/**
	 * 
	 * @param o
	 * @param withGenerateKey
	 * @param excludeProperties
	 */
	protected void beforeInsert(Object o, boolean withGenerateKey, String[] excludeProperties) {
		List<DAOInterceptor> interceptors=SmartJdbcConfig.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeInsert(o, withGenerateKey, excludeProperties);
			}
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param o
	 * @param withGenerateKey
	 * @param excludeProperties
	 */
	protected void afterInsert(int result, Object o, boolean withGenerateKey, String[] excludeProperties) {
		List<DAOInterceptor> interceptors=SmartJdbcConfig.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterInsert(result,o, withGenerateKey, excludeProperties);
			}
		}
	}
	
	/**
	 * 
	 * @param bean
	 * @param excludeProperties
	 * @return
	 */
	public int update(Object bean,
			String... excludeProperties){
		return update(bean,false,excludeProperties);
	}
	//
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeProperties
	 * @return
	 */
	public int update(Object bean,
			boolean excludeNull,
			String... excludeProperties){
		beforeUpdate(bean,excludeNull,excludeProperties);
		SqlBean sqlBean=SqlProvider.createUpdateSql(bean, excludeNull, excludeProperties);
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		afterUpdate(result,bean,excludeNull,excludeProperties);
		return result;
	}
	
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeProperties
	 */
	protected void beforeUpdate(Object bean, boolean excludeNull, String[] excludeProperties) {
		List<DAOInterceptor> interceptors=SmartJdbcConfig.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeUpdate(bean, excludeNull, excludeProperties);
			}
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param bean
	 * @param excludeNull
	 * @param excludeProperties
	 */
	protected void afterUpdate(int result, Object bean, boolean excludeNull, String[] excludeProperties) {
		List<DAOInterceptor> interceptors=SmartJdbcConfig.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterUpdate(result,bean, excludeNull, excludeProperties);
			}
		}
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @return
	 */
	public int delete(Class<?> domainClass,QueryTerms qt){
		beforeDelete(domainClass,qt);
		SqlBean sqlBean=SqlProvider.createDeleteSql(domainClass, qt);
		int result=executeUpdate(sqlBean.sql,sqlBean.parameters);
		afterDelete(result,domainClass,qt);
		return result;
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 */
	protected void beforeDelete(Class<?> domainClass, QueryTerms qt) {
		List<DAOInterceptor> interceptors=SmartJdbcConfig.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.beforeDelete(domainClass, qt);
			}
		}
	}

	/**
	 * 
	 * @param result
	 * @param domainClass
	 * @param qt
	 */
	protected void afterDelete(int result, Class<?> domainClass, QueryTerms qt) {
		List<DAOInterceptor> interceptors=SmartJdbcConfig.getDaoInterceptors();
		if(interceptors!=null) {
			for (DAOInterceptor interceptor : interceptors) {
				interceptor.afterDelete(result,domainClass, qt);
			}
		}
	}

	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @return
	 */
	public int queryCount(Class<?> domainClass,QueryTerms qt){
		SqlBean sqlBean=SqlProvider.queryCountSql(domainClass, qt);
		return queryForInteger(sqlBean.sql, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @param excludeProperties
	 * @return
	 */
	public <T> T query(Class<T> domainClass,QueryTerms qt,String... excludeProperties){
		SqlBean sqlBean=SqlProvider.createQuerySql(domainClass, qt, excludeProperties);
		return queryForObject(sqlBean.sql,new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=domainClass.newInstance();
				convertBean(o, row, excludeProperties);
				return o;
			}			
		}, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param query
	 * @return
	 */
	public <T> T query(Class<T> domainClass,Query query){
		SqlBean sqlBean=SqlProvider.createQuerySql(domainClass,query,false);
		return queryForObject(sqlBean.sql,new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=domainClass.newInstance();
				convertBean(o,row);
				return o;
			}			
		}, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param query
	 * @return
	 */
	public int queryCount(Class<?> domainClass,Query query){
		SqlBean sqlBean=SqlProvider.createQueryCountSql(domainClass, query);
		return queryForInteger(sqlBean.sql, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @param excludeProperties
	 * @return
	 */
	public <T> List<T>queryList(Class<T> domainClass,QueryTerms qt,String... excludeProperties){
		SqlBean sqlBean=SqlProvider.createQuerySql(domainClass, qt, excludeProperties);
		return queryForList(sqlBean.sql,new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=domainClass.newInstance();
				convertBean(o, row, excludeProperties);
				return o;
			}			
		}, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param query
	 * @return
	 */
	public <T> List<T>queryList(Class<T> domainClass,Query query){
		SqlBean sqlBean=SqlProvider.createQuerySql(domainClass,query,true);
		return queryForList(sqlBean.sql,new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=domainClass.newInstance();
				convertBean(o, row);
				return o;
			}			
		}, sqlBean.parameters);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public <T> List<T> queryList(Query query){
		QueryTerms qt=createQueryTerms(query,true);
		return queryList(getDomainClass(query),qt);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public int queryListCount(Query query) {
		QueryTerms qt=createQueryTerms(query,false);
		return queryCount(getDomainClass(query),qt);
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> Class<T> getDomainClass(Query query) {
		QueryDefine queryDefine=query.getClass().getAnnotation(QueryDefine.class);
		if(queryDefine==null||queryDefine.domainClass()==null) {
			throw new SmartJdbcException("no domainClass found in QueryClass["+query.getClass().getName()+"]");
		}
		return (Class<T>) queryDefine.domainClass();
	}
	
	/**
	 * 
	 * @param query
	 * @param needPaging
	 * @return
	 */
	protected QueryTerms createQueryTerms(Query query,boolean needPaging) {
		QueryTerms qt = QueryTerms.create();
		addConditions(qt,query);
		if(needPaging) {//需要分页
			addPaging(qt, query);
		}
		return qt;
	}
	
	/**
	 * 
	 * @param qt
	 * @param q
	 */
	protected void addConditions(QueryTerms qt,Query q) {
		Field[] fields=q.getClass().getFields();
		for (Field field : fields) {
			try {
				if(Modifier.isStatic(field.getModifiers())||
						Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				Class<?> fieldType=field.getType();
				Object value=field.get(q);
				if(value==null||(fieldType.equals(String.class)&&StringUtil.isEmpty((String)value))) {
					continue;
				}
				
				QueryField queryFieldDefine=field.getAnnotation(QueryField.class);
				if(queryFieldDefine!=null&&queryFieldDefine.ingore()) {
					continue;
				}
				if(queryFieldDefine!=null&&queryFieldDefine.whereSql()!=null&&
						(!StringUtil.isEmpty(queryFieldDefine.whereSql().sql()))) {//whereSql check first
					WhereSql whereSql=queryFieldDefine.whereSql();
					qt.whereSql(whereSql.sql(),StringUtil.convert(whereSql.values()));
				}else {
					String dbFieldName=convertFieldName(field.getName());
					if(queryFieldDefine!=null&&(!StringUtil.isEmpty(queryFieldDefine.field()))) {
						dbFieldName=convertFieldName(queryFieldDefine.field());
					}
					String operator="";
					if(queryFieldDefine!=null&&(!StringUtil.isEmpty(queryFieldDefine.operator()))) {
						operator=queryFieldDefine.operator();
					}
					if (StringUtil.isEmpty(operator)) {
						if(fieldType.equals(String.class)) {//字符串默认like
							operator="like";
						}else if (fieldType.equals(int[].class)) {//int[] 默认 in
							int[] values=(int[])value;
							List<Integer> intList = new ArrayList<Integer>();
							for (int index = 0; index < values.length; index++){
							    intList.add(values[index]);
							}
							QueryUtil.addInCondition(qt, dbFieldName,true,intList);
							continue;
						}else {
							operator="=";
						}
					}
					qt.where(dbFieldName,operator,value);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new SmartJdbcException(e.getMessage());
			}
		}
	}
	
	/**
	 * 
	 * @param qt
	 * @param query
	 */
	public void addPaging(QueryTerms qt,Query query) {
		QueryDefine queryDefine=query.getClass().getAnnotation(QueryDefine.class);
		if(queryDefine!=null&&queryDefine.orderBys()!=null) {
			for (OrderBy orderBy : queryDefine.orderBys()) {
				if (query.orderType != null&& query.orderType == orderBy.orderType()) {
					qt.orderBy(orderBy.sql());
				}
			}
		}
		qt.limit(query.getStartPageIndex(), query.pageSize);
	}
	
	/**
	 * 
	 * @param type
	 * @param rs
	 * @return
	 */
	public <T> T convertBean(Class<T> type,ResultSet rs){
		try{
			T instance=type.newInstance();
			convertBean(instance,rs);
			return instance;
		}catch(Exception e){
			throw new SmartJdbcException(e);
		}
	} 
	//
	/**
	 * 
	 * @param o
	 * @param rs
	 * @param excludeProperties
	 * @throws Exception
	 */
	public void convertBean(Object o, ResultSet rs, String... excludeProperties)
			throws Exception {
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeProperties) {
			excludesNames.add(e);
		}
		Class<?> type = o.getClass();
		SqlProvider.checkExcludeProperties(excludeProperties,type);
		for (Field f : type.getFields()) {
			if (excludesNames.contains(f.getName())) {
				continue;
			}
			String fieldName = convertFieldName(f.getName());
			Class<?> fieldType = f.getType();
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			Object value = null;
			if (fieldType.equals(String.class)) {
				value = rs.getString(fieldName);
			} else if (fieldType.equals(Integer.class)
					|| fieldType.equals(int.class)) {
				value = rs.getInt(fieldName);
			} else if (fieldType.equals(Short.class)
					|| fieldType.equals(short.class)) {
				value = rs.getShort(fieldName);
			} else if (fieldType.equals(Long.class)
					|| fieldType.equals(long.class)) {
				value = rs.getLong(fieldName);
			} else if (fieldType.equals(Double.class)
					|| fieldType.equals(double.class)) {
				value = rs.getDouble(fieldName);
			} else if (fieldType.equals(Float.class)
					|| fieldType.equals(float.class)) {
				value = rs.getFloat(fieldName);
			} else if (fieldType.equals(Date.class)) {
				value = rs.getTimestamp(fieldName);
			} else if (fieldType.equals(Boolean.class)
					|| fieldType.equals(boolean.class)) {
				value = rs.getBoolean(fieldName);
			} else if (fieldType.equals(BigDecimal.class)) {
				value = rs.getBigDecimal(fieldName);
			}  else if (fieldType.equals(byte[].class)) {
				Blob bb = rs.getBlob(fieldName);
				if (bb != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					IOUtil.copy(bb.getBinaryStream(), bos);
					value = bos.toByteArray();
				}
			} else {
				String strValue=rs.getString(fieldName);
				if(strValue!=null){
					Type genericType=f.getGenericType();
					if ( genericType instanceof ParameterizedType ) {  
						 Type[] typeArguments = ((ParameterizedType)genericType).getActualTypeArguments();  
						 if(typeArguments.length==1) {
							 if(List.class.isAssignableFrom(fieldType) && (typeArguments[0] instanceof Class)) {
								 value=JSONUtil.fromJsonList(strValue,(Class<?>) typeArguments[0]);
							 }
						 }
					 }else {
						 value=JSONUtil.fromJson(strValue,fieldType);
					 }
				}
			}
			f.setAccessible(true);
			if (value != null) {
				f.set(o, value);
			}
		}
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected  String convertFieldName(String name) {
		return SmartJdbcConfig.convertFieldName(name);
	}
	
	/**
	 * 
	 * @param sql
	 * @param rowHandler
	 * @param parameters
	 * @return
	 */
	public <T> List<T> queryList(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		return queryForList(sql, rowHandler, parameters);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public <T> List<T> queryList(
			Class<T> domainClass,
			String sql,
			Object... parameters) {
		return queryForList(sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=domainClass.newInstance();
				convertBean(o,row);
				return o;
			}}, parameters);
	}
	/**
	 * 
	 * @param domainClass
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public final <T> T queryObject(
			Class<T> domainClass,
			String sql,
			Object... parameters) {
		return queryForObject(sql, new ResultSetHandler<T>() {
			@Override
			public T handleRow(ResultSet row) throws Exception {
				T o=domainClass.newInstance();
				convertBean(o,row);
				return o;
			}}, parameters);
	}
	
	/**
	 * 
	 * @param sql
	 * @param rowHandler
	 * @param parameters
	 * @return
	 */
	public final <T> T queryObject(
			String sql,
			ResultSetHandler<T> rowHandler, 
			Object... parameters) {
		return queryForObject(sql, rowHandler, parameters);
	}
	
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public final Integer queryInteger(String sql,Object ...parameters){
		return  queryForInteger(sql, parameters);
	}
	
	/**
	 * 
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public final int executeForUpdate(String sql,Object... parameters) {
		return executeUpdate(sql, parameters);
	}
}
