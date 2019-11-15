# mysql-binlog-expose使用手册

- 前言 （最低JAVA版本为JDK1.8）

**本工具对mysql的binlog日志进行了监听(如果让数据库成为主库请自行查阅相关资料),并且对其中的dml和ddl进行了监听，可以在不改动业务代码的同时方便扩展**

---



1. 如何使用

```
ExposeConfig config = new ExposeConfig();
        config.addConnectionEvent(new ConnectionEventListener() {
            @Override
            public void onConnect(boolean connected, BinLogInfo binLogInfo) {
                // 连接时
                if (connected){
                    System.out.println(binLogInfo.getBinlogFileName()+"----"+binLogInfo.getBinlogPosition());
                    // 关闭时
                }else {
                    System.out.println(binLogInfo.getBinlogFileName()+"----"+binLogInfo.getBinlogPosition());
                }
            }
        });
        config.addEventInfoMergeListener(new EventInfoMergeListener() {
            // 初始化时执行
            @Override
            public void onEvent(BaseEventInfoMerge eventInfoMerge) {
                EventInfoWrap parse = eventInfoMerge.parse();
                Iterator<EventInfoExtension> iterator = parse.iterator();
                while (iterator.hasNext()){
                    EventInfoExtension next = iterator.next();
                    next.getCurrentStep();// 获取步骤数
                    next.getStepCount();// 获取总步骤数
                    next.getEventInfo();// 获取步骤EventHeader EventData nextPosition 和 SqlType
                }
            }

            // 执行非InnoDb的DML语句时触发
            @Override
            public void onEvent(CommitDMLEventInfoMerge commitDMLEventInfoMerge) {
                // 省略。。。
            }

            // 执行DDL语句触发
            @Override
            public void onEvent(DDLEventInfoMerge ddlEventInfoMerge) {
                // 省略。。。
            }

            // 执行DML语句触发
            @Override
            public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
                 // 省略。。。
            }

            // 执行InnoDb的DML语句时触发
            @Override
            public void onEvent(XidDMLEventInfoMerge xidDMLEventInfoMerge) {
                 // 省略。。。
            }
        });


        BinaryLogClientBuild build = new BinaryLogClientBuild();
        build.setHostname("*")
                .setUsername("*")
                .setPassword("*")
                // 这个就是DB，如果不设置，就监听这个数据库的所有DB
                .setSchema("*");
                        // 设置binlog文件信息
//                .setBinlogFilename()
        // 设置binlog position的位置
//                .setBinlogPosition();

        Expose expose = new Expose(config).build(build);

        new Thread(() -> {
            try {
                expose.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        // ....
        
        expose.disconnect();
```

2. 使用DEMO<br>
2.1. 创建需要的表

```
CREATE TABLE `test_alter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `change` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `test_binlog_inno` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `age` int(10) DEFAULT NULL,
  `money` decimal(10,3) DEFAULT NULL,
  `ct` datetime DEFAULT CURRENT_TIMESTAMP,
  `ut` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



CREATE TABLE `test_binlog_mysiam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `age` int(10) DEFAULT NULL,
  `money` decimal(10,3) DEFAULT NULL,
  `ct` datetime DEFAULT CURRENT_TIMESTAMP,
  `ut` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

```

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.2. 使用过程

步骤1：创建<2.1>的三个表,结果：触发ddlEventInfoMerge事件，执行语句

```
System.out.println(ddlEventInfoMerge.parse().toString());
```
输出信息：<br>

```
DefaultEventInfoExtension{stepCount=1, currentStep=1, lastStep=true, firstStep=true, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573619390000, eventType=QUERY, serverId=1, headerLength=19, dataLength=224, nextPosition=510325689, flags=0}, eventData=QueryEventData{threadId=3107, executionTime=0, errorCode=0, database='t_move', sql='CREATE TABLE `test_alter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `change` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4'}, sqlType=DDL_CREATE}}

