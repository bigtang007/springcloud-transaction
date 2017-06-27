package com.lorne.tx.controller;

import com.lorne.tx.mq.service.MQTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lorne on 2017/6/27.
 */
@RestController
@RequestMapping("/tx/transaction")
public class TransactionController {


    @Autowired
    private MQTransactionService transactionService;


    @RequestMapping("/notify")
    @ResponseBody
    public boolean notify(@RequestParam("kid")String kid,@RequestParam("state") int state){
        return transactionService.notify(kid, state);
    }



    @RequestMapping("/checkRollback")
    @ResponseBody
    public boolean checkRollback(@RequestParam("kid")String kid){
        return transactionService.checkRollback(kid);
    }
}
