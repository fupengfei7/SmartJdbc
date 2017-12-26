package test.domain;

import io.itit.smartjdbc.annotations.ForeignKey;

/**
 * 优惠券
 * @author skydu
 *
 */
public class DiscountCoupon extends BaseDomain{
	//
	public static final int STATUS_未开始=1;
	public static final int STATUS_进行中=2;
	public static final int STATUS_已结束=3;
	//
	public String name;
	
	/**状态*/
	public int status;
	
	/**达到多少钱满足条件(单位：元)*/
	public int conditionalMoney;
	
	/**优惠金额*/
	public int money;
	
	/**数量*/
	public int num;

	@ForeignKey(domainClass = User.class)
	public int createUserId;
	
	@ForeignKey(domainClass = User.class)
	public int updateUserId;
	
}
