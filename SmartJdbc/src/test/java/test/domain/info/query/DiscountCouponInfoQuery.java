package test.domain.info.query;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.QueryDefine;
import io.itit.smartjdbc.annotations.QueryField;
import test.domain.User;
import test.domain.info.DiscountCouponInfo;

/**
 * 
 * @author skydu
 *
 */
@QueryDefine(domainClass=DiscountCouponInfo.class)
public class DiscountCouponInfoQuery extends Query{

	public String name;
	
	@InnerJoin(table2=User.class,table1Field="createUserId")
	@QueryField(field="name")
	public String createUserName;


	@InnerJoin(table2=User.class,table1Field="updateUserId")
	@QueryField(field="name")
	public String updateUserName;
	
	public int[] statusList;
}
