package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;

import java.util.List;

public interface CourseTeacherService {


    List<CourseTeacherDto> getTeacherInfo(Long courseId);

    CourseTeacherDto addTeacher(AddCourseTeacherDto addCourseTeacherDto);

    CourseTeacherDto updateTeacher(CourseTeacherDto courseTeacherDto);

    void deleteTeacher(Long courseId, Long teacherId);
}