package com.lorne.tx.interceptor;

import com.lorne.tx.Constants;
import com.lorne.tx.annotation.TxTransaction;
import com.lorne.tx.bean.TransactionLocal;
import com.lorne.tx.bean.TxTransactionInfo;
import com.lorne.tx.bean.TxTransactionLocal;
import com.lorne.tx.service.TransactionServer;
import com.lorne.tx.service.TransactionServerFactoryService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Created by lorne on 2017/6/7.
 */
@Aspect
@Component
public class TxManagerInterceptor {


    @Autowired
    private TransactionServerFactoryService transactionServerFactoryService;


    @Around("@annotation(com.lorne.tx.annotation.TxTransaction)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();

        Method thisMethod =  clazz.getMethod(method.getName(),method.getParameterTypes());

        TxTransaction transaction =  thisMethod.getAnnotation(TxTransaction.class);

        TxTransactionLocal txTransactionLocal =  TxTransactionLocal.current();


        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = requestAttributes==null?null:((ServletRequestAttributes)requestAttributes).getRequest();

        String groupId = request == null?null:request.getHeader("tx-group");

        TransactionLocal transactionLocal = TransactionLocal.current();

        TxTransactionInfo state = new TxTransactionInfo(transaction, txTransactionLocal,groupId,transactionLocal);

        TransactionServer server =  transactionServerFactoryService.createTransactionServer(state);

        return server.execute(point,state);

    }
}
