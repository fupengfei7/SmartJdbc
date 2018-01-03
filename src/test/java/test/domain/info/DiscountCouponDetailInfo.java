package test.domain.info;

import io.itit.smartjdbc.annotations.DomainDefine;
import io.itit.smartjdbc.annotations.DomainField;
import io.itit.smartjdbc.annotations.LeftJoin;
import test.domain.Department;
import test.domain.DiscountCoupon;
import test.domain.DiscountCouponUserLike;
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
	
	@LeftJoin(table2=DiscountCouponUserLike.class,table1Field="id",table2Field="discountCouponId")
	@DomainField(field="id")
	public int userLikeId;//用户是否点赞
	
}
