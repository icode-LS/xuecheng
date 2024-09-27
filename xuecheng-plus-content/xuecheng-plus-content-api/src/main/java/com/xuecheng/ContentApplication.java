package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Mr.M
 * @version 1.0
 * @description 内容管理服务启动类
 * @date 2023/2/11 15:49
 */
@EnableFeignClients(basePackages = {"com.xuecheng.content.feignclient"})
@EnableDiscoveryClient
@EnableSwagger2Doc
@EnableTransactionManagement
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {


        SpringApplication.run(ContentApplication.class, args);
    }
}
