package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublisherService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CoursePublisherServiceImpl implements CoursePublisherService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseTeacherService teacherService;

    @Override
    public CoursePreviewDto getCoursePreview(Long courseId) {
        CoursePreviewDto result = new CoursePreviewDto();

        // 获取课程基本信息和营销信息
        CourseBaseInfoDto courseBase = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 获取课程计划信息
        List<TeachplanDto> teachplanDtos = teachplanService.findTeachplanTree(courseId);
        // 获取课程师资信息
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = teacherService.list(courseId);

        result.setCourseBase(courseBase);
        result.setCourseTeachers(courseTeachers);
        result.setTeachplans(teachplanDtos);

        return  result;
    }
}
