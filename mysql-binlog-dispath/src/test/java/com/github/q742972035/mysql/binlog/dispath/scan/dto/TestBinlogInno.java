package com.github.q742972035.mysql.binlog.dispath.scan.dto;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: mysql-binlog-dispath
 * @description:
 * @author: 张忆
 * @create: 2019-11-17 07:34
 **/
@Table(name = "test_binlog_inno")
public class TestBinlogInno {

    private int id ;
    private String name1 ;
    private String age1 ;
    private BigDecimal money ;
    private Date ct ;
    private Date ut ;

    public int getId() {
        return id;
    }

    public TestBinlogInno setId(int id) {
        this.id = id;
        return this;
    }

    public String getName1() {
        return name1;
    }

    public TestBinlogInno setName1(String name1) {
        this.name1 = name1;
        return this;
    }

    public String getAge1() {
        return age1;
    }

    public TestBinlogInno setAge1(String age1) {
        this.age1 = age1;
        return this;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public TestBinlogInno setMoney(BigDecimal money) {
        this.money = money;
        return this;
    }

    public Date getCt() {
        return ct;
    }

    public TestBinlogInno setCt(Date ct) {
        this.ct = ct;
        return this;
    }

    public Date getUt() {
        return ut;
    }

    public TestBinlogInno setUt(Date ut) {
        this.ut = ut;
        return this;
    }

    @Override
    public String toString() {
        return "TestBinlogInno{" +
                "id=" + id +
                ", name1='" + name1 + '\'' +
                ", age1='" + age1 + '\'' +
                ", money=" + money +
                ", ct=" + ct +
                ", ut=" + ut +
                '}';
    }
}
