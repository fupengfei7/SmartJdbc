package test.domain.info.query;

import javax.management.relation.Role;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.annotations.QueryDefine.OrderBy;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=User.class,
orderBys={@OrderBy(orderType=UserInfoQuery.ORDER_BY_CREATE_TIME_DESC,sql=" createTime desc"),
		@OrderBy(orderType=UserInfoQuery.ORDER_BY_CREATE_TIME_ASC,sql=" createTime asc")})
public class UserInfoQuery extends Query{
	//
	public static final int ORDER_BY_CREATE_TIME_DESC=1;
	public static final int ORDER_BY_CREATE_TIME_ASC=2;
	//
	public String userName;
	
	public Integer gender;
	
	@InnerJoin(table2=Role.class,table1Field="roleId")
	@QueryField(field="name")
	public String roleName;
}
