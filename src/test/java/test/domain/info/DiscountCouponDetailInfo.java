package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import test.domain.Department;
import test.domain.DiscountCoupon;
import test.domain.User;

/**
 * 
 * @author skydu
 *
 */
@DomainDefine(domainClass=DiscountCoupon.class)
public class DiscountCouponDetailInfo extends DiscountCoupon{

	@DomainField(foreignKeyFields="createUserId")
	public User createUser;
	
	@DomainField(foreignKeyFields="createUserId,departmentId")
	public Department createUserDepartment;
	
	@DomainField(foreignKeyFields="updateUserId")
	public User updateUser;
	
	@DomainField(foreignKeyFields="updateUserId,departmentId")
	public Department updateUserDepartment;
	
}
