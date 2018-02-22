package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import test.domain.DiscountCoupon;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(domainClass=DiscountCoupon.class)
public class DiscountCouponInfo extends DiscountCoupon{

	@DomainField(foreignKeyFields="createUserId",field="name")
	public String createUserName;
	
	@DomainField(foreignKeyFields="createUserId,departmentId",field="name")
	public String createUserDepartmentName;
	
	@DomainField(foreignKeyFields="updateUserId",field="name")
	public String updateUserName;
	
	@DomainField(foreignKeyFields="updateUserId,departmentId",field="name")
	public String updateUserDepartmentName;
	
	@DomainField(ignoreWhenSelect=true)
	public String test;
	
}
