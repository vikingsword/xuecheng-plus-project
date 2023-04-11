package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author vikingar
 * @time 2023/4/11 21:53
 */
@Api(value = "课程教师接口", tags = "课程教师接口")
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacherDto> getTeacherInfo(@PathVariable Long courseId) {
        return courseTeacherService.getTeacherInfo(courseId);
    }

    @PostMapping("/courseTeacher")
    public CourseTeacherDto addTeacher(@RequestBody AddCourseTeacherDto addCourseTeacherDto) {
        return courseTeacherService.addTeacher(addCourseTeacherDto);
    }

    @PutMapping("/courseTeacher")
    public CourseTeacherDto updateTeacher(@RequestBody CourseTeacherDto courseTeacherDto) {
        return courseTeacherService.updateTeacher(courseTeacherDto);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseTeacherService.deleteTeacher(courseId, teacherId);
    }

}
