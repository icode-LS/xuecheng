package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程计划管理相关的接口
 * @date 2023/2/14 11:25
 */
@Slf4j
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

   @ApiOperation("查询课程计划树形结构")
   //查询课程计划  GET /teachplan/22/tree-nodes
   @GetMapping("/teachplan/{courseId}/tree-nodes")
 public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
       List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

       return teachplanTree;
   }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("teachplan/{id}")
    public void deleteTeachPlan(@PathVariable Long id){
       log.info("开始删除课程计划    =====  " + id);

       teachplanService.deleteTeachPlan(id);

    }

    @ApiOperation("向上移动")
    @PostMapping("teachplan/moveup/{id}")
    public void moveupTeachPlan(@PathVariable Long id){
       log.info("向上移动课程计划");
        teachplanService.moveTeachPlan(id, 1);
    }


    @ApiOperation("向下移动")
    @PostMapping("teachplan/movedown/{id}")
    public void movedownTeachPlan(@PathVariable Long id){
        log.info("向下移动课程计划");

        teachplanService.moveTeachPlan(id, 0);
    }

}
