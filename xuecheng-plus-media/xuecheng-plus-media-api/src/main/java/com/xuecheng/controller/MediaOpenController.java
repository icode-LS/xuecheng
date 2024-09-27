package com.xuecheng.controller;

import com.baomidou.mybatisplus.annotation.TableId;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.base.utils.StringUtil;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "媒资视频接口")
@Slf4j
@RestController
@RequestMapping("/open")
public class MediaOpenController {


    @Autowired
    private MediaFilesMapper mediaFilesMapper;


    @GetMapping("/preview/{mediaId}")
    @ApiOperation("获取媒资视频")
    public RestResponse<String> getMediaVideo(@PathVariable String mediaId){
        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
        if(mediaFiles == null ||
                StringUtil.isEmpty(mediaFiles.getUrl())){
            XueChengPlusException.cast("访问视频资源出错！");
        }

        return RestResponse.success(mediaFiles.getUrl());
    }


}
