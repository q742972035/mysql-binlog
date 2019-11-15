package zy.opensource.mysql.binlog.incr.expose.type.sql;


/**
 * @program: mysql-binlog-incr-expose
 * @description:
 * @author: 张忆
 * @create: 2019-08-03 00:22
 **/
public interface SqlType {

    String DML_INSERT = "DML_INSERT";
    String DML_UPDATE = "DML_UPDATE";
    String DML_DELETE = "DML_DELETE";
    String DML_BEGIN = "DML_BEGIN";
    String DML_XID = "DML_XID";
    String DML_COMMIT = "DML_COMMIT";
    String DML_TABLE_MAP = "DML_TABLE_MAP";


    String DDL_CREATE = "DDL_CREATE";
    String DDL_ALTER = "DDL_ALTER";
    String DDL_DROP = "DDL_DROP";
    String DDL_TRUNCATE = "DDL_TRUNCATE";

    String BASE_ROTATE = "BASE_ROTATE";
    String BASE_FORMATD_ESCRIPTION = "BASE_FORMATD_ESCRIPTION";


    SqlType NONE = new SqlType() {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String toString() {
            return "null";
        }
    };


    enum BaseInfo implements SqlType {
        ROTATE(BASE_ROTATE), FORMATD_ESCRIPTION(BASE_FORMATD_ESCRIPTION);

        String name;

        BaseInfo(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    String getName();


    enum DDL implements SqlType {
        CREATE(DDL_CREATE),
        ALTER(DDL_ALTER),
        DROP(DDL_DROP),
        TRUNCATE(DDL_TRUNCATE),
        ;



        String name;

        DDL(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    enum DML implements SqlType {
        INSERT(DML_INSERT), UPDATE(DML_UPDATE), DELETE(DML_DELETE), BEGIN(DML_BEGIN),
        TABLE_MAP(DML_TABLE_MAP), XID(DML_XID), COMMIT(DML_COMMIT);

        String name;

        DML(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
