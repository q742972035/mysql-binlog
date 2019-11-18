# mysql-binlog-dispatch使用手册

- 前言 （最低JAVA版本为JDK1.8）

**本工具基于expose进行扩展，允许使用一个类单独对指定的表进行DML的INSERT/UPDATE/DELETE监听**

---

## 工程导入


```
<dependency>
    <groupId>com.github.q742972035.mysql.binlog</groupId>
    <artifactId>dispatch</artifactId>
    <version>0.0.0.2-SNAPSHOTS</version>
</dependency>
```
**（仓库地址为maven中央仓库）**

---
## 历史版本
暂无

---
## SQL准备
```
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL DEFAULT '',
  `email` varchar(20) NOT NULL DEFAULT '',
  `phone` varchar(20) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `admin` (
  `uuid` varchar(128) NOT NULL,
  `name` varchar(30) DEFAULT '',
  `age` int(3) DEFAULT '18',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

```



## 开始使用
1. 构造Runner
```
Properties properties = PropertiesUtils.getPropertiesByPath("/test.properties", ClassLoader.getSystemClassLoader());

        Runner runner = new Runner();
        BinaryLogClientBuild build = new BinaryLogClientBuild()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .setSchema(properties.getProperty("schema"))
                .setHostname(properties.getProperty("hostname"))：
        runner.setClientBuild(build);
        runner.setThreadSize(3);
        runner.setScanpackage(properties.getProperty("scanpackage"));

        runner.init();
        runner.run();
```
2. 构建属于自己的handler

```
@TableHandler(tableName = "...")
public class UserTableHandler {
}
```
在<1.>中设置的scanpackage包路径下的任何（子）包下，该类通过@TableHandler修饰，tableName 设置为表名（同一个表只能被一个@TableHandler修饰，否则会抛出异常 **TableHandlerExistException**）

在@TableHandler修饰的类中，所属的方法可以添加@Insert\@Update\@Delete注解，但是一个类中最多只能出现一次上述注解，否则会出现异常**MethodAnnotationException**

以User表结构为例的使用方式
```
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL DEFAULT '',
  `email` varchar(20) NOT NULL DEFAULT '',
  `phone` varchar(20) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;


```
定义的User类

```
public class User {
    private Long id;
    @com.github.q742972035.mysql.binlog.dispatch.annotation.Column("username")
    private String un;
    @javax.persistence.Column(name = "email")
    private String email;
    private String phone;

    private Date create_time;
    private Date update_time;

    setter...
    getter...
}
```
> 当出现字段名与表的列名冲突时，可以使用@Column确定列名,*@Column只能修饰在字段中才会生效*。




2.1. @Insert的使用

2.1.1. 最简单的使用
```
@Insert
public void insert(User user){
    
}
```
> 每当出现一次insert语句时，可以直接从user变量中获取这个user的所有属性。

2.1.2. 只需要个别字段的使用
```
@Insert
public void insert(String email){

}
```
> 每当出现一次insert语句时，只会获取到输出语句的email信息。

2.1.3. 字段名语义不一致
```
@Insert
public void insert(@Column("email") String e){

}
```
2.1.4 直接使用Map
```
@Insert
public void insert(Map<String,Object> map){

}
```

> user表中不存在列名的字段e，所以可以通过@Column注解重定向列名为email

2.2 @Delete的使用（与<2.1>一致，只需修改注解为@Delete即可）
> 上述描述的insert语句统一改为delete语句，其他行为一致。

