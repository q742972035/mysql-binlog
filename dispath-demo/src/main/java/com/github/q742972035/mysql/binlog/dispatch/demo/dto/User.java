package com.github.q742972035.mysql.binlog.dispatch.demo.dto;

import java.util.Date;

public class User {
    private Long id;
    @com.github.q742972035.mysql.binlog.dispatch.annotation.Column("username")
    private String un;
    @javax.persistence.Column(name = "email")
    private String email;
    private String phone;

    private Date create_time;
    private Date update_time;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
