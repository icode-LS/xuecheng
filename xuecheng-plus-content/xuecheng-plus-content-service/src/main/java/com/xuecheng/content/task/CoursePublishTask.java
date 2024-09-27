package com.xuecheng.content.task;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublisherService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublisherService coursePublisherService;

    @XxlJob("CoursePublishJob")
    public void xxlJobCoursePublish(){
        // 获取分片和总数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        // @todo 配置执行器和创建执行器

        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        // 获取根据id获取课程信息
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        // 页面静态化
        try {
            generateCourseHtml(courseId, mqMessage);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        // 写es索引
        createEsIndex(courseId, mqMessage);
        // 写入redis缓存
        createRedisCache(courseId, mqMessage);
        return false;
    }

    private void generateCourseHtml(Long courseId, MqMessage message) throws IOException, TemplateException {
        // 获取任务
        Long taskId = message.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 判断任务阶段是否完成，保证幂等性
        int stageOne = mqMessageService.getStageOne(taskId);
        if(stageOne > 0){
            return;
        }

        // 生成临时html文件@todo

        // 上传html文件到minio@todo

        // 完成
        mqMessageService.completedStageOne(taskId);

    }

    private void createEsIndex(Long courseId, MqMessage message){
        // 获取任务
        Long taskId = message.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 判断任务阶段是否完成，保证幂等性
        int stageOne = mqMessageService.getStageTwo(taskId);
        if(stageOne > 0){
            return;
        }

        // 添加es索引处理


        // 完成
        mqMessageService.completedStageTwo(taskId);
    }

    private void createRedisCache(Long courseId, MqMessage message){
        // 获取任务
        Long taskId = message.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 判断任务阶段是否完成，保证幂等性
        int stageOne = mqMessageService.getStageThree(taskId);
        if(stageOne > 0){
            return;
        }

        // 添加redis索引


        // 完成
        mqMessageService.completedStageThree(taskId);
    }




}
