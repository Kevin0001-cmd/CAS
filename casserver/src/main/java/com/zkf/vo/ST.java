package com.zkf.vo;

import javax.persistence.*;

@Entity
@Table(name = "service_ticket")
public class ST {
    //service_ticket, 小令牌
    private String st;
    //User.id
    @Column(name = "user_id")
    private String userId;
    //TGT.id，由哪个TGT签发
    @Column(name = "tgt_id")
    private String tgtId;
    //Service.id
    @Column(name = "service_id")
    private String serviceId;
    //使用次数，一般限制使用次数
    private int used;
    //过期时间
    private String expires_in;
    //是否有效(-1：无效，1：有效)
    private int validate;

    private String url;
    private int id;

    public ST() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }


    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
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

    public String getUserId() {
        return userId;
    }

    public ST(String st, String userId, String tgtId, String url, String serviceId, int used) {
        this.st = st;
        this.userId = userId;
        this.tgtId = tgtId;
        this.serviceId = serviceId;
        this.used = used;
        this.validate = validate;
        this.url = url;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTgtId() {
        return tgtId;
    }

    public void setTgtId(String tgtId) {
        this.tgtId = tgtId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }
}
