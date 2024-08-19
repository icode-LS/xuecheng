package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "课程老师管理", tags = "课程老师管理")
@RestController
@RequestMapping("/courseTeacher")
public class CourseTeacherController {


    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("课程教师列表")
    @GetMapping("/list/{courseId}")
    public List<CourseTeacher> teacherList(@PathVariable Long courseId){
        log.info("开始查询教师列表 === " + courseId);

        List<CourseTeacher> list = courseTeacherService.list(courseId);

        return list;
    }

    @ApiOperation("添加/更新教师")
    @PostMapping
    public void addOrUpdateTeacher(@RequestBody CourseTeacher courseTeacher){
        log.info("开始添加教师 {}", courseTeacher);

        //@TODO 改名字
        courseTeacherService.add(courseTeacher);

    }



    @ApiOperation("删除教师")
    @DeleteMapping("/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId, @PathVariable Long teacherId){
        log.info("开始删除教师  " + courseId + "  " + teacherId);

        courseTeacherService.delete(courseId, teacherId);

    }




}
