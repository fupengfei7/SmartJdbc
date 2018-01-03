package test.domain;

import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 优惠券
 * @author skydu
 *
 */
public class DiscountCouponUserLike extends BaseDomain{
	//
	@ForeignKey(domainClass=DiscountCoupon.class)
	public int discountCouponId;
	
	@ForeignKey(domainClass=User.class)
	public int userId;
	
}
