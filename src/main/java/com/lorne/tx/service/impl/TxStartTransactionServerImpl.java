package com.lorne.tx.service.impl;

import com.lorne.tx.Constants;
import com.lorne.tx.bean.TxTransactionInfo;
import com.lorne.tx.bean.TxTransactionLocal;
import com.lorne.tx.client.model.TxGroup;
import com.lorne.tx.client.TxManagerService;
import com.lorne.tx.service.TransactionRunningService;
import com.lorne.tx.service.TransactionServer;
import com.lorne.tx.service.model.ServiceThreadModel;
//import com.lorne.tx.utils.DubboUtils;
import com.lorne.core.framework.utils.KidUtils;
import com.lorne.core.framework.utils.task.ConditionUtils;
import com.lorne.core.framework.utils.task.Task;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分布式事务启动开始时的业务处理
 * Created by lorne on 2017/6/8.
 */
@Service(value = "txStartTransactionServer")
public class TxStartTransactionServerImpl implements TransactionServer {


    private Logger logger = LoggerFactory.getLogger(TxStartTransactionServerImpl.class);


    @Autowired
    private TxManagerService txManagerService;


    @Autowired
    private TransactionRunningService transactionRunningService;



    @Override
    public Object execute(final ProceedingJoinPoint point, final TxTransactionInfo info)throws Throwable {
        //分布式事务开始执行
        logger.info("tx-start");

      // final String url = DubboUtils.getProviderUrl();

        TxGroup txGroup = txManagerService.createTransactionGroup(Constants.server_port);

        final String taskId = KidUtils.generateShortUuid();
        final Task task = ConditionUtils.getInstance().createTask(taskId);

        final String groupId = txGroup.getGroupId();


        Constants.threadPool.execute(new Runnable() {
            @Override
            public void run() {

                TxTransactionLocal txTransactionLocal = new TxTransactionLocal();
                txTransactionLocal.setGroupId(groupId);
                TxTransactionLocal.setCurrent(txTransactionLocal);


                logger.info("taskId-id-tx:"+taskId);

                boolean signTask = false;


                ServiceThreadModel model =  transactionRunningService.serviceInThread(signTask,groupId,task,point);


                txManagerService.closeTransactionGroup(groupId);

                transactionRunningService.serviceWait(signTask,task,model);

            }
        });

        task.awaitTask();

        logger.info("tx-end");
        //分布式事务执行完毕
        try{
            return task.getBack().doing();
        }finally {
            task.remove();
        }

    }
}
