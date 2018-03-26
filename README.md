# 轻量级数据库访问框架SmartJdbc

# 1 简介

SmartJdbc一个简单易用的ORM框架，它支持sql构建、sql执行、命名参数绑定、查询结果自动映射和通用DAO。

SmartJdbc可以让你不用写DAO,不用写SQL。

# 2 入门

## 2.1 安装

要使用 SmartJdbc， 只需将 SmartJdbc-1.0.5.jar 文件置于 classpath 中即可。

如果使用 Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml 文件中：

```xml
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>SmartJdbc</artifactId>
    <version>1.0.5</version>
</dependency>
<dependency>
    <groupId>com.github.icecooly</groupId>
    <artifactId>SmartJdbc-Spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

如果使用 Gradle 来构建项目，则需将下面的代码置于 build.gradle 文件的 dependencies 代码块中：

```groovy
compile 'com.github.icecooly:SmartJdbc:1.0.5'
compile 'com.github.icecooly:SmartJdbc-Spring:1.0.0'
```

# 3 例子

## 3.1 简单insert

```java
User user=new User();
user.name="关羽";
user.userName="guanyu";
user.password="111111";
user.id=dao.add(user);
```

## 3.2 基本查询

```java
User user=dao.getById(User.class, 1);
User user=dao.getDomain(User.class,QueryWhere.create().where("userName", "test"));
```

## 3.3 列表查询

```java
UserQuery query=new UserQuery();
query.userName="test";
query.nameOrUserName="关";
query.orderType=UserQuery.ORDER_BY_CREATE_TIME_DESC;
List<User> list=dao.getList(query);
```

# 4 其他

* [项目主页](https://github.com/icecooly/SmartJdbc)

# 5 更新日志