2.3 @Update的使用
![image](https://raw.githubusercontent.com/q742972035/mysql-binlog/master/mysql-binlog-dispatch/IMAGES/demo1.png)

2.3.1 只需要某个字段
```
@Update
public void update(@Before Date update_time, @After String phone){

}
```
当对数据修改时，会传回修改前的update_time，和修改后的phone
> 但是这样有一个缺陷，比如我想知道修改前与修改后的phone

2.3.2 同时知道某一个字段的修改前后的值
```
@Update
public void update(@Before("phone") String aPhone,@After("phone") String bPhone){

}
```
通过@Before或@After的默认修饰，可以指定列名
上面的写法也可以改成

```
@Update
public void update(@Before()@Column("phone") String aPhone, @After() @Column("phone") String bPhone){

}
```
2.3.3 想知道修改前后的所有内容
```
@Update
public void update(@Before User bUser, @After User aUser){

}
```
或者

```
@Update
public void update(@Before Map<String,Object> bm, @After Map<String,Object> am){

}
```

@Update修饰得所有形参都需要@Before或者@After修饰，否则会出现异常**IllegalUpdateException**

2.4 @CurrentPosition和@NextPosition

某些特殊的场景可能需要使用nextPosition信息（@Insert\@Update\@Delete）通用，@CurrentPosition和@NextPosition是作用在方法形参上的，以下写法都可以

```
@Insert\@Update\@Delete
public void methodname(@CurrentPosition long cp, @NextPosition long np
,@CurrentPosition String scp,@NextPosition int inp){

}
```

3. Runner类的threadSize字段说明

binlog日志是通过单线程读取的，如果业务逻辑太繁琐，则阻塞binlog日志的读取速度，因此通过多线程技术，允许将多个表分在不同的线程上各自读取（同一个表的binlog日志先后顺序一致），也就是在a表先insert后update的时候，不会发生a表先update再insert得情况。

原理如下：

![image](https://github.com/q742972035/mysql-binlog/blob/master/mysql-binlog-dispatch/IMAGES/ReadWriteLinkedListGroup.png?raw=true)


4.TableHandler得其他使用方式

大部分情况下，每个表得TableHandler都有着@Insert,@Update和@Delete得情况，如果都写一遍，想一下就觉得有点恐怖

通过继承BaseTableHandler，快速拓展
```
@TableHandler(tableName = "user")
public class UserOtherTableHandler extends BaseTableHandler<User> {

    public UserOtherTableHandler() throws GenericClassNotFoundException {
        setHandlerActions(Arrays.asList(
                // user得操作1
                new UserDoAction1()
                // user得操作2
                ,new UserDoAction2()
        ));
    }
}
```
其中UserDoAction1可以比如成user表的第一个操作，UserDoAction2同理为user表的第二个操作。UserDoAction1和UserDoAction2都有各自的SingleThreadExecutor，确保了每一个Action的先后顺序以及各自Action的互不干扰（假如Action1为统计user表的用户数量，Action2为统计user表的手机号码数量，Action2的手机号码查与去重可能会相对于Action1耗时）

每一个Action都需要通过继承BaseHandlerAction，如
```
public class UserDoAction1 extends BaseHandlerAction<User> {
    @Override
    public void doInsert(User user) {

    }

    @Override
    public void doUpdate(User before, User after) {

    }

    @Override
    public void doDelete(User user) {

    }

    @Override
    public void init() {

    }
}
```
init方法可以做一些初始化操作，一般模板可以是这样（其他需要自行扩展），如：
```
public class UserDoAction1 extends BaseHandlerAction<User> {

    private UserDao userDao;
    private UserStatisDao userStatisDao;

    public UserDoAction1(UserDao userDao,UserStatisDao userStatisDao) {
        this.userDao = userDao;
        this.userStatisDao = userStatisDao;
    }

    @Override
    public void doInsert(User user) {
        Long id = user.getId();
        if (id < maxId) {
            return;
        }
        userStatisDao.usercountAddOne();
    }

    @Override
    public void doUpdate(User before, User after) {

    }

    @Override
    public void doDelete(User user) {
        Long id = user.getId();
        if (id < maxId) {
            return;
        }
        userStatisDao.usercountDeleteOne();
    }

    private volatile long maxId;

    @Override
    public void init() {
        // 获取User目前的最大id
        maxId = userDao.getMaxId();
        // 往其他表初始化user数量
        userStatisDao.refreshAllUserCount();
    }
}
```


