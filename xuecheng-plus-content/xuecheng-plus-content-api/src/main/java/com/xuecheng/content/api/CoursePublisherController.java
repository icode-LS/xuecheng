package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublisherService;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Api(tags = "课程发布接口")
@Slf4j
@RestController
public class CoursePublisherController {


    @Autowired
    private CoursePublisherService coursePublisherService;

    @GetMapping("/coursepreview/{id}")
    @ApiOperation("课程预览")
    public ModelAndView preview(@PathVariable("id") Long id){
        log.info("开始课程预览 " + id);

        ModelAndView modelAndView = new ModelAndView();
        CoursePreviewDto coursePreview = coursePublisherService.getCoursePreview(id);


        modelAndView.addObject("model", coursePreview);
        modelAndView.setViewName("course_template");

        return modelAndView;
    }

}
