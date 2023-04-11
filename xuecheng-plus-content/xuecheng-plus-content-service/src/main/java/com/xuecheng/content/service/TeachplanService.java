package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

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
     * @param teachplanDto
     */
    void saveTeachplan(SaveTeachplanDto teachplanDto);

    void deleteTeachplan(Long id);
}