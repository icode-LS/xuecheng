package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/14 12:11
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    private int getTeachplanCount(Long courseId,Long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return  count+1;
    }
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断是新增和修改
        Long teachplanId = saveTeachplanDto.getId();
        if(teachplanId ==null){
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定排序字段，找到它的同级节点个数，排序字段就是个数加1  select count(1) from teachplan where course_id=117 and parentid=268
            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int teachplanCount = getTeachplanCount(courseId, parentid);
            teachplan.setOrderby(teachplanCount);
            teachplanMapper.insert(teachplan);

        }else{
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            //将参数复制到teachplan
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }

    }

    @Transactional
    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //课程计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan == null){
            XueChengPlusException.cast("课程计划不存在");
        }

        //先删除原有记录,根据课程计划id删除它所绑定的媒资
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));

        //再添加新记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMediaMapper.insert(teachplanMedia);

    }

    @Override
    public void deleteTeachPlan(Long planId) {
        // 删除自身信息和子章节信息
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getId, planId)
                .or()
                .eq(Teachplan::getParentid, planId);

        teachplanMapper.delete(wrapper);
        // 删除关联的media信息

        LambdaQueryWrapper<TeachplanMedia> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(TeachplanMedia::getTeachplanId, planId);

        teachplanMediaMapper.delete(wrapper1);
    }

    @Override
    public void moveTeachPlan(Long planId, Integer type) {
        // 查询出课程计划
        Teachplan teachplan = teachplanMapper.selectById(planId);
        // 获取courseId grade和order
        Long courseId = teachplan.getCourseId(), parentId = teachplan.getParentid();
        Integer grade = teachplan.getGrade(), order = teachplan.getOrderby();
        // 向上移动
        Page<Teachplan> page = new Page<>(1,1);
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getGrade, grade)
                .eq(Teachplan::getParentid, parentId);
        if(type == 1){
            // 找到向上第一个
            wrapper.lt(Teachplan::getOrderby, order);

            Page<Teachplan> result = teachplanMapper.selectPage(page, wrapper);
            List<Teachplan> records = result.getRecords();
            //判断是否已经处于顶部
            if(records.isEmpty()){
                return;
            }
            Teachplan teachplan1 = records.get(0);
            //交换orderby
            teachplan.setOrderby(teachplan1.getOrderby());
            teachplan1.setOrderby(order);
            //更新
            teachplanMapper.updateById(teachplan1);
        }else{
            // 向下移动
            //找到向下第一个
            wrapper.gt(Teachplan::getOrderby, order);
            Page<Teachplan> result = teachplanMapper.selectPage(page, wrapper);
            List<Teachplan> records = result.getRecords();
            //判断是否已经处于顶部
            if(records.isEmpty()){
                return;
            }
            Teachplan teachplan1 = records.get(0);
            //交换orderby
            teachplan.setOrderby(teachplan1.getOrderby());
            teachplan1.setOrderby(order);
            //更新
            teachplanMapper.updateById(teachplan1);

        }
        teachplanMapper.updateById(teachplan);
    }

}
