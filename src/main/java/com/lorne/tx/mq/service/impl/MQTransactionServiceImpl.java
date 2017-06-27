package com.lorne.tx.mq.service.impl;

import com.lorne.tx.mq.service.MQTransactionService;
import com.lorne.core.framework.utils.task.ConditionUtils;
import com.lorne.core.framework.utils.task.IBack;
import com.lorne.core.framework.utils.task.Task;
import org.springframework.stereotype.Service;

/**
 * Created by lorne on 2017/6/7.
 */
@Service
public class MQTransactionServiceImpl implements MQTransactionService {


    /**
     *
     * @param kid
     * @param state
     * 1:提交
     * 0：回滚
     * -1:回滚 通知事务模块存在网络异常
     * @return
     */
    @Override
    public boolean notify(String kid, final int state) {
        Task task = ConditionUtils.getInstance().getTask(kid);
        if(task!=null){
            task.setBack(new IBack() {
                @Override
                public Object doing(Object... objects) throws Throwable {
                    return state;
                }
            });
            task.signalTask();

            return true;
        }
        return false;
    }



    @Override
    public boolean checkRollback(String kid) {
        Task task = ConditionUtils.getInstance().getTask(kid);
        if(task!=null){
            if(task.isNotify()){
                return true;
            }else{
                //task任务调度将不会被执行
                task.setState(1);
                return false;
            }
        }
        return true;
    }

}
