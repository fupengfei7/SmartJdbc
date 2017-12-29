package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.util.JSONUtil;

/**
 * 
 * @author skydu
 *
 */
public class UpdateProvider extends SqlProvider{
	//
	Object bean;
	String[] excludeProperties;
	boolean excludeNull;
	//
	public UpdateProvider(Object bean,boolean excludeNull,String ... excludeProperties) {
		this.bean=bean;
		this.excludeNull=excludeNull;
		this.excludeProperties=excludeProperties;
	}
	//
	@Override
	public SqlBean build() {
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
		List<Field> primaryKey=getPrimaryKey(bean.getClass());
		QueryWhere qt=QueryWhere.create();
		for (Field field : primaryKey) {
			qt.where(convertFieldName(field.getName()),getFieldValue(bean, field.getName()));
		}
		sql.append(qt.whereStatement());
		for(Object o:qt.whereValues()){
			fieldList.add(o);
		}
		return createSqlBean(sql.toString(), fieldList.toArray(new Object[fieldList.size()]));
	}

}
