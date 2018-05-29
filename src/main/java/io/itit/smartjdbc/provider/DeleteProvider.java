package io.itit.smartjdbc.provider;

import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.QueryWhere.WhereStatment;
import io.itit.smartjdbc.SqlBean;

/**
 * 
 * @author skydu
 *
 */
public class DeleteProvider extends SqlProvider{
	//
	Class<?> domainClass;
	QueryWhere qw;
	//
	public DeleteProvider(Class<?> domainClass,QueryWhere qw) {
		this.domainClass=domainClass;
		this.qw=qw;
	}
	
	@Override
	public SqlBean build() {
		StringBuffer sql=new StringBuffer();
		String tableName=getTableName(domainClass);
		sql.append("delete from ").append(tableName).append(" ");
		sql.append("where 1=1 ");
		WhereStatment ws=qw.whereStatement();
		sql.append(ws.sql);
		return createSqlBean(sql.toString(),ws.values);
	}

}
