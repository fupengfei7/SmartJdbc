package io.itit.smartjdbc.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.SmartJdbcConfig;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.NonPersistent;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryDefine.OrderBy;
import io.itit.smartjdbc.annotations.QueryField.WhereSql;
import io.itit.smartjdbc.util.DumpUtil;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class SelectSqlBuilder {
	//
	private static Logger logger=LoggerFactory.getLogger(SelectSqlBuilder.class);
	//
	private static final String DEFAULT_MAIN_TABLE_ALIAS="a";
	//
	public static class SelectField{//select tableAlias.field as asField
		public String tableAlias;
		public String field;
		public String asField;
		public boolean distinct;
	}
	//
	public static class Where{
		public String alias;
		public String key;
		public Object value;
		public String operator;
		public String sql;
		public LinkedList<Object> sqlValues;
		public Where() {
			sqlValues=new LinkedList<Object>();
		}
	}
	//
	public static class Join{
		public String table1Alias;
		public Class<?> table1;
		public String table1Field;
		public String table2Alias;
		public Class<?> table2;
		public String table2Field;
	}
	//
	public List<SelectField> selectFields;
	public Map<String,Join> innerJoins;//inner join tableName alias on 
	public List<Join> leftJoins;//left join tableName alias on 
	//
	public List<Where> wheres;
	public String orderBy;
	public Integer limitStart=0;
	public Integer limitEnd=-1;
	//
	public SelectSqlBuilder() {
		selectFields=new ArrayList<>();
		wheres=new ArrayList<>();
		leftJoins=new ArrayList<>();
	}
	//
	public SelectSqlBuilder select(String field) {
		return select(null, field, null, false);
	}
	//
	public SelectSqlBuilder select(String tableAlias,String field) {
		return select(tableAlias, field, null, false);
	}
	//
	public SelectSqlBuilder select(String tableAlias,String field,String asAlias) {
		return select(tableAlias, field, asAlias, false);
	}
	//
	public SelectSqlBuilder select(String tableAlias,String field,String asField,boolean distinct) {
		SelectField sf=new SelectField();
		sf.tableAlias=tableAlias;
		sf.field=field;
		sf.asField=asField;
		sf.distinct=distinct;
		selectFields.add(sf);
		return this;
	}
	//
	public SelectSqlBuilder where(String alias,String key,Object value){
		return this.where(alias,key, "=", value);
	}
	//
	public SelectSqlBuilder where(String key,Object value){
		return this.where(null,key, "=", value);
	}
	//
	public SelectSqlBuilder where(String alias,String key,String op,Object value){
		Where w=new Where();
		w.alias=alias;
		w.key=key;
		w.operator=op;
		w.value=value;
		this.wheres.add(w);
		return this;
	}
	//
	public SelectSqlBuilder whereSql(String sql,Object ...values){
		Where w=new Where();
		w.sql=sql;
		for(int i=0;i<values.length;i++){
			w.sqlValues.add(values[i]);
		}
		this.wheres.add(w);
		return this;
	}
	//
	//
	public Object[] whereValues(){
		List<Object>ret=new LinkedList<Object>();
		for(Where w:wheres){
			if(w.key!=null){
				ret.add(w.value);
			}else{
				ret.addAll(w.sqlValues);
			}
		}
		return ret.toArray();
	}
	//
	public String whereStatement(){
		StringBuilder sql=new StringBuilder();
		sql.append(" ");
		for(Where w:wheres){
			if(w.key!=null){
				sql.append(" and ");
				if(w.alias!=null) {
					sql.append(w.alias).append(".");
				}
				sql.append("`").append(w.key).append("` ");
				sql.append(w.operator).append(" ");
				if(w.operator.trim().equalsIgnoreCase("like")){
					sql.append(" concat('%',?,'%') ");
				}else{
					sql.append(" ? ");
				}
			}else{
				sql.append(" "+ w.sql+" ");
			}
		}
		sql.append(" ");
		return sql.toString();
	}
	//
	public SelectSqlBuilder orderBy(String orderBy){
		this.orderBy=orderBy;
		return this;
	}
	//
	public SelectSqlBuilder limit(int start,int limit){
		this.limitStart=start;
		this.limitEnd=limit;
		return this;
	}
	//
	public SelectSqlBuilder limit(int end){
		this.limitStart=0;
		this.limitEnd=end;
		return this;
	}
	//
	private List<Field> getQueryFields(Query query){
		List<Field> fieldList=new ArrayList<>();
		Field[] fields = query.getClass().getFields();
		QueryDefine queryDefine = query.getClass().getAnnotation(QueryDefine.class);
		if (queryDefine == null) {
			throw new IllegalArgumentException("queryDefine not found in " + query.getClass().getName());
		}
		for (Field field : fields) {
			try {
				if (Modifier.isStatic(field.getModifiers()) || 
						Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				Class<?> fieldType = field.getType();
				Object reallyValue = field.get(query);
				if (reallyValue == null
						|| (fieldType.equals(String.class) && StringUtil.isEmpty((String) reallyValue))) {
					continue;
				}
				QueryField queryField = field.getAnnotation(QueryField.class);
				if (queryField!= null && queryField.ingore()) {
					continue;
				}
				fieldList.add(field);
			}catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new IllegalArgumentException(e);
			}
		}
		return fieldList;
	}
	//
	private Map<String, Join> getInnerJoins(Query query) {
		Map<String, Join> map = new LinkedHashMap<>();
		if(query==null) {
			return map;
		}
		Class<?> queryClass=query.getClass();
		QueryDefine qd=queryClass.getAnnotation(QueryDefine.class);
		if(qd==null||qd.domainClass().equals(void.class)) {
			throw new IllegalArgumentException("domainClass not found /"+query.getClass().getSimpleName());
		}
		List<Field> fields=getQueryFields(query);
		int index = 1;
		for (Field field : fields) {
			InnerJoin innerJoin=field.getAnnotation(InnerJoin.class);
			if(innerJoin==null) {
				continue;
			}
			if(innerJoin.table2().equals(void.class)||
					StringUtil.isEmpty(innerJoin.table1Field())||
					StringUtil.isEmpty(innerJoin.table2Field())) {
				continue;
			}
			String key=innerJoin.table2().getSimpleName()+"-"+
					innerJoin.table1Field()+"-"+
					innerJoin.table2Field();
			if(map.containsKey(key)){
				continue;
			}
			Join join=new Join();
			join.table1Alias=DEFAULT_MAIN_TABLE_ALIAS;
			join.table2Alias="i"+(index++);
			join.table1Field=innerJoin.table1Field();
			join.table2Field=innerJoin.table2Field();
			join.table2=innerJoin.table2();
			map.put(key, join);
		}
		return map;
	}
	//
	/**
	 * 
	 * @param query
	 * @param needPaging
	 */
	public void addCondition(Query query,boolean needPaging) {
		addCondition(query);
		if(needPaging) {
			addPaging(query);
		}
	}
	//
	protected void addCondition(Query q) {
		List<Field> fields=getQueryFields(q);
		for (Field field : fields) {
			try {
				Class<?> fieldType = field.getType();
				Object value=field.get(q);
				QueryField queryFieldDefine=field.getAnnotation(QueryField.class);
				String alias = DEFAULT_MAIN_TABLE_ALIAS;
				InnerJoin innerJoin=field.getAnnotation(InnerJoin.class);
				if(innerJoin!=null) {
					String key=innerJoin.table2().getSimpleName()+"-"+
							innerJoin.table1Field()+"-"+
							innerJoin.table2Field();
					Join join=innerJoins.get(key);
					alias=join.table2Alias;
				}
				//
				if(queryFieldDefine!=null&&queryFieldDefine.whereSql()!=null&&(!StringUtil.isEmpty(queryFieldDefine.whereSql().sql()))) {//whereSql check first
					WhereSql whereSql=queryFieldDefine.whereSql();
					whereSql(whereSql.sql(),convert(whereSql.values()));
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
							addInCondition(alias,dbFieldName,true,intList);
							continue;
						}else {
							operator="=";
						}
					}
					where(alias,dbFieldName,operator,value);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new IllegalArgumentException(e.getMessage());
			}
		}
	}
	//
	public SelectSqlBuilder addInCondition(String alias,String field, boolean withFieldEscape,
			List<? extends Object> values) {
		Object[] valueArray = null;
		if (values != null) {
			valueArray = values.toArray();
		}
		return addInCondition(alias,field, withFieldEscape, valueArray);
	}

	//
	public SelectSqlBuilder addInCondition(String alias,String field, boolean withFieldEscape,
			Object[] values) {
		if (values == null) {
			return this;
		}
		int len = values.length;
		if (len == 0) {
			return this;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(" and ");
		if (withFieldEscape) {
			buffer.append("`");
			buffer.append(field);
			buffer.append("`");
		} else {
			buffer.append(field);
		}
		buffer.append(" in ( ");
		for (int i = 0; i < len; i++) {
			buffer.append(" ?,");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(" ) ");
		return whereSql(buffer.toString(), values);
	}
	//
	public static Object[] convert(String[] values) {
		if(values==null) {
			return null;
		}
		Object[] oValues=new Object[values.length];
		for(int i=0;i<values.length;i++) {
			oValues[i]=values[i];
		}
		return oValues;
	}
	//
	public void addPaging(Query query) {
		QueryDefine queryDefine=query.getClass().getAnnotation(QueryDefine.class);
		if(queryDefine!=null&&queryDefine.orderBys()!=null) {
			for (OrderBy orderBy : queryDefine.orderBys()) {
				if (query.orderType != null&& query.orderType == orderBy.orderType()) {
					orderBy(orderBy.sql());
				}
			}
		}
		this.limit(query.getStartPageIndex(), query.pageSize);
	}
	//
	private void buildSelectFields(Class<?> domainClass){
		int index=1;
		Map<String, Join> map = new LinkedHashMap<>();
		Field[] fields=domainClass.getFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			DomainField domainField = field.getAnnotation(DomainField.class);
			if(domainField==null) {
				select(DEFAULT_MAIN_TABLE_ALIAS, field.getName());
				continue;
			}
			NonPersistent nonPersistent= field.getAnnotation(NonPersistent.class);
			if (nonPersistent!=null) {
				continue;
			}
			if (StringUtil.isEmpty(domainField.foreignKeyFields())) {
				continue;
			}
			String foreignKeyId = domainField.foreignKeyFields();
			String[] foreignKeyIds=foreignKeyId.split(",");
			Class<?> table1=domainClass;
			String table1Alias=DEFAULT_MAIN_TABLE_ALIAS;
			Join join=null;
			for (String id : foreignKeyIds) {
				Field foreignKeyField=null;
				try {
					foreignKeyField=table1.getField(id);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new IllegalArgumentException(e.getMessage()+"/"+table1.getSimpleName());
				}
				ForeignKey foreignKey=foreignKeyField.getAnnotation(ForeignKey.class);
				if(foreignKey==null) {
					throw new IllegalArgumentException("@ForeignKey not found in "+
								domainClass.getSimpleName()+"."+foreignKeyField.getName());
				}
				Class<?> table2=foreignKey.domainClass();
				String key=table1.getSimpleName()+"."+id+"="+table2.getSimpleName()+".id";
				join = map.get(key);
				if (join ==null) {
					join = new Join();
					join.table1Alias=table1Alias;
					join.table1=table1;
					join.table2 = table2;
					join.table1Field=id;
					join.table2Field = "id";
					join.table2Alias ="l"+(index++);
					map.put(key, join);
				}
				table1=table2;
				table1Alias=join.table2Alias;
			}
			SelectField selectField=new SelectField();
			selectField.tableAlias=join.table2Alias;
			selectField.field=domainField.field();
			selectField.asField=field.getName();
			selectFields.add(selectField);
			boolean distinct=domainField.distinct();
			if(StringUtil.isEmpty(domainField.field())) {
				select(join.table2Alias,field.getName(),null,distinct);
			}else {
				select(join.table2Alias,domainField.field(),field.getName(),distinct);
			}
		}
		leftJoins=new ArrayList<>(map.values());
	}
	//
	public static String convertFieldName(String name) {
		return SmartJdbcConfig.convertFieldName(name);
	}
	//
	public static String getTableName(Class<?> domainClass) {
		DomainDefine domainDefine=domainClass.getAnnotation(DomainDefine.class);
		if (domainDefine != null && (!domainDefine.tableClass().equals(void.class))) {
			return getTableName(domainDefine.tableClass());
		}
		return SmartJdbcConfig.getTableName(domainClass);
	}
	//
	public SqlBean queryCount(Class<?> domainClass,Query query) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) ");
		return build(sql,domainClass,query,false);
	}
	//
	public SqlBean query(Class<?> domainClass,Query query,boolean needPaging) {
		StringBuffer sql = new StringBuffer();
		buildSelectFields(domainClass);
		sql.append("select ");
		if(selectFields.size()==0) {
			throw new IllegalArgumentException("no select field found in "+domainClass.getName());
		}
		for (SelectField field : selectFields) {
			sql.append(field.tableAlias).append(".`").append(convertFieldName(field.field)).append("`");
			if(field.asField!=null) {
				sql.append(" as `").append(field.asField).append("`");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		return build(sql,domainClass,query,false);
	}
	//
	public SqlBean build(StringBuffer sql,Class<?> domainClass,Query query,boolean needPaging) {
		//from
		sql.append(" from ").append(getTableName(domainClass)).append(" ").append(DEFAULT_MAIN_TABLE_ALIAS).append(" ");
		//inner join
		this.innerJoins=getInnerJoins(query);
		for (Join join : innerJoins.values()) {
			sql.append(" inner join  ");
			sql.append(getTableName(join.table2)).append(" ").append(join.table2Alias);
			sql.append(" on ").append(join.table1Alias).append(".`"+convertFieldName(join.table1Field)+"`=").
				append(join.table2Alias).append(".").append(convertFieldName(join.table2Field));
		}
		//left join
		for (Join join : leftJoins) {
			sql.append(" left join  ");
			sql.append(getTableName(join.table2)).append(" ").append(join.table2Alias);
			sql.append(" on ").append(join.table1Alias).append(".`"+convertFieldName(join.table1Field)+"`=").
				append(join.table2Alias).append(".").append(convertFieldName(join.table2Field));
		}
		//where
		addCondition(query,needPaging);
		sql.append(" where 1=1 ");
		sql.append(whereStatement());
		//order by
		if (!StringUtil.isEmpty(orderBy)) {
			sql.append(" order by ");
		}
		//limit
		if (needPaging) {
			sql.append(" limit ").append(limitStart).append(",").append(limitEnd);
		}
		//
		if(logger.isDebugEnabled()) {
			logger.debug(DumpUtil.dump(this));
		}
		return new SqlBean(sql.toString(),whereValues());
	}
}
