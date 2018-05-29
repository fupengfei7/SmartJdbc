package io.itit.smartjdbc;

import java.lang.reflect.Field;

import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryField.OrGroup;

/**
 * 
 * @author skydu
 *
 */
public class QueryFieldInfo {
	//
	public QueryField queryField;
	public Field field;
	public Class<?> fieldType;
	public Object value;
	public OrGroup orGroup;
}
