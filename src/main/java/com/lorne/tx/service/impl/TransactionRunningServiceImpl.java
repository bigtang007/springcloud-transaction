package com.lorne.tx.service.impl;


import com.lorne.core.framework.Constant;
import com.lorne.core.framework.utils.KidUtils;
import com.lorne.core.framework.utils.task.ConditionUtils;
import com.lorne.core.framework.utils.task.IBack;
import com.lorne.core.framework.utils.task.Task;
import com.lorne.tx.Constants;
import com.lorne.tx.client.TxManagerService;
import com.lorne.tx.client.model.TxGroup;
import com.lorne.tx.service.TransactionRunningService;
import com.lorne.tx.service.model.ServiceThreadModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.TimeUnit;

/**
 * Created by lorne on 2017/6/9.
 */
@Service
public class TransactionRunningServiceImpl implements TransactionRunningService {




    @Autowired
    private PlatformTransactionManager txManager;


    @Autowired
    private TxManagerService txManagerService;


    @Override
    public ServiceThreadModel serviceInThread(boolean signTask, String _groupId, Task task, ProceedingJoinPoint point) {


        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

        TransactionStatus status = txManager.getTransaction(def);

        String kid = KidUtils.generateShortUuid();

        Task waitTask = ConditionUtils.getInstance().createTask(kid);

        TxGroup txGroup =  txManagerService.addTransactionGroup(_groupId,kid,Constants.server_port);

        try {
            final Object res = point.proceed();
            task.setBack(new IBack() {
                @Override
                public Object doing(Object... objects) throws Throwable {
                    return res;
                }
            });
            //通知TxManager调用成功
            txManagerService.notifyTransactionInfo(_groupId,kid,true);
        } catch (final Throwable throwable) {
            task.setBack(new IBack() {
                @Override
                public Object doing(Object... objects) throws Throwable{
                    throw new Throwable(throwable);
                }
            });
            //通知TxManager调用失败
            txManagerService.notifyTransactionInfo(_groupId,kid,false);
        }

        if(signTask)
            task.signalTask();


        ServiceThreadModel model = new ServiceThreadModel();
        model.setStatus(status);
        model.setWaitTask(waitTask);
        model.setTxGroup(txGroup);

        return model;

    }


    @Override
    public void serviceWait(boolean signTask,Task task,ServiceThreadModel model) {
        Task waitTask = model.getWaitTask();
        final String taskId = waitTask.getKey();
        TransactionStatus status = model.getStatus();

        Constant.scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                Task task = ConditionUtils.getInstance().getTask(taskId);
                if(task.getState()==0) {
                    task.setBack(new IBack() {
                        @Override
                        public Object doing(Object... objects) throws Throwable {
                            return false;
                        }
                    });
                    task.signalTask();
                }
            }
        },model.getTxGroup().getWaitTime(), TimeUnit.SECONDS);

        waitTask.awaitTask();

        try {
            int state =  (Integer) waitTask.getBack().doing();
            if(state==1){
                txManager.commit(status);
                if(!signTask){
                    task.signalTask();
                }
            }else{
                txManager.rollback(status);
                if(state==-1){
                    task.setBack(new IBack() {
                        @Override
                        public Object doing(Object... objs) throws Throwable {
                             throw new Throwable("事务模块网络异常.");
                        }
                    });
                }
                if(!signTask){
                    task.signalTask();
                }
            }
        } catch (Throwable throwable) {
            txManager.rollback(status);
        }finally {
            waitTask.remove();
        }
    }


}
