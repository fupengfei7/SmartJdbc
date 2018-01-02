package test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.util.DumpUtil;
import test.dao.BizDAO;
import test.domain.DiscountCoupon;
import test.domain.User;
import test.domain.info.DiscountCouponDetailInfo;
import test.domain.info.DiscountCouponInfo;
import test.domain.info.UserInfo;
import test.domain.info.query.DiscountCouponInfoQuery;
import test.domain.info.query.UserInfoQuery;
import test.domain.query.UserQuery;

/**
 * 
 * @author skydu
 *
 */
public class DAOTestCase extends BaseTestCase{
	//
	BizDAO dao;
	//
	public DAOTestCase() {
		dao=new BizDAO();
		Config.addDAOInterceptor(new DAOInterceptor() {
			@Override
			public void beforeInsert(Object bean, boolean withGenerateKey,String[] excludeProperties) {
				super.beforeInsert(bean, withGenerateKey, excludeProperties);
				dao.setFieldValue(bean, "createTime", new Date());
				dao.setFieldValue(bean, "updateTime", new Date());
			}
			//
			@Override
			public void beforeUpdate(Object bean, boolean excludeNull, String[] excludeProperties) {
				super.beforeUpdate(bean, excludeNull, excludeProperties);
				dao.setFieldValue(bean, "updateTime", new Date());
			}
		});
	}
	static {
	    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
	//
	public void testAddUser() {
		User user=new User();
		user.name="test";
		user.userName="test";
		user.password="111111";
		user.id=dao.add(user);
		System.out.println(user.id);
	}
	
	public void testGetById() {
		User user=dao.getById(User.class, 1);
		System.out.println(DumpUtil.dump(user));
	}
	
	public void testGetUsers() {
		UserQuery query=new UserQuery();
		query.userName="t";
		query.orderType=UserQuery.ORDER_BY_CREATE_TIME_DESC;
		List<User> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testGetUsersCounts() {
		UserQuery query=new UserQuery();
		query.userName="t";
		int count=dao.getListCount(query);
		System.out.println(count);
	}
	
	public void testUpdateUser() {
		User user=dao.getById(User.class, 1);
		user.description="测试描述";
		dao.update(user);
	}
	
	public void testDeleteUser() {
		int count=dao.delete(User.class, QueryWhere.create().where("id", 3));
		System.out.println(count);
	}
	//
	public void testAddDiscountCoupon() throws IOException{
		DiscountCoupon bean=new DiscountCoupon();
		bean.name="满100减10元";
		bean.createUserId=1;
		bean.conditionalMoney=100;
		bean.money=10;
		bean.num=100;
		int id=dao.add(bean);
		System.out.println(id);
	}
	//
	//
	//
	//
	public void testGetUserInfoById() {
		dao.getById(UserInfo.class,1);
	}
	//
	public void testGetUserInfos() {
		UserInfoQuery query=new UserInfoQuery();
		query.roleName="总经理";
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetUserInfosCount() {
		UserInfoQuery query=new UserInfoQuery();
		query.gender=1;
		query.roleName="总经理";
		dao.getListCount(query);
	}
	//
	public void testGetDiscountCouponInfo() {
		dao.getById(DiscountCouponInfo.class,1);
	}
	//
	public void testGetDiscountCouponInfos() {
		DiscountCouponInfoQuery query=new DiscountCouponInfoQuery();
		query.createUserName="刘备";
		List<DiscountCouponInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetDiscountCouponInfosCount() {
		DiscountCouponInfoQuery query=new DiscountCouponInfoQuery();
		query.createUserName="刘备";
		dao.getListCount(query);
	}
	//
	//
	public void testGetDiscountCouponDetailInfo() {
		dao.getById(DiscountCouponDetailInfo.class,1);
	}
}