DefaultEventInfoExtension{stepCount=1, currentStep=1, lastStep=true, firstStep=true, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573619390000, eventType=QUERY, serverId=1, headerLength=19, dataLength=418, nextPosition=510326126, flags=0}, eventData=QueryEventData{threadId=3107, executionTime=0, errorCode=0, database='t_move', sql='CREATE TABLE `test_binlog_inno` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `age` int(10) DEFAULT NULL,
  `money` decimal(10,3) DEFAULT NULL,
  `ct` datetime DEFAULT CURRENT_TIMESTAMP,
  `ut` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4'}, sqlType=DDL_CREATE}}

DefaultEventInfoExtension{stepCount=1, currentStep=1, lastStep=true, firstStep=true, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573619390000, eventType=QUERY, serverId=1, headerLength=19, dataLength=420, nextPosition=510326565, flags=0}, eventData=QueryEventData{threadId=3107, executionTime=0, errorCode=0, database='t_move', sql='CREATE TABLE `test_binlog_mysiam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `age` int(10) DEFAULT NULL,
  `money` decimal(10,3) DEFAULT NULL,
  `ct` datetime DEFAULT CURRENT_TIMESTAMP,
  `ut` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4'}, sqlType=DDL_CREATE}}
```

步骤2：执行如下SQL，触发DML事件[**onEvent(DMLEventInfoMerge dmlEventInfoMerge)**  和**onEvent(XidDMLEventInfoMerge xidDMLEventInfoMerge)**],由于是事务类型，因此触发了*xidDMLEventInfoMerge*(非事务类型触发*commitDMLEventInfoMerge*)

SQL:
```
BEGIN;
INSERT INTO `test_binlog_inno`(`name`) VALUES('小1'),('小2');
INSERT INTO `test_binlog_inno`(`name`) VALUES('小3');
UPDATE test_binlog_inno SET `name` = '修改后' WHERE `id` = 3;
DELETE FROM test_binlog_inno where id = 1;
COMMIT;
```

**输出示例1**:

```
 public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
                System.out.println(dmlEventInfoMerge.parse().toString());
            }
```

内容:

```
DefaultEventInfoExtension{stepCount=10, currentStep=1, lastStep=false, firstStep=true, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=QUERY, serverId=1, headerLength=19, dataLength=63, nextPosition=510326647, flags=8}, eventData=QueryEventData{threadId=3113, executionTime=0, errorCode=0, database='t_move', sql='BEGIN'}, sqlType=DML_BEGIN}}
DefaultEventInfoExtension{stepCount=10, currentStep=2, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=TABLE_MAP, serverId=1, headerLength=19, dataLength=53, nextPosition=510326719, flags=0}, eventData=TableMapEventData{tableId=95, database='t_move', table='test_binlog_inno', columnTypes=3, 15, 3, -10, 18, 18, columnMetadata=0, 80, 0, 778, 0, 0, columnNullability={1, 2, 3, 4, 5}, eventMetadata=null}, sqlType=DML_TABLE_MAP}}
DefaultEventInfoExtension{stepCount=10, currentStep=3, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=EXT_WRITE_ROWS, serverId=1, headerLength=19, dataLength=56, nextPosition=510326794, flags=0}, eventData=WriteRowsEventData{tableId=95, includedColumns={0, 1, 2, 3, 4, 5}, rows=[
    [1, 小1, null, null, Wed Nov 13 14:13:44 CST 2019, Wed Nov 13 14:13:44 CST 2019],
    [2, 小2, null, null, Wed Nov 13 14:13:44 CST 2019, Wed Nov 13 14:13:44 CST 2019]
]}, sqlType=DML_INSERT}}
DefaultEventInfoExtension{stepCount=10, currentStep=4, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=TABLE_MAP, serverId=1, headerLength=19, dataLength=53, nextPosition=510326866, flags=0}, eventData=TableMapEventData{tableId=95, database='t_move', table='test_binlog_inno', columnTypes=3, 15, 3, -10, 18, 18, columnMetadata=0, 80, 0, 778, 0, 0, columnNullability={1, 2, 3, 4, 5}, eventMetadata=null}, sqlType=DML_TABLE_MAP}}
DefaultEventInfoExtension{stepCount=10, currentStep=5, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=EXT_WRITE_ROWS, serverId=1, headerLength=19, dataLength=36, nextPosition=510326921, flags=0}, eventData=WriteRowsEventData{tableId=95, includedColumns={0, 1, 2, 3, 4, 5}, rows=[
    [3, 小3, null, null, Wed Nov 13 14:13:44 CST 2019, Wed Nov 13 14:13:44 CST 2019]
]}, sqlType=DML_INSERT}}
DefaultEventInfoExtension{stepCount=10, currentStep=6, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=TABLE_MAP, serverId=1, headerLength=19, dataLength=53, nextPosition=510326993, flags=0}, eventData=TableMapEventData{tableId=95, database='t_move', table='test_binlog_inno', columnTypes=3, 15, 3, -10, 18, 18, columnMetadata=0, 80, 0, 778, 0, 0, columnNullability={1, 2, 3, 4, 5}, eventMetadata=null}, sqlType=DML_TABLE_MAP}}
DefaultEventInfoExtension{stepCount=10, currentStep=7, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=EXT_UPDATE_ROWS, serverId=1, headerLength=19, dataLength=62, nextPosition=510327074, flags=0}, eventData=UpdateRowsEventData{tableId=95, includedColumnsBeforeUpdate={0, 1, 2, 3, 4, 5}, includedColumns={0, 1, 2, 3, 4, 5}, rows=[
    {before=[3, 小3, null, null, Wed Nov 13 14:13:44 CST 2019, Wed Nov 13 14:13:44 CST 2019], after=[3, 修改后, null, null, Wed Nov 13 14:13:44 CST 2019, Wed Nov 13 14:13:44 CST 2019]}
]}, sqlType=DML_UPDATE}}
DefaultEventInfoExtension{stepCount=10, currentStep=8, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=TABLE_MAP, serverId=1, headerLength=19, dataLength=53, nextPosition=510327146, flags=0}, eventData=TableMapEventData{tableId=95, database='t_move', table='test_binlog_inno', columnTypes=3, 15, 3, -10, 18, 18, columnMetadata=0, 80, 0, 778, 0, 0, columnNullability={1, 2, 3, 4, 5}, eventMetadata=null}, sqlType=DML_TABLE_MAP}}
DefaultEventInfoExtension{stepCount=10, currentStep=9, lastStep=false, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625624000, eventType=EXT_DELETE_ROWS, serverId=1, headerLength=19, dataLength=36, nextPosition=510327201, flags=0}, eventData=DeleteRowsEventData{tableId=95, includedColumns={0, 1, 2, 3, 4, 5}, rows=[
    [1, 小1, null, null, Wed Nov 13 14:13:44 CST 2019, Wed Nov 13 14:13:44 CST 2019]
]}, sqlType=DML_DELETE}}
DefaultEventInfoExtension{stepCount=10, currentStep=10, lastStep=true, firstStep=false, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573625625000, eventType=XID, serverId=1, headerLength=19, dataLength=12, nextPosition=510327232, flags=0}, eventData=XidEventData{xid=9845525}, sqlType=DML_XID}}


