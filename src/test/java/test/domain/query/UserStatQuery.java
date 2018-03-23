package test.domain.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.OrderBys;
import io.itit.smartjdbc.annotations.OrderBys.OrderBy;
import io.itit.smartjdbc.annotations.QueryDefine;
import test.domain.info.UserStat;

/**
 * 
 * @author skydu
 *
 */

@OrderBys(orderBys={
		@OrderBy(orderType=UserStatQuery.ORDER_BY_CREATE_TIME_DESC,sql=" createTime desc"),
		@OrderBy(orderType=UserStatQuery.ORDER_BY_CREATE_TIME_ASC,sql=" createTime asc")})
@QueryDefine(domainClass=UserStat.class)
public class UserStatQuery extends Query{
	//
	public static final int ORDER_BY_CREATE_TIME_DESC=1;
	public static final int ORDER_BY_CREATE_TIME_ASC=2;
	
	public Integer gender;
}
