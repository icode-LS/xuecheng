package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {

    private CourseBaseInfoDto courseBase;

    private List<TeachplanDto> teachplans;

    private List<CourseTeacher> courseTeachers;

}
