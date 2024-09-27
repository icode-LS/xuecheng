package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.JsonUtil;
import com.xuecheng.base.utils.StringUtil;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublisherService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CoursePublisherServiceImpl implements CoursePublisherService {

    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseTeacherService teacherService;

    @Autowired
    private CoursePublishMapper coursePublishMapper;


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

        return result;
    }

    @Override
    @Transactional
    public void commitAudit(Long courseId, Long companyId) {
        // 准备填入course_publisher_pre表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        // 获取要提交的课程
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        // 营销信息和基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 检查机构是否相同
        if (!Objects.equals(courseBase.getCompanyId(), companyId)) {
            XueChengPlusException.cast("非课程所属机构不能提交审核！");
        }
        // 检查课程状态是否是已提交
        if (courseBase.getAuditStatus().equals("202003")) {
            XueChengPlusException.cast("课程正在审核中！");
        }
        // 没有课程图片不能提交审核
        if (StringUtil.isEmpty(courseBase.getPic())) {
            XueChengPlusException.cast("未添加课程图片!");
        }
        //获取课程计划
        List<TeachplanDto> teachPlans = teachplanService.findTeachplanTree(courseId);
        // 如果计划为空则不可发布
        if (teachPlans.isEmpty()) {
            XueChengPlusException.cast("未添加课程计划！不可发布");
        }
        // 转换为json字符串
        String teachPlanStr = JsonUtil.objectTojson(teachPlans);
        // 获取课程老师
        String teachers = JsonUtil.objectTojson(teacherService.list(courseId));
        // 获取营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 获取大小分类名称
        CourseCategory mt = courseCategoryMapper.selectById(courseBaseInfo.getMt());
        CourseCategory st = courseCategoryMapper.selectById(courseBaseInfo.getSt());
        // 设置属性值
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        coursePublishPre.setMtName(mt.getName());
        coursePublishPre.setStName(st.getName());
        coursePublishPre.setTeachers(teachers);
        coursePublishPre.setTeachplan(teachPlanStr);
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setMarket(JsonUtil.objectTojson(courseMarket));
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setStatus("202003");
        // 检查是否更新
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        // 设置课程审核状态为已提交
        courseBase.setAuditStatus("202003");
        // 课程状态
        courseBase.setStatus("202001");
        courseBaseMapper.updateById(courseBase);
    }

    @Override
    @Transactional
    public void coursePublish(Long courseId, Long companyId) {
        //约束校验
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            XueChengPlusException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("不允许提交其它机构的课程。");
        }
        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            XueChengPlusException.cast("操作失败，课程审核通过方可发布。");
        }
        // 赋值发布实体
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setCreateDate(LocalDateTime.now());
        coursePublish.setOnlineDate(LocalDateTime.now());
        coursePublish.setStatus("203002");
        // 新增课程发布
        coursePublishMapper.insert(coursePublish);
        // 修改course表内的状态
        CourseBase courseBase = new CourseBase();
        courseBase.setStatus("203002");
        courseBase.setId(courseId);
        courseBaseMapper.updateById(courseBase);
        // 插入message表
        mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        // 删除课程预发布表内容
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) throws IOException, TemplateException {
        // 进行课程静态化处理
        //配置freemarker
        Configuration configuration = new Configuration(Configuration.getVersion());

        //加载模板
        //选指定模板路径,classpath下templates下
        //得到classpath路径
        String classpath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //设置字符编码
        configuration.setDefaultEncoding("utf-8");

        //指定模板文件名称
        Template template = configuration.getTemplate("course_template.ftl");

        //准备数据
        CoursePreviewDto coursePreviewInfo = this.getCoursePreview(courseId);

        Map<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewInfo);

        //静态化
        //参数1：模板，参数2：数据模型
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        System.out.println(content);
        //将静态化内容输出到文件中
        InputStream inputStream = IOUtils.toInputStream(content);
        // 获取multipart对象
        File htmlFile = File.createTempFile("coursePublish", ".html");
        FileOutputStream outputStream = new FileOutputStream(htmlFile);
        IOUtils.copy(inputStream, outputStream);

        return htmlFile;

    }

    @Override
    public void uploadHtml(Long courseId, File file) {
        // 获取multipart 文件
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        // 调用mediaClient
//        String s = mediaServiceClient.uploadFile(multipartFile, "/course" + courseId + ".html");

        // 降级判断....


    }

}
