package com.lorne.tx.client.model;


import com.lorne.core.framework.model.JsonModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lorne on 2017/6/7.
 */
public class TxGroup extends JsonModel{


    private String groupId;

    private String providerUrl;

    private int waitTime;

    private boolean hasOver = false;

    public boolean isHasOver() {
        return hasOver;
    }

    public void hasOvered() {
        this.hasOver = true;
    }

    private List<TxInfo> list;

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public TxGroup() {
        list = new ArrayList<TxInfo>();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public void addTransactionInfo(TxInfo info){
        if(!hasOver){
            list.add(info);
        }
    }

    public void setHasOver(boolean hasOver) {
        this.hasOver = hasOver;
    }

    public void setList(List<TxInfo> list) {
        this.list = list;
    }

    public List<TxInfo> getList() {
        return list;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

}
