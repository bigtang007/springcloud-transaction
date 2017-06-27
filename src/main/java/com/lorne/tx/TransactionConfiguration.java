package com.lorne.tx;

import com.lorne.tx.feign.TransactionRestTemplateInterceptor;
import feign.Feign;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by lorne on 2017/6/26.
 */

@Configuration
@ComponentScan
@EnableFeignClients
public class TransactionConfiguration {

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder().requestInterceptor(new TransactionRestTemplateInterceptor());
    }


}
