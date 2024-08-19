package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> list(Long courseId) {
        // 查询
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId,courseId);

        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(wrapper);

        return courseTeachers;

    }

    @Override
    public void add(CourseTeacher courseTeacher) {
        if(courseTeacher.getId() != null){
            courseTeacherMapper.updateById(courseTeacher);
        }else{
            courseTeacher.setCreateDate(LocalDateTime.now());

            courseTeacherMapper.insert(courseTeacher);
        }
    }


    @Override
    public void delete(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId,courseId)
                .eq(CourseTeacher::getId, teacherId);

        courseTeacherMapper.delete(wrapper);
    }
}
