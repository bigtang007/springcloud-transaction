package com.lorne.tx.client.model;


import com.lorne.core.framework.model.JsonModel;

/**
 * Created by lorne on 2017/6/7.
 */
public class TxInfo extends JsonModel{

    private String kid;

    private String url;


    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
