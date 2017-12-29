package io.itit.smartjdbc.provider;

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
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.QueryWhere.Where;
import io.itit.smartjdbc.SmartJdbcException;
import io.itit.smartjdbc.SqlBean;
import io.itit.smartjdbc.annotations.DomainField;
import io.itit.smartjdbc.annotations.ForeignKey;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.NonPersistent;
import io.itit.smartjdbc.annotations.OrderBys;
import io.itit.smartjdbc.annotations.OrderBys.OrderBy;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryField.WhereSql;
import io.itit.smartjdbc.util.DumpUtil;
import io.itit.smartjdbc.util.StringUtil;

/**
 * 
 * @author skydu
 *
 */
public class SelectProvider extends SqlProvider{
	//
	private static Logger logger=LoggerFactory.getLogger(SelectProvider.class);
	//
	private static final String DEFAULT_MAIN_TABLE_ALIAS="a";
	//
	public static class SelectField{//select tableAlias.field as asField
		public String tableAlias;
		public String field;
		public String asField;
		public boolean distinct;
		public String statFunction;
	}
	//
	public static class GroupByField{
		public String tableAlias;
		public String field;
	}
	//
	public static class Join{
		public String key;
		public String table1Alias;
		public Class<?> table1;
		public String table1Field;
		public String table2Alias;
		public Class<?> table2;
		public String table2Field;
		public List<Join> joins;
		//
		public Join() {
			joins=new ArrayList<>();
		}
	}
	//
	Class<?> domainClass;
	Query query;
	boolean isSelectCount;
	boolean needPaging;
	boolean needOrderBy;
	boolean isForUpdate;
	List<SelectField> selectFields;
	Map<String,Join> innerJoins;//inner join tableName alias on 
	List<Join> leftJoins;//left join tableName alias on 
	QueryWhere qw;
	List<GroupByField> groupBys;
	//
	public SelectProvider(Class<?> domainClass) {
		this.domainClass=domainClass;
		this.selectFields=new ArrayList<>();
		this.qw=new QueryWhere();
		this.groupBys=new ArrayList<>();
		this.leftJoins=new ArrayList<>();
		this.needOrderBy=true;
	}
	//
	public SelectProvider selectCount() {
		this.isSelectCount=true;
		return this;
	}
	//
	public SelectProvider sum(String sumField) {
		sum(DEFAULT_MAIN_TABLE_ALIAS, sumField, sumField);
		return this;
	}
	//
	public SelectProvider sum(String alias,String field,String asField) {
		select(alias, field, asField, false, "sum");
		return this;
	}
	//
	public SelectProvider needPaging(boolean needPaging) {
		this.needPaging=needPaging;
		return this;
	}
	//
	public SelectProvider needOrderBy(boolean needOrderBy) {
		this.needOrderBy=needOrderBy;
		return this;
	}
	//
	public SelectProvider query(Query query) {
		this.query=query;
		return this;
	}
	//
	public SelectProvider query(QueryWhere queryWhere) {
		this.qw=queryWhere;
		return this;
	}
	//
	public SelectProvider select(String field) {
		return select(null, field, null, false,null);
	}
	//
	public SelectProvider select(String tableAlias,String field) {
		return select(tableAlias, field, null, false, null);
	}
	//
	public SelectProvider select(String tableAlias,String field,String asAlias) {
		return select(tableAlias, field, asAlias, false, null);
	}
	//
	public SelectProvider select(String tableAlias,String field,String asField,
			boolean distinct,String statFunction) {
		selectFields.add(createSelectField(tableAlias, field, asField, distinct, statFunction));
		return this;
	}
	//
	private SelectField createSelectField(String tableAlias,String field,String asField,boolean distinct,String statFunction) {
		SelectField sf=new SelectField();
		sf.tableAlias=tableAlias;
		sf.field=field;
		sf.asField=asField;
		sf.distinct=distinct;
		sf.statFunction=statFunction;
		return sf;
	}
	//
	public SelectProvider where(String alias,String key,Object value){
		return this.where(alias,key, "=", value);
	}
	//
	public SelectProvider where(String key,Object value){
		return this.where(DEFAULT_MAIN_TABLE_ALIAS,key, "=", value);
	}
	//
	public SelectProvider where(String alias,String key,String op,Object value){
		Where w=new Where();
		w.alias=alias;
		w.key=key;
		w.operator=op;
		w.value=value;
		qw.wheres.add(w);
		return this;
	}
	//
	public SelectProvider whereSql(String sql,Object ...values){
		Where w=new Where();
		w.sql=sql;
		for(int i=0;i<values.length;i++){
			w.sqlValues.add(values[i]);
		}
		qw.wheres.add(w);
		return this;
	}
	//
	protected Object[] whereValues(){
		List<Object>ret=new LinkedList<Object>();
		for(Where w:qw.wheres){
			if(w.key!=null){
				ret.add(w.value);
			}else{
				ret.addAll(w.sqlValues);
			}
		}
		return ret.toArray();
	}
	//
	public SelectProvider groupBy(String field) {
		groupBy(DEFAULT_MAIN_TABLE_ALIAS, field);
		return this;
	}
	//
	public SelectProvider groupBy(String tableAlias,String field) {
		groupBys.add(createGroupByField(tableAlias, field));
		return this;
	}
	//
	private GroupByField createGroupByField(String tableAlias,String field) {
		GroupByField groupByField=new GroupByField();
		groupByField.tableAlias=tableAlias;
		groupByField.field=field;
		return groupByField;
	}
	//
	public SelectProvider orderBy(String orderBy){
		qw.orderBy=orderBy;
		return this;
	}
	//
	public SelectProvider limit(int start,int limit){
		qw.limitStart=start;
		qw.limitEnd=limit;
		return this;
	}
	//
	public SelectProvider limit(int end){
		qw.limitStart=0;
		qw.limitEnd=end;
		return this;
	}
	//
	public SelectProvider forUpdate(){
		this.isForUpdate=true;
		return this;
	}
	//
	protected List<Field> getQueryFields(Query query){
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
	protected Map<String, Join> getInnerJoins(Query query) {
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
	protected void addWheres(Query q) {
		if(q==null) {
			return;
		}
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
	protected SelectProvider addInCondition(String alias,String field, boolean withFieldEscape,
			List<? extends Object> values) {
		Object[] valueArray = null;
		if (values != null) {
			valueArray = values.toArray();
		}
		return addInCondition(alias,field, withFieldEscape, valueArray);
	}

	//
	protected SelectProvider addInCondition(String alias,String field, boolean withFieldEscape,
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
	protected static Object[] convert(String[] values) {
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
	protected void addOrderBy(Query query) {
		if(query==null) {
			return;
		}
		OrderBys orderBys=query.getClass().getAnnotation(OrderBys.class);
		if(orderBys!=null&&orderBys.orderBys()!=null) {
			for (OrderBy orderBy : orderBys.orderBys()) {
				if (query.orderType != null&& query.orderType == orderBy.orderType()) {
					orderBy(orderBy.sql());
				}
			}
		}
	}
	//
	protected void addPaging(Query query) {
		if(query==null) {
			return;
		}
		this.limit(query.getStartPageIndex(),query.pageSize);
	}
	//
	private void buildSelectFields(){
		int index=1;
		Map<String, Join> map = new LinkedHashMap<>();
		Field[] fields=domainClass.getFields();
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())|| Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			NonPersistent nonPersistent= field.getAnnotation(NonPersistent.class);
			if (nonPersistent!=null) {
				continue;
			}
			DomainField domainField = field.getAnnotation(DomainField.class);
			if(domainField==null||StringUtil.isEmpty(domainField.foreignKeyFields())) {
				select(DEFAULT_MAIN_TABLE_ALIAS, field.getName());
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
				String key=id;
				if(join==null) {
					join = map.get(key);
					if(join==null) {
						join=createLeftJoin(key,table1Alias,"l"+(index++),table1, table2,id);
						map.put(key, join);
					}
				}else {
					Join childJoin=getJoin(key, join.joins);
					if(childJoin==null) {
						childJoin=createLeftJoin(key,table1Alias,"l"+(index++),table1,table2,id);
						join.joins.add(childJoin);
					}
					join=childJoin;
				}
				table1=table2;
				table1Alias=join.table2Alias;
			}
			boolean distinct=domainField.distinct();
			String statFunc=domainField.statFunc();
			if(StringUtil.isEmpty(domainField.field())) {
				select(join.table2Alias,field.getName(),null,distinct,statFunc);
			}else {
				select(join.table2Alias,domainField.field(),field.getName(),distinct,statFunc);
			}
		}
	}
	//
	private String getSinglePrimaryKey(Class<?> clazz) {
		List<Field> list=SqlProvider.getPrimaryKey(clazz);
		if(list.size()>1||list.size()==0) {
			throw new SmartJdbcException("PrimaryKey column can only be one");
		}
		return list.get(0).getName();
	}
	//
	private Join getJoin(String key,List<Join> list) {
		for (Join join : list) {
			if(join.key.equals(key)) {
				return join;
			}
		}
		return null;
	}
	//
	private Join createLeftJoin(String key,String table1Alias,String table2Alias,Class<?> table1,Class<?> table2,
			String table1Field) {
		Join join = new Join();
		join.key=key;
		join.table1Alias=table1Alias;
		join.table2Alias=table2Alias;
		join.table1=table1;
		join.table2 = table2;
		join.table1Field=table1Field;
		join.table2Field= getSinglePrimaryKey(table2);
		leftJoins.add(join);
		return join;
	}
	
	/**
	 * 
	 * @return
	 */
	protected SqlBean queryCount() {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) ");
		this.needPaging=false;
		return build(sql);
	}
	
	/**
	 * 
	 * @return
	 */
	protected SqlBean query() {
		StringBuffer sql = new StringBuffer();
		buildSelectFields();
		sql.append("select ");
		if(selectFields.size()==0) {
			throw new IllegalArgumentException("no select field found in "+domainClass.getName());
		}
		addSelectFields(sql);
		return build(sql);
	}
	
	private void addSelectFields(StringBuffer sql) {
		for (SelectField field : selectFields) {
			if(StringUtil.isEmpty(field.statFunction)) {
				sql.append(field.tableAlias).append(".`");
				sql.append(convertFieldName(field.field)).append("`");
			}else {
				sql.append(field.statFunction);
				sql.append("(");
				sql.append(field.tableAlias).append(".`");
				sql.append(convertFieldName(field.field)).append("`");
				sql.append(")");
			}
			if(field.asField!=null) {
				sql.append(" as `").append(convertFieldName(field.asField)).append("`");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
	}
	//
	private SqlBean build(StringBuffer sql) {
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
		addWheres(query);
		sql.append(" where 1=1 ");
		sql.append(qw.whereStatement());
		
		//group by
		if(groupBys.size()>0) {
			sql.append(" group by ");
			for (GroupByField field : groupBys) {
				if(!StringUtil.isEmpty(field.tableAlias)) {
					sql.append(field.tableAlias).append(".");
				}
				sql.append(convertFieldName(field.field)).append(",");
			}
			sql.deleteCharAt(sql.length()-1);
		}
		
		//order by
		if(needOrderBy) {
			addOrderBy(query);
			if (!StringUtil.isEmpty(qw.orderBy)) {
				sql.append(" order by ").append(qw.orderBy);
			}
		}
		//limit
		if(needPaging) {
			addPaging(query);
			sql.append(" limit ").append(qw.limitStart).append(",").append(qw.limitEnd);
		}
		//for update
		if(isForUpdate) {
			sql.append(" for update ");
		}
		//
		SqlBean bean=new SqlBean(sql.toString(),whereValues());
		if(logger.isDebugEnabled()) {
			logger.debug("{} \nSqlBean:{}",DumpUtil.dump(this),DumpUtil.dump(bean));
		}
		return bean;
	}
		
	public Class<?> getDomainClass() {
		return domainClass;
	}

	@Override
	public SqlBean build() {
		if(isSelectCount) {
			return queryCount();
		}
		return query();
	}
}
