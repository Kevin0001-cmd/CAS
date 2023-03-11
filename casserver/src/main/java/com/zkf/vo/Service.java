package com.zkf.vo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service")
public class Service {
    // Service的业务路由
    private String url;
    //Service的退出路由
    private String logout_url;
    private String id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogout_url() {
        return logout_url;
    }

    public void setLogout_url(String logout_url) {
        this.logout_url = logout_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }
}
