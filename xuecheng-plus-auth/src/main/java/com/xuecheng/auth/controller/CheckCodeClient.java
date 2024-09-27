package com.xuecheng.auth.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "checkcode")
public interface CheckCodeClient {

    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code);
}
