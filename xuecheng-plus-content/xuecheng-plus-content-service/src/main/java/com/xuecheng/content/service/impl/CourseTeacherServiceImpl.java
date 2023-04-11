package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.utils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;


    @Override
    public List<CourseTeacherDto> getTeacherInfo(Long courseId) {

        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(wrapper);
        List<CourseTeacherDto> courseTeacherDtos = BeanCopyUtils.copyBeanList(courseTeachers, CourseTeacherDto.class);
        return courseTeacherDtos;
    }

    @Override
    public CourseTeacherDto addTeacher(AddCourseTeacherDto addCourseTeacherDto) {
        CourseTeacher teacher1 = BeanCopyUtils.copyBean(addCourseTeacherDto, CourseTeacher.class);
        teacher1.setCreateDate(LocalDateTime.now());
        courseTeacherMapper.insert(teacher1);

        CourseTeacherDto dto = new CourseTeacherDto();
        CourseTeacher teacher = courseTeacherMapper.selectById(teacher1.getId());
        BeanUtils.copyProperties(teacher, dto);
        return dto;
    }

    @Override
    public CourseTeacherDto updateTeacher(CourseTeacherDto courseTeacherDto) {
        CourseTeacher teacher = BeanCopyUtils.copyBean(courseTeacherDto, CourseTeacher.class);
        courseTeacherMapper.updateById(teacher);
        CourseTeacher courseTeacher = courseTeacherMapper.selectById(teacher.getId());
        return BeanCopyUtils.copyBean(courseTeacher, CourseTeacherDto.class);
    }

    @Override
    public void deleteTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId,teacherId);
        courseTeacherMapper.delete(wrapper);
    }
}