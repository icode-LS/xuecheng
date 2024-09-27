package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublisherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "预览课程接口")
@Slf4j
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    private CoursePublisherService coursePublisherService;


    @GetMapping("/course/whole/{courseId}")
    @ApiOperation("详细")
    public CoursePreviewDto getCourseWhole(@PathVariable Long courseId){
        return coursePublisherService.getCoursePreview(courseId);
    }

}
