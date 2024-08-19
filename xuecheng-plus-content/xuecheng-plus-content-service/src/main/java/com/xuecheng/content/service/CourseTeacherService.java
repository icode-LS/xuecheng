package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {

    List<CourseTeacher> list(Long courseId);

    void add(CourseTeacher courseTeacher);

    void delete(Long courseId, Long teacherId);

}
