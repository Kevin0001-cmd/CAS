package com.zkf.vo;

import javax.persistence.*;

@Entity
@Table(name="service_ticket")
//小令牌表，业务系统记录ST的状态，用于根据ST删除对应的Session
public class ST {
    //service_ticket, 小令牌
    private String st;
    //是否有效(-1：无效，1：有效)
    private int validate;
    private int id;

    public ST() {

    }

    public void setId(int id) {
        this.id = id;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public ST(String st) {
        this.st = st;
    }

    public int getValidate() {
        return validate;
    }

    public void setValidate(int validate) {
        this.validate = validate;
    }
}
