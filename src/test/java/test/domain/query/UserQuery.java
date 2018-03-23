package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.OrderBys;
import io.itit.smartjdbc.annotations.OrderBys.OrderBy;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
@OrderBys(orderBys={
		@OrderBy(orderType=UserQuery.ORDER_BY_CREATE_TIME_DESC,sql=" createTime desc"),
		@OrderBy(orderType=UserQuery.ORDER_BY_CREATE_TIME_ASC,sql=" createTime asc")})
@QueryDefine(domainClass=User.class)
public class UserQuery extends Query{
	//
	public static final int ORDER_BY_CREATE_TIME_DESC=1;
	public static final int ORDER_BY_CREATE_TIME_ASC=2;
	//
	public String userName;
	
	public Integer gender;
	
	@QueryField(whereSql="and (name like concat('%',#{nameOrUserName},'%') or userName like concat('%',#{nameOrUserName},'%'))")
	public String nameOrUserName;
	
	@QueryField(field="gender")
	public int[] genders;
	
	
}
