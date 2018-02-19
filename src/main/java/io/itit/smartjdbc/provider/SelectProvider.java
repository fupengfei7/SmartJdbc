package io.itit.smartjdbc.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import io.itit.smartjdbc.annotations.LeftJoin;
import io.itit.smartjdbc.annotations.NonPersistent;
import io.itit.smartjdbc.annotations.OrderBys;
import io.itit.smartjdbc.annotations.OrderBys.OrderBy;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.util.ArrayUtils;
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
	public static class SelectField{//select tableAlias.field as asField
		public String tableAlias;
		public String field;
		public String preAsField;
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
		this.qw=QueryWhere.create();
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
		sum(MAIN_TABLE_ALIAS, sumField, sumField);
		return this;
	}
	//
	public SelectProvider sum(String alias,String field,String asField) {
		select(alias, field,null,asField, false, "sum");
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
	public SelectProvider query(QueryWhere qw) {
		this.qw=qw;
		return this;
	}
	//
	public SelectProvider select(String field) {
		return select(null, field);
	}
	//
	public SelectProvider select(String tableAlias,String field) {
		return select(tableAlias,field,null);
	}
	//
	public SelectProvider select(String tableAlias,String field,String asAlias) {
		return select(tableAlias, field,null,asAlias, false, null);
	}
	//
	public SelectProvider select(String tableAlias,String field,
			String preAsField,String asField,boolean distinct,String statFunction) {
		selectFields.add(createSelectField(tableAlias, field, 
				preAsField,asField, distinct, statFunction));
		return this;
	}
	//
	private SelectField createSelectField(String tableAlias,String field,
			String preAsField,String asField,boolean distinct,String statFunction) {
		SelectField sf=new SelectField();
		sf.tableAlias=tableAlias;
		sf.field=field;
		sf.preAsField=preAsField;
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
		return this.where(MAIN_TABLE_ALIAS,key, "=", value);
	}
	//
	public SelectProvider where(String alias,String key,String op,Object value){
		qw.where(alias, key, op, value);
		return this;
	}
	//
	public SelectProvider in(String alias,String key,Object[] values){
		qw.in(alias, key, values);
		return this;
	}
	//
	public SelectProvider whereSql(String sql,Object ...values){
		qw.whereSql(sql, values);
		return this;
	}
	//
	public SelectProvider groupBy(String field) {
		groupBy(MAIN_TABLE_ALIAS, field);
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
		qw.orderBy(orderBy);
		return this;
	}
	//
	public SelectProvider limit(int start,int limit){
		qw.limit(start, limit);
		return this;
	}
	//
	public SelectProvider limit(int end){
		qw.limit(end);
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
			join.table1Alias=MAIN_TABLE_ALIAS;
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
	 * @param q
	 */
	protected void addWheres(Query q) {
		if(q==null) {
			return;
		}
		Map<String,Object> paraMap=new HashMap<>();
		List<Field> fields=getQueryFields(q);
		for (Field field : fields) {
			try {
				Object value=field.get(q);
				paraMap.put("#{"+field.getName()+"}", value);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new SmartJdbcException(e.getMessage());
			}
		}
		for (Field field : fields) {
			try {
				Class<?> fieldType = field.getType();
				Object value=field.get(q);
				QueryField queryFieldDefine=field.getAnnotation(QueryField.class);
				String alias = MAIN_TABLE_ALIAS;
				InnerJoin innerJoin=field.getAnnotation(InnerJoin.class);
				if(innerJoin!=null) {
					String key=innerJoin.table2().getSimpleName()+"-"+
							innerJoin.table1Field()+"-"+
							innerJoin.table2Field();
					Join join=innerJoins.get(key);
					alias=join.table2Alias;
				}
				//
				if(queryFieldDefine!=null&&queryFieldDefine.whereSql()!=null&&(!StringUtil.isEmpty(queryFieldDefine.whereSql()))) {//whereSql check first
					String whereSql=queryFieldDefine.whereSql();
					SqlBean sqlBean=parseSql(whereSql, paraMap);//eg:userName like #{userName}
					whereSql(sqlBean.sql,sqlBean.parameters);
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
						}else if (fieldType.equals(int[].class)||
								fieldType.equals(short[].class)||
								fieldType.equals(byte[].class)||
								fieldType.equals(String[].class)) {
							if(fieldType.equals(int[].class)) {
								in(alias, dbFieldName, ArrayUtils.convert((int[])value));
							}else if(fieldType.equals(short[].class)) {
								in(alias, dbFieldName, ArrayUtils.convert((short[])value));
							}else if(fieldType.equals(byte[].class)) {
								in(alias, dbFieldName, ArrayUtils.convert((byte[])value));
							}else if(fieldType.equals(String[].class)) {
								in(alias, dbFieldName, ArrayUtils.convert((String[])value));
							}
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
	public static boolean preParseSql(String sql) {
		Pattern p=Pattern.compile("\\#\\{[a-zA-Z_$][a-zA-Z0-9_$]*\\}");
		Matcher m = p.matcher(sql);
		if(m.find()) { 
		    return true;
		}
		return false;
	}
	//
	public static SqlBean parseSql(String sql,Map<String,Object> paraMap) {
		Pattern p=Pattern.compile("\\#\\{[a-zA-Z_$][a-zA-Z0-9_$]*\\}");
		Matcher m = p.matcher(sql);
		List<String> groups=new ArrayList<>();
		while(m.find()) { 
		    groups.add(m.group());
		}
		if(groups.isEmpty()) {
			return new SqlBean(sql);
		}
		String newSql=m.replaceAll("?");
		Object[] values=new Object[groups.size()];
		int i=0;
		for (String group : groups) {
			Object value=paraMap.get(group);
			if(value==null) {
				throw new SmartJdbcException(group+" not found.\nsql:"+sql+
						"\nall can choose paras is:"+paraMap.keySet()); 
			}
			values[i++]=value;
		}
		return new SqlBean(newSql,values);
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
			if(domainField==null) {
				select(MAIN_TABLE_ALIAS, field.getName());
				continue;
			}
			boolean distinct=domainField.distinct();
			String statFunc=domainField.statFunc();
			String reallyName=field.getName();
			if(!StringUtil.isEmpty(domainField.field())) {
				reallyName=domainField.field();
			}
			//
			LeftJoin leftJoin=field.getAnnotation(LeftJoin.class);
			if(leftJoin!=null) {
				Join join=createLeftJoin(field.getName(),MAIN_TABLE_ALIAS,"l"+(index++),
						domainClass,leftJoin.table2(),leftJoin.table1Field(),leftJoin.table2Field());
				select(join.table2Alias,reallyName,null,field.getName(),distinct,statFunc);
			}else if(!StringUtil.isEmpty(domainField.foreignKeyFields())) {
				String foreignKeyId = domainField.foreignKeyFields();
				String[] foreignKeyIds=foreignKeyId.split(",");
				Class<?> table1=domainClass;
				String table1Alias=MAIN_TABLE_ALIAS;
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
				if(WRAP_TYPES.contains(field.getType())){
					if(StringUtil.isEmpty(domainField.field())) {
						select(join.table2Alias,field.getName(),null,null,distinct,statFunc);
					}else {
						select(join.table2Alias,domainField.field(),null,field.getName(),distinct,statFunc);
					}
				}else {
					List<Field> subClassFields=getPersistentFields((Class<?>)field.getGenericType());
					for (Field subClassField : subClassFields) {
						select(join.table2Alias,subClassField.getName(),field.getName()+"_",
								subClassField.getName(),distinct,statFunc);
					}
				}
			}else {
				select(MAIN_TABLE_ALIAS, field.getName(),null,null,distinct,statFunc);
				continue;
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
		return createLeftJoin(key, table1Alias, table2Alias, table1, table2, table1Field, getSinglePrimaryKey(table2));
	}
	//
	private Join createLeftJoin(String key,String table1Alias,String table2Alias,Class<?> table1,Class<?> table2,
			String table1Field,String table2Field) {
		Join join = new Join();
		join.key=key;
		join.table1Alias=table1Alias;
		join.table2Alias=table2Alias;
		join.table1=table1;
		join.table2 = table2;
		join.table1Field=table1Field;
		join.table2Field= table2Field;
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
			if(field.distinct) {
				sql.append(" distinct ");
			}
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
				String asField=convertFieldName(field.asField);
				if(field.preAsField!=null) {
					asField=field.preAsField+asField;
				}
				sql.append(" as `").append(asField).append("`");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
	}
	//
	private SqlBean build(StringBuffer sql) {
		//from
		sql.append(" from ").append(getTableName(domainClass)).append(" ").append(MAIN_TABLE_ALIAS).append(" ");
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
		for (Where w : qw.getWheres()) {
			if(w.alias==null) {
				w.alias=MAIN_TABLE_ALIAS;
			}
		}
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
			if (!StringUtil.isEmpty(qw.getOrderBy())) {
				sql.append(" order by ").append(qw.getOrderBy());
			}
		}
		//limit
		if(needPaging) {
			addPaging(query);
			if(qw.getLimitEnd()!=-1) {
				sql.append(" limit ").append(qw.getLimitStart()).append(",").append(qw.getLimitEnd());
			}
		}
		//for update
		if(isForUpdate) {
			sql.append(" for update ");
		}
		//
		SqlBean bean=new SqlBean(sql.toString(),qw.whereValues());
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
