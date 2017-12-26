package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryDefine.OrderBy;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=User.class,
orderBys={@OrderBy(orderType=UserQuery.ORDER_BY_CREATE_TIME_DESC,sql=" createTime desc"),
		@OrderBy(orderType=UserQuery.ORDER_BY_CREATE_TIME_ASC,sql=" createTime asc")})
public class UserQuery extends Query{
	//
	public static final int ORDER_BY_CREATE_TIME_DESC=1;
	public static final int ORDER_BY_CREATE_TIME_ASC=2;
	//
	public String userName;
	
	public Integer gender;
	
	
}
