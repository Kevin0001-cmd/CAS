package com.zkf.vo;

import javax.persistence.*;

@Entity
@Table(name = "ticket_granted_ticket")
public class TGT {
    //ticket_granted_ticket, 大令牌
    private String tgt;
    //User.id
    private String user_id;
    //过期时间
    private String expires_in;
    //是否有效(-1：无效，1：有效)
    private int validate;
    private int id;

    public TGT() {

    }

    public void setId(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public String getTgt() {
        return tgt;
    }

    public void setTgt(String tgt) {
        this.tgt = tgt;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public int getValidate() {
        return validate;
    }

    public void setValidate(int validate) {
        this.validate = validate;
    }

    public TGT(String tgt, String user_id) {
        this.tgt = tgt;
        this.user_id = user_id;
    }
}
