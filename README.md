# 轻量级ORM框架SmartJdbc(Spring Boot+SpringMVC+SmartJdbc)

# 1 简介

SmartJdbc一个简单易用的ORM框架，它支持sql构建、sql执行、命名参数绑定、查询结果自动映射和通用DAO。

SmartJdbc可以让你不用写DAO,不用写SQL。

# 2 入门

## 2.1 安装

要使用 SmartJdbc， 只需将 SmartJdbc-1.0.8.jar 文件置于 classpath 中即可。

如果使用 Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>SmartJdbc</artifactId>
    <version>1.0.8</version>
</dependency>
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>SmartJdbc-Spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件的 dependencies 代码块中：

```groovy
compile 'com.github.icecooly:SmartJdbc:1.0.8'
compile 'com.github.icecooly:SmartJdbc-Spring:1.0.0'
```

# 3 例子

## 3.1 简单增删改

增加
```java
User user=new User();
user.name="刘备";
user.userName="liubei";
user.password="111111";
user.id=dao.add(user);
```
修改
```java
User user=dao.getById(User.class, 1);
user.name="刘备2";
user.userName="liubei2";
user.password="222222";
user.id=dao.update(user);
```
删除
```java
dao.deleteById(User.class, 1);
```

## 3.2 基本查询

```java
User user=dao.getById(User.class, 1);
User user=dao.getDomain(User.class,QueryWhere.create().where("userName", "test"));
```

## 3.3 列表查询

```java
//查询用户名包含test的用户列表,按照创建时间降序
UserQuery query=new UserQuery();
query.userName="test";
query.orderType=UserQuery.ORDER_BY_CREATE_TIME_DESC;
List<User> list=dao.getList(query);
```

## 3.4 复杂查询(如果数据库设计是三范式，不冗余存储数据，查询时可以自动join，不用手工写sql)

```java
//查询角色名称是总监是用户列表
@DomainDefine(domainClass=User.class)
public class User extends BaseDomain{
	public String name;
	
	public String userName;
	
	public String password;
	
	public boolean gender;
	
	public Date lastLoginTime;
	
	@ForeignKey(domainClass=Department.class)
	public int departmentId;
	
	@ForeignKey(domainClass=Role.class)
	public int roleId;
	
	public String description;
}
public class UserInfo extends User{
	@DomainField(foreignKeyFields="departmentId",field="name")
	public String departmentName;
	
	@DomainField(foreignKeyFields="roleId",field="name")
	public String roleName;
}
UserInfoQuery query=new UserInfoQuery();
query.roleName="总监";
query.pageSize=20;
List<UserInfo> users=dao.getList(query);
```
更多可参考test/DAOTestCase.java

# 4 Spring Boot中使用SmartJdbc
SmartJdbcConfig.java
```java
@Configuration
@EnableTransactionManagement
@AutoConfigureAfter({DataSourceConfig.class})
public class SmartJdbcConfig implements TransactionManagementConfigurer {
	//
	private static final Logger logger = LoggerFactory.getLogger(SmartJdbcConfig.class);
	//
	@Autowired
	private DataSource dataSource;
	//
	@PostConstruct
	public void init() {
		SqlSessionFactory sessionFactory=new SqlSessionFactory();
		sessionFactory.setDataSource(dataSource);
		ConnectionManager.setTransactionManager(sessionFactory);
		Config.setTableNameFunc(clazz->{return "t"+convertFieldName(clazz.getSimpleName());});//表结构映射User->t_user
		Config.setConvertFieldNameFunc(this::convertFieldName);//字段映射userName->user_name
		Config.addSqlInterceptor(sessionFactory);
	}
	
	protected  String convertFieldName(String name) {
		StringBuffer result = new StringBuffer();
		for (char c : name.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append("_");
			}
			result.append(Character.toLowerCase(c));
		}
		return result.toString();
	}
	
	@Bean(name="annotationDrivenTransactionManager")
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}
}
```

# 5 其他

* [项目主页](https://github.com/icecooly/SmartJdbc)

# 6 更新日志