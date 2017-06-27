package com.lorne.tx.client;

import com.lorne.tx.client.model.TxGroup;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by lorne on 2017/6/7.
 */
@FeignClient(value = "tx-manager")
public interface TxManagerService {


    /**
     * 创建事务组
     * @return
     */
    @RequestMapping("/tx/manager/createTransactionGroup")
    TxGroup createTransactionGroup(@RequestParam("port") int port);


    /**
     * 添加事务组子对象
     * @return
     */
    @RequestMapping("/tx/manager/addTransactionGroup")
    TxGroup addTransactionGroup(@RequestParam("groupId") String groupId,@RequestParam("taskId") String taskId,@RequestParam("port") int port);


    /**
     * 关闭事务组-进入事务提交第一阶段
     * @param groupId
     * @return
     */
    @RequestMapping("/tx/manager/closeTransactionGroup")
    boolean closeTransactionGroup(@RequestParam("groupId") String groupId);


    /**
     * 通知事务组事务执行状态
     * @param groupId
     * @param kid
     * @param state
     * @return
     */
    @RequestMapping("/tx/manager/notifyTransactionInfo")
    boolean notifyTransactionInfo(@RequestParam("groupId") String groupId,@RequestParam("kid") String kid,@RequestParam("state") boolean state);
}
