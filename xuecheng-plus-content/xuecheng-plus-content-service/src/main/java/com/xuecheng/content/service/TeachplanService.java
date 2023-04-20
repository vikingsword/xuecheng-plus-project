package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {

    /**
     * @param courseId 课程id
     * @return List<TeachplanDto>
     * @description 查询课程计划树型结构
     */
    List<TeachplanDto> findTeachplanTree(long courseId);


    /**
     * 新增修改保存课程计划
     *
     * @param teachplanDto
     */
    void saveTeachplan(SaveTeachplanDto teachplanDto);

    void deleteTeachplan(Long id);

    void moveTeachplan(String moveDirection, Long id);


    /**
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @description 教学计划绑定媒资
     * @author Mr.M
     * @date 2022/9/14 22:20
     */
    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}