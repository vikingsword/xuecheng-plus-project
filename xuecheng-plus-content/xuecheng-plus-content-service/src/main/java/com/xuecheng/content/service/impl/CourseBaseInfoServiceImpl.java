package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.exception.XueChengPlusException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author Administrator
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件，根据课程名称查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //构建查询条件，根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //构建查询条件，根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;


    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {


        //合法性校验 在接口层使用 validation 框架校验


        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto, courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }
        //向课程营销表保存课程营销信息
        //课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        Long courseId = courseBaseNew.getId();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseId);
        int i = saveCourseMarket(courseMarketNew);
        if (i <= 0) {
            throw new RuntimeException("保存课程营销信息失败");
        }
        //查询课程基本信息及营销信息并返回
        return getCourseBaseInfo(courseId);

    }

    private int saveCourseMarket(CourseMarket courseMarketNew) {
        //收费规则
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isBlank(charge)) {
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0) {
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if (courseMarketObj == null) {
            // 插入
            return courseMarketMapper.insert(courseMarketNew);
        } else {
            // 更新
            BeanUtils.copyProperties(courseMarketNew, courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }


    // 根据课程id查询课程基本信息，包括基本信息和营销信息
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {

        //课程id
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }

        //校验本机构只能修改本机构的课程,业务逻辑校验需要在 service 中写
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //封装基本信息的数据
        BeanUtils.copyProperties(dto, courseBase);
        // todo mybatis 自动填充
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程基本信息
        int i = courseBaseMapper.updateById(courseBase);

        //封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        CourseBaseInfoDto courseBaseInfo = this.getCourseBaseInfo(courseId);
        return courseBaseInfo;

    }

    @Transactional
    @Override
    public void deleteCourse(Long companyId, Long courseId) {
        // 课程的审核状态为未提交(202002)时方可删除。 todo 魔法值
        // 删除课程需要删除课程相关的基本信息、营销信息、课程计划、课程教师信息。
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseBase::getId, courseId);
        CourseBase course = courseBaseMapper.selectOne(wrapper);
        if ("202002".equals(course.getAuditStatus())) {
            courseBaseMapper.deleteById(courseId);
            courseMarketMapper.deleteById(courseId);

            LambdaQueryWrapper<Teachplan> teachplanWrapper = new LambdaQueryWrapper<>();
            teachplanWrapper.eq(Teachplan::getCourseId, courseId);
            teachplanMapper.delete(teachplanWrapper);

            LambdaQueryWrapper<CourseTeacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(CourseTeacher::getCourseId, courseId);
            courseTeacherMapper.delete(teacherWrapper);
        }

    }


}