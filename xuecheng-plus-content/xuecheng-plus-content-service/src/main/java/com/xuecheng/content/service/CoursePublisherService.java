package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public interface CoursePublisherService {

    CoursePreviewDto getCoursePreview(Long courseId);

    void commitAudit(Long courseId, Long companyId);

    void coursePublish(Long courseId, Long companyId);

    File generateCourseHtml(Long courseId) throws IOException, TemplateException;

    void uploadHtml(Long courseId, File file);

}
