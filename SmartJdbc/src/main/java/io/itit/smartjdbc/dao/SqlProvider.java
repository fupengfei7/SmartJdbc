package io.itit.smartjdbc.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryTerms;
import io.itit.smartjdbc.SmartJdbcConfig;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.annotations.NonPersistent;
import io.itit.smartjdbc.annotations.PrimaryKey;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public class SqlProvider {
	//
	private static Logger logger=LoggerFactory.getLogger(SqlProvider.class);
	//
	private static final HashSet<Class<?>> WRAP_TYPES=new HashSet<>();
	static{
		WRAP_TYPES.add(Boolean.class);
		WRAP_TYPES.add(Character.class);
		WRAP_TYPES.add(Byte.class);
		WRAP_TYPES.add(Short.class);
		WRAP_TYPES.add(Integer.class);
		WRAP_TYPES.add(Long.class);
		WRAP_TYPES.add(BigDecimal.class);
		WRAP_TYPES.add(BigInteger.class);
		WRAP_TYPES.add(Double.class);
		WRAP_TYPES.add(Float.class);
		WRAP_TYPES.add(String.class);
		WRAP_TYPES.add(Date.class);
		WRAP_TYPES.add(Timestamp.class);
		WRAP_TYPES.add(java.sql.Date.class);
		WRAP_TYPES.add(Byte[].class);
		WRAP_TYPES.add(byte[].class);
		WRAP_TYPES.add(int.class);
		WRAP_TYPES.add(boolean.class);
		WRAP_TYPES.add(char.class);
		WRAP_TYPES.add(byte.class);
		WRAP_TYPES.add(short.class);
		WRAP_TYPES.add(int.class);
		WRAP_TYPES.add(long.class);
		WRAP_TYPES.add(float.class);
		WRAP_TYPES.add(double.class);
	}
	
	/**
	 * 
	 * @param o
	 * @param excludeProperties
	 * @return
	 */
	public static SqlBean createInsertSql(Object o,String... excludeProperties) {
		StringBuilder sql=new StringBuilder();
		Class<?>type=o.getClass();
		checkExcludeProperties(excludeProperties,type);
		String tableName=getTableName(type);
		sql.append("insert into ").append(tableName).append("(");
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeProperties) {
			excludesNames.add(e);
		}
		List<Object>fieldList=new ArrayList<Object>();
		List<Field> fl=getPersistentFields(type);
		for (Field f : fl) {
			if (excludesNames.contains(f.getName())) {
				continue;
			}
			String fieldName = convertFieldName(f.getName());
			try {
				Object fieldValue=f.get(o);
				if(fieldValue!=null&&!WRAP_TYPES.contains(fieldValue.getClass())){
					fieldList.add(JSONUtil.toJson(fieldValue));
				}else{
					fieldList.add(fieldValue);
				}
			} catch (Exception e) {
				throw new SmartJdbcException(e);
			}
			sql.append("`").append(fieldName).append("`,");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		sql.append("values(");
		for(int i=0;i<fieldList.size();i++){
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		//
		return new SqlBean(sql.toString(),fieldList.toArray(new Object[fieldList.size()]));	
	}
	
	
	/**
	 * 
	 * @param bean
	 * @param excludeNull
	 * @param excludeProperties
	 * @return
	 */
	public static SqlBean createUpdateSql(Object bean,boolean excludeNull,String... excludeProperties){
		StringBuilder sql=new StringBuilder();
		Class<?>type=bean.getClass();
		checkExcludeProperties(excludeProperties,type);
		String tableName=getTableName(type);
		sql.append("update ").append(tableName).append(" ");
		Set<String> excludesNames = new TreeSet<String>();
		for (String e : excludeProperties) {
			excludesNames.add(e);
		}
		List<Object>fieldList=new ArrayList<Object>();
		sql.append("set ");
		for (Field f : type.getFields()) {
			if (excludesNames.contains(f.getName())) {
				continue;
			}
			String fieldName = convertFieldName(f.getName());
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			try {
				Object fieldValue=f.get(bean);
				if(excludeNull&&fieldValue==null){
					continue;
				}
				if(fieldValue!=null&&!WRAP_TYPES.contains(fieldValue.getClass())){
					fieldList.add(JSONUtil.toJson(fieldValue));
				}else{
					fieldList.add(fieldValue);
				}
			} catch (Exception e) {
				throw new SmartJdbcException(e);
			}
			sql.append(" `").append(fieldName).append("`=?,");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" where 1=1");
		List<Field> primaryKey=getPrimaryKey(bean);
		QueryTerms qt=QueryTerms.create();
		for (Field field : primaryKey) {
			qt.where(convertFieldName(field.getName()),getFieldValue(bean, field.getName()));
		}
		sql.append(qt.whereStatement());
		for(Object o:qt.whereValues()){
			fieldList.add(o);
		}
		return new SqlBean(sql.toString(), fieldList.toArray(new Object[fieldList.size()]));
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @return
	 */
	public static SqlBean createDeleteSql(Class<?> domainClass,QueryTerms qt){
		StringBuilder sql=new StringBuilder();
		String tableName=getTableName(domainClass);
		sql.append("delete from ").append(tableName);
		sql.append(" where 1=1");
		sql.append(qt.whereStatement());
		return new SqlBean(sql.toString(),qt.whereValues());
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @param excludeProperties
	 * @return
	 */
	public static SqlBean createQuerySql(Class<?> domainClass,QueryTerms qt,String... excludeProperties){
		return querySql(domainClass, qt, excludeProperties) ;
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param query
	 * @param needPaging
	 * @return
	 */
	public static SqlBean createQuerySql(Class<?> domainClass,Query query,boolean needPaging){
		SelectSqlBuilder builder=new SelectSqlBuilder();
		return builder.query(domainClass, query, needPaging);
	}
	
	/**
	 * 
	 * @param domainClass
	 * @param query
	 * @return
	 */
	public static SqlBean createQueryCountSql(Class<?> domainClass,Query query){
		SelectSqlBuilder builder=new SelectSqlBuilder();
		return builder.queryCount(domainClass, query);
	}
	/**
	 * 
	 * @param domainClass
	 * @param qt
	 * @return
	 */
	public static SqlBean queryCountSql(Class<?> domainClass,QueryTerms qt){
		StringBuilder sql=new StringBuilder();
		String tableName=getTableName(domainClass);
		sql.append("select count(1) ");
		sql.append(" from ").append(tableName);
		sql.append(" where 1=1");
		sql.append(qt.whereStatement());
		if(!qt.orderBys.isEmpty()){
			sql.append(" order by ");
			for(String k:qt.orderBys){
				sql.append(k).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
		}
		if(qt.limitEnd!=-1){
			sql.append(" limit ").
			append(qt.limitStart).
			append(",").
			append(qt.limitEnd);
		}
		return new SqlBean(sql.toString(), qt.whereValues());
	}
	
	private static SqlBean querySql(Class<?> domainClass,QueryTerms qt,String... excludeProperties){
		StringBuilder sql=new StringBuilder();
		Class<?>type=domainClass.getClass();
		checkExcludeProperties(excludeProperties,type);
		String tableName=getTableName(domainClass);
		sql.append("select ");
		if(excludeProperties==null||excludeProperties.length==0){
			sql.append(" * ");
		}else{
			Set<String> excludesNames = new TreeSet<String>();
			for (String e : excludeProperties) {
				excludesNames.add(e);
			}
			for (Field f : type.getFields()) {
				if (excludesNames.contains(f.getName())) {
					continue;
				}
				String fieldName = convertFieldName(f.getName());
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}
				sql.append("`").append(fieldName).append("`").append(",");
			}
			sql.deleteCharAt(sql.length()-1);
		}
		sql.append(" from ").append(tableName);
		sql.append(" where 1=1");
		sql.append(qt.whereStatement());
		if(!qt.orderBys.isEmpty()){
			sql.append(" order by ");
			for(String k:qt.orderBys){
				sql.append(k).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
		}
		if(qt.limitEnd!=-1){
			sql.append(" limit ").
			append(qt.limitStart).
			append(",").
			append(qt.limitEnd);
		}
		return new SqlBean(sql.toString(),qt.whereValues());
	}
	
	public static String getTableName(Class<?> type) {
		return SmartJdbcConfig.getTableName(type);
	}
	
	public static String convertFieldName(String name) {
		return SmartJdbcConfig.convertFieldName(name);
	}

	public static void checkExcludeProperties(String []excludeProperties,Class<?>type){
		for(String p:excludeProperties){
			try {
				if(type.getField(p)==null){	
					return;
				}
			} catch (Exception e) {
				throw new SmartJdbcException("can not find property:"+
						p+" in type:"+type.getName());
			} 
		}
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public static List<Field> getPrimaryKey(Object o){
		List<Field> primaryKey=new ArrayList<>();
		List<Field> fields=getPersistentFields(o.getClass());
		Field idField=null;
		for (Field field : fields) {
			if(field.getAnnotation(PrimaryKey.class)!=null) {
				primaryKey.add(field);
			}
			if(field.getName().equals("id")) {
				idField=field;
			}
		}
		if(primaryKey.size()==0&&idField==null) {
			throw new SmartJdbcException("PrimaryKey not found in "+o.getClass().getName());
		}
		if(primaryKey.size()==0) {
			return Arrays.asList(idField);
		}
		return primaryKey;
	}
	
	public static List<Field> getPersistentFields(Class<?> domainClass){
		List<Field> fields=new ArrayList<>();
		for (Field field : domainClass.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			if(field.getAnnotation(NonPersistent.class)!=null) {
				continue;
			}
			fields.add(field);
		}
		return fields;
	}
	
	/**
	 * 
	 * @param bean
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue(Object bean,String fieldName){
		try {
			Field field=bean.getClass().getField(fieldName);
			if(field!=null) {
				return field.get(bean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