```

**输出示例2**:

```
    public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
        List<EventTableInfo> historyEventTableInfos = dmlEventInfoMerge.getHistoryEventTableInfos();
        for (EventTableInfo eventTableInfo : historyEventTableInfos) {
            System.out.println(eventTableInfo.getTable()); // 获取表名
            System.out.println(eventTableInfo.getSqlType()); // 获取DML信息
            System.out.println(eventTableInfo.getDatabase()); // 获取db的名字
            for (List<TableElement> tableElements: eventTableInfo.getTableElements()) {
                for (TableElement tableElement : tableElements) {
                    System.out.println(tableElement); //获取表信息
                }
            }
        }
    }
```
内容：

```
test_binlog_inno
DML_INSERT
t_move
TableElement{index=1, type=java.lang.Integer, obj=1, mysqlType=INT, columns=Columns{columnName='id', columnDefault='null', isNullable='NO', dataType='int', columnType='int(11)', columnKey='PRI'}}
TableElement{index=2, type=java.lang.String, obj=小1, mysqlType=VARCHAR, columns=Columns{columnName='name', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=3, type=null, obj=null, mysqlType=INT, columns=Columns{columnName='age', columnDefault='null', isNullable='YES', dataType='int', columnType='int(10)', columnKey=''}}
TableElement{index=4, type=null, obj=null, mysqlType=DECIMAL, columns=Columns{columnName='money', columnDefault='null', isNullable='YES', dataType='decimal', columnType='decimal(10,3)', columnKey=''}}
TableElement{index=5, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ct', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=6, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ut', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=1, type=java.lang.Integer, obj=2, mysqlType=INT, columns=Columns{columnName='id', columnDefault='null', isNullable='NO', dataType='int', columnType='int(11)', columnKey='PRI'}}
TableElement{index=2, type=java.lang.String, obj=小2, mysqlType=VARCHAR, columns=Columns{columnName='name', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=3, type=null, obj=null, mysqlType=INT, columns=Columns{columnName='age', columnDefault='null', isNullable='YES', dataType='int', columnType='int(10)', columnKey=''}}
TableElement{index=4, type=null, obj=null, mysqlType=DECIMAL, columns=Columns{columnName='money', columnDefault='null', isNullable='YES', dataType='decimal', columnType='decimal(10,3)', columnKey=''}}
TableElement{index=5, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ct', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=6, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ut', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
test_binlog_inno
DML_INSERT
t_move
TableElement{index=1, type=java.lang.Integer, obj=3, mysqlType=INT, columns=Columns{columnName='id', columnDefault='null', isNullable='NO', dataType='int', columnType='int(11)', columnKey='PRI'}}
TableElement{index=2, type=java.lang.String, obj=小3, mysqlType=VARCHAR, columns=Columns{columnName='name', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=3, type=null, obj=null, mysqlType=INT, columns=Columns{columnName='age', columnDefault='null', isNullable='YES', dataType='int', columnType='int(10)', columnKey=''}}
TableElement{index=4, type=null, obj=null, mysqlType=DECIMAL, columns=Columns{columnName='money', columnDefault='null', isNullable='YES', dataType='decimal', columnType='decimal(10,3)', columnKey=''}}
TableElement{index=5, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ct', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=6, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ut', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
test_binlog_inno
DML_UPDATE
t_move
TableElement{index=1, type=[java.lang.Integer , java.lang.Integer], obj=[3 , 3], mysqlType=INT, columns=Columns{columnName='id', columnDefault='null', isNullable='NO', dataType='int', columnType='int(11)', columnKey='PRI'}}
TableElement{index=2, type=[java.lang.String , java.lang.String], obj=[小3 , 修改后], mysqlType=VARCHAR, columns=Columns{columnName='name', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=3, type=[null , null], obj=[null , null], mysqlType=INT, columns=Columns{columnName='age', columnDefault='null', isNullable='YES', dataType='int', columnType='int(10)', columnKey=''}}
TableElement{index=4, type=[null , null], obj=[null , null], mysqlType=DECIMAL, columns=Columns{columnName='money', columnDefault='null', isNullable='YES', dataType='decimal', columnType='decimal(10,3)', columnKey=''}}
TableElement{index=5, type=[java.util.Date , java.util.Date], obj=[Wed Nov 13 14:13:44 CST 2019 , Wed Nov 13 14:13:44 CST 2019], mysqlType=DATETIME, columns=Columns{columnName='ct', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=6, type=[java.util.Date , java.util.Date], obj=[Wed Nov 13 14:13:44 CST 2019 , Wed Nov 13 14:13:44 CST 2019], mysqlType=DATETIME, columns=Columns{columnName='ut', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
test_binlog_inno
DML_DELETE
t_move
TableElement{index=1, type=java.lang.Integer, obj=1, mysqlType=INT, columns=Columns{columnName='id', columnDefault='null', isNullable='NO', dataType='int', columnType='int(11)', columnKey='PRI'}}
TableElement{index=2, type=java.lang.String, obj=小1, mysqlType=VARCHAR, columns=Columns{columnName='name', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=3, type=null, obj=null, mysqlType=INT, columns=Columns{columnName='age', columnDefault='null', isNullable='YES', dataType='int', columnType='int(10)', columnKey=''}}
TableElement{index=4, type=null, obj=null, mysqlType=DECIMAL, columns=Columns{columnName='money', columnDefault='null', isNullable='YES', dataType='decimal', columnType='decimal(10,3)', columnKey=''}}
TableElement{index=5, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ct', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=6, type=java.util.Date, obj=Wed Nov 13 14:13:44 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ut', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}

```

> 根据<2.2>步骤2的SQL语句，这是一个事务类型，由此下面方法同样可以监听
```
            public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
//                List<EventTableInfo> historyEventTableInfos = dmlEventInfoMerge.getHistoryEventTableInfos();
//                for (EventTableInfo eventTableInfo : historyEventTableInfos) {
//                    System.out.println(eventTableInfo.getTable()); // 获取表名
//                    System.out.println(eventTableInfo.getSqlType()); // 获取DML信息
//                    System.out.println(eventTableInfo.getDatabase()); // 获取db的名字
//                    for (List<TableElement> tableElements: eventTableInfo.getTableElements()) {
//                        for (TableElement tableElement : tableElements) {
//                            System.out.println(tableElement); //获取表信息
//                        }
//                    }
//                }
            }
```


步骤3：执行如下SQL，触发DML事件[**onEvent(DMLEventInfoMerge dmlEventInfoMerge)**  和**onEvent(CommitDMLEventInfoMerge  commitDMLEventInfoMerge)**],由于是非事务类型，因此触发了==commitDMLEventInfoMerge==(事务类型触发==xidDMLEventInfoMerge==)

SQL:
```
BEGIN;
INSERT INTO `test_binlog_mysiam`(`name`) VALUES('小1'),('小2');
INSERT INTO `test_binlog_mysiam`(`name`) VALUES('小3');
UPDATE test_binlog_mysiam SET `name` = '修改后' WHERE `id` = 3;
DELETE FROM test_binlog_mysiam where id = 1;
COMMIT;
```

> 虽然开启了事务，但是由于不是innodb类型的表，事务开启会无效

**输出所在方法**:

```
    public void onEvent(CommitDMLEventInfoMerge commitDMLEventInfoMerge) {
    }

    public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
    }
```

步骤3: 执行test_binlog_inno的字段name为name1,age为age1,并且将age改成varchar（20）

SQL:
```
ALTER TABLE `test_binlog_inno`
CHANGE COLUMN `name` `name1`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `id`,
CHANGE COLUMN `age` `age1`  varchar(20) NULL DEFAULT NULL AFTER `name1`;

```

监听方法:
```
    public void onEvent(DDLEventInfoMerge ddlEventInfoMerge) {
        System.out.println(ddlEventInfoMerge.parse().toString());
    }
```

输出内容：
```
DefaultEventInfoExtension{stepCount=1, currentStep=1, lastStep=true, firstStep=true, eventInfo=EventInfo{eventHeader=EventHeaderV4{timestamp=1573628279000, eventType=QUERY, serverId=1, headerLength=19, dataLength=284, nextPosition=510328741, flags=0}, eventData=QueryEventData{threadId=3111, executionTime=0, errorCode=0, database='t_move', sql='ALTER TABLE `test_binlog_inno`
CHANGE COLUMN `name` `name1`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `id`,
CHANGE COLUMN `age` `age1`  varchar(20) NULL DEFAULT NULL AFTER `name1`'}, sqlType=DDL_ALTER}}
```

再次执行SQL：

```
INSERT INTO `test_binlog_mysiam`(`name`) VALUES('小4');

```

监听方法:
```
    public void onEvent(DMLEventInfoMerge dmlEventInfoMerge) {
        List<EventTableInfo> historyEventTableInfos = dmlEventInfoMerge.getHistoryEventTableInfos();
                for (EventTableInfo eventTableInfo : historyEventTableInfos) {
                    System.out.println(eventTableInfo.getTable()); // 获取表名
                    System.out.println(eventTableInfo.getSqlType()); // 获取DML信息
                    System.out.println(eventTableInfo.getDatabase()); // 获取db的名字
                    for (List<TableElement> tableElements: eventTableInfo.getTableElements()) {
                        for (TableElement tableElement : tableElements) {
                            System.out.println(tableElement); //获取表信息
                        }
                    }
                }
    }
```

输出内容：
```
test_binlog_inno
DML_INSERT
t_move
TableElement{index=1, type=java.lang.Integer, obj=4, mysqlType=INT, columns=Columns{columnName='id', columnDefault='null', isNullable='NO', dataType='int', columnType='int(11)', columnKey='PRI'}}
TableElement{index=2, type=java.lang.String, obj=小4, mysqlType=VARCHAR, columns=Columns{columnName='name1', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=3, type=null, obj=null, mysqlType=VARCHAR, columns=Columns{columnName='age1', columnDefault='null', isNullable='YES', dataType='varchar', columnType='varchar(20)', columnKey=''}}
TableElement{index=4, type=null, obj=null, mysqlType=DECIMAL, columns=Columns{columnName='money', columnDefault='null', isNullable='YES', dataType='decimal', columnType='decimal(10,3)', columnKey=''}}
TableElement{index=5, type=java.util.Date, obj=Wed Nov 13 15:25:50 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ct', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}
TableElement{index=6, type=java.util.Date, obj=Wed Nov 13 15:25:50 CST 2019, mysqlType=DATETIME, columns=Columns{columnName='ut', columnDefault='CURRENT_TIMESTAMP', isNullable='YES', dataType='datetime', columnType='datetime', columnKey=''}}

```


因此功能覆盖了监听DDL和DML，并且可以通过
```
eventInfoMerge.getCurrentPosition()
和
eventInfoMerge.getNextPosition()
```
来获取事件变化前和变化后的binlogPosition节点。

如使用中有问题，欢迎email: 742972035@qq.com
