package com.xuecheng.controller;

import com.xuecheng.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    MediaFileService mediaFileService;


    @GetMapping("/list")
    public String get(){
        return "请求成功！";
    }

}
