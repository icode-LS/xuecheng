package com.xuecheng.media.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.port}")
    private String port;

    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        Config config = new Config();
        //单机模式  依次设置redis地址和密码
        System.out.println(redisHost);
        // config.useSingleServer().setAddress("redis://" + redisHost + ":" + port).setPassword(redisPassword);
        // 没有配置redis密码
        config.useSingleServer().setAddress("redis://" + redisHost + ":" + port);
        return Redisson.create(config);
    }



}
