package test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.itit.smartjdbc.Config;
import io.itit.smartjdbc.DAOInterceptor;
import io.itit.smartjdbc.Param;
import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.QueryWhere;
import io.itit.smartjdbc.util.DumpUtil;
import test.dao.BizDAO;
import test.domain.Article;
import test.domain.User;
import test.domain.info.ArticleInfo;
import test.domain.info.UserInfo;
import test.domain.info.UserSimpleInfo;
import test.domain.info.UserStat;
import test.domain.query.ArticleInfoQuery;
import test.domain.query.UserInfoQuery;
import test.domain.query.UserQuery;
import test.domain.query.UserStatQuery;

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
	/**新增用户*/
	public void testAddUser() {
		User user=new User();
		user.name="关羽";
		user.userName="guanyu";
		user.password="111111";
		user.id=dao.add(user);
		System.out.println(user.id);
	}
	
	/**通过id查询用户*/
	public void testGetById() {
		User user=dao.getById(User.class, 1);
		System.out.println(DumpUtil.dump(user));
	}
	
	/**通过用户名查询用户*/
	public void testGetByUserName() {
		User user=dao.getDomain(User.class,QueryWhere.create().where("userName", "test"));
		System.out.println(DumpUtil.dump(user));
	}
	
	/**查询用户列表*/
	public void testGetUsers() {
		UserQuery query=new UserQuery();
		query.userName="test";
		List<User> list=dao.getList(query,"createTime","updateTime");
		System.out.println(DumpUtil.dump(list));
	}
	
	/**查询用户列表总数*/
	public void testGetUsersCounts() {
		UserQuery query=new UserQuery();
		query.userName="test";
		int count=dao.getListCount(query);
		System.out.println(count);
	}
	
	/**当然写sql也是支持的*/
	public void testQueryUsers() {
		List<User> users=dao.queryList(User.class, 
				"select * from User where userName like concat('%'?,'%') and id=?", 
				"liu",
				1);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryUsers2() {
		List<User> users=dao.queryList(User.class, 
				"select * from User where userName like concat('%',#{userName},'%') and id=#{id}", 
				new Param("userName", "liu"),
				new Param("id", 1));
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryUsersCount() {
		int count=dao.queryCount(
				"select count(1) from User where userName like concat('%',?,'%') and id=?", 
				"liu",
				1);
		System.out.println(count);
	}
	
	public void testGetUserIds() {
		List<Integer> userIds=dao.queryForIntegers("select id from User where id=#{id}",
				new Param("id", 1));
		System.out.println(DumpUtil.dump(userIds));
		//
		userIds=dao.queryForIntegers("select id from User where id=?",
				1);
		System.out.println(DumpUtil.dump(userIds));
	}
	
	public void testQueryUsersCount2() {
		int count=dao.queryCount(
				"select count(1) from User where userName like concat('%',#{userName},'%') and id=#{id}", 
				new Param("userName", "liu"),
				new Param("id", 1));
		System.out.println(count);
	}
	
	
	public void testUpdateUser() {
		User user=dao.getById(User.class, 1);
		user.gender=User.GENDER_男;
		dao.update(user);
	}
	
	public void testUpdateUser2() {
		dao.executeUpdate("update User set name='关羽0' where id=?",1);
		dao.executeUpdate("update User set name='关羽2' where id=#{id}",new Param("id", 1));
	}
	
	/**删除用户*/
	public void testDeleteUser() {
		int count=dao.delete(User.class, QueryWhere.create().where("id", 3));
		System.out.println(count);
	}
	//
	/**添加文章*/
	public void testAddArticle() throws IOException{
		Article bean=new Article();
		bean.title="桑切斯遭孤立?队友吃饭他加练 为融入曼联拼了";
		bean.content="上周，有关桑切斯的负面消息很多，英媒体爆料他在曼联阵中独来独往、总是一个人吃饭，"
				+ "无法融入到集体中，还有媒体称桑切斯甚至在比赛中遭到了队友的当面批评，"
				+ "这些让外界对桑切斯在曼联的未来表示了担忧。不过，英媒体《每日镜报》"
				+ "今日透露桑切斯在曼联并没有被孤立，智利人也没有刻意躲避队友，"
				+ "他之所以经常一个人吃饭竟是这样的原因。";
		bean.createUserId=1;
		bean.updateUserId=1;
		bean.status=Article.STATUS_待审核;
		int id=dao.add(bean);
		System.out.println(id);
	}
	//
	public void testGetUserInfoById() {
		dao.getById(UserInfo.class,1);
	}
	//
	/**查询角色名称是总监是用户列表*/
	public void testGetUserInfos() {
		UserInfoQuery query=new UserInfoQuery();
		query.roleName="总监";
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
	public void testGetUserInfosOrderBySortFields() {
		UserInfoQuery query=new UserInfoQuery();
		// order by name asc,roleId desc
		query.gender=Query.SORT_TYPE_ASC;
		query.nameSort=Query.SORT_TYPE_ASC;
		query.roleIdSort=Query.SORT_TYPE_DESC;
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetArticleInfo() {
		dao.getById(ArticleInfo.class,1);
	}
	//
	public void testGetArticleInfos() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.createUserName="刘备";
		query.statusList=new int[] {1,2};
		List<ArticleInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	//
	public void testGetArticleInfosCount() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.createUserName="刘备";
		query.statusList=new int[] {ArticleInfo.STATUS_审核通过,ArticleInfo.STATUS_审核未通过};
		System.out.println(DumpUtil.dump(dao.getListCount(query)));
	}
	/**
	 * 查询文章详情
	 */
	public void testArticleInfo() {
		ArticleInfo info=dao.getById(ArticleInfo.class,1);
		System.out.println(DumpUtil.dump(info));
	}
	
	/**
	 * 查询userId为1的用户喜爱的文章列表
	 */
	public void testGetUserLikeArticles() {
		ArticleInfoQuery query=new ArticleInfoQuery();
		query.likeUserId=1;
		query.pageSize=20;
		List<ArticleInfo> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	//
	public void getUserStats() {
		UserStatQuery query=new UserStatQuery();
		List<UserStat> list=dao.getList(query);
		System.out.println(DumpUtil.dump(list));
	}
	
	public void testSum() {
		Long sum=dao.sum(User.class,Long.class, "roleId", QueryWhere.create());
		System.out.println("sum:"+sum);
	}
	
	public void testSumByQuery() {
		UserQuery query=new UserQuery();
		query.userName="liu";
		Long sum=dao.sum(query,Long.class, "roleId");
		System.out.println("sum:"+sum);
	}
	
	public void testGetUserSimpleInfo() {
		List<UserSimpleInfo> list=dao.getList(UserSimpleInfo.class,QueryWhere.create());
		System.out.println(DumpUtil.dump(list));
	}
	//
	public void testQueryWithOrGroup() {
		UserInfoQuery query=new UserInfoQuery();
		query.orName="关";
		query.orUserName="zhang";
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
	
	public void testQueryWithWhereSql() {
		UserInfoQuery query=new UserInfoQuery();
		query.nameOrUserName="zhang";
		List<UserInfo> users=dao.getList(query);
		System.out.println(DumpUtil.dump(users));
	}
}
