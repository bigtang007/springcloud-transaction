package com.lorne.tx.service.model;

import com.lorne.tx.client.model.TxGroup;
import com.lorne.core.framework.utils.task.Task;
import org.springframework.transaction.TransactionStatus;

/**
 * Created by lorne on 2017/6/9.
 */
public class ServiceThreadModel {

    private Task waitTask;
    private TransactionStatus status;
    private TxGroup txGroup;


    public Task getWaitTask() {
        return waitTask;
    }

    public void setWaitTask(Task waitTask) {
        this.waitTask = waitTask;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }


    public TxGroup getTxGroup() {
        return txGroup;
    }

    public void setTxGroup(TxGroup txGroup) {
        this.txGroup = txGroup;
    }
}
