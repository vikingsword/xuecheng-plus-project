package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.exception.XueChengPlusException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if (id != null) {
            // id 不为空更新
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            // id为空插入，取出同父同级别的课程计划数量
            Integer count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);

            teachplanMapper.insert(teachplanNew);

        }

    }

    @Transactional
    @Override
    public void deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        // parentId != 0 or (parentId=0 and 以这个id作为parentId进行查询，找不到内容(也就是没有子节点))
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, id);
        List<Teachplan> teachplanList = teachplanMapper.selectList(wrapper);
        if (teachplan.getParentid() != 0 || (teachplanList.size() == 0)) {
            // 可以删除，物理删除
            teachplanMapper.deleteById(id);
            // 同时物理删除 media 中的信息
            LambdaQueryWrapper<TeachplanMedia> mediaWrapper = new LambdaQueryWrapper<>();
            mediaWrapper.eq(TeachplanMedia::getTeachplanId, id);
            teachplanMediaMapper.delete(mediaWrapper);
        } else {
            // 不能删除，返回自定义异常 todo 返回errCode
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        }
    }

    @Override
    public void moveTeachplan(String moveDirection, Long id) {
        // 获取当前课程计划的 order by 如果在最顶上或者最底下则不动
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long parentid = teachplan.getParentid();
        Integer orderby = teachplan.getOrderby();
        Integer teachplanCount = getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid());
        if (orderby == 1 && moveDirection.equals("movedown")) {
            // 向下移动
            moveDown(parentid,orderby);
        } else if (orderby.equals(teachplanCount) && moveDirection.equals("moveup")) {
            // 向上移动
            moveUp(parentid,orderby);
        } else if (moveDirection.equals("moveup")) {
            // 向上移动
            moveUp(parentid,orderby);
        } else if (moveDirection.equals("movedown")) {
            // 向下移动
            moveDown(parentid,orderby);
        } else {
            // do nothing
        }

    }

    private void moveUp(Long parentId, Integer orderBy) {
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getOrderby, orderBy - 1).eq(Teachplan::getParentid, parentId);
        Teachplan teachplan = teachplanMapper.selectOne(wrapper);
        LambdaQueryWrapper<Teachplan> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Teachplan::getOrderby, orderBy).eq(Teachplan::getParentid, parentId);
        Teachplan teachplan2 = teachplanMapper.selectOne(wrapper2);

        teachplan.setOrderby(orderBy);
        teachplanMapper.updateById(teachplan);

        teachplan2.setOrderby(orderBy - 1);
        teachplanMapper.updateById(teachplan2);
    }


    private void moveDown(Long parentId, Integer orderBy) {
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getOrderby,orderBy + 1).eq(Teachplan::getParentid,parentId);
        Teachplan teachplan = teachplanMapper.selectOne(wrapper);
        LambdaQueryWrapper<Teachplan> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(Teachplan::getOrderby,orderBy).eq(Teachplan::getParentid,parentId);
        Teachplan teachplan2 = teachplanMapper.selectOne(wrapper2);

        teachplan.setOrderby(orderBy);
        teachplanMapper.updateById(teachplan);

        teachplan2.setOrderby(orderBy + 1);
        teachplanMapper.updateById(teachplan2);

    }

//    private void move(Long parentId, Integer orderBy, Integer pace) {
//        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Teachplan::getOrderby, orderBy + pace).eq(Teachplan::getParentid, parentId);
//        Teachplan teachplan = teachplanMapper.selectOne(wrapper);
//        teachplan.setOrderby(orderBy);
//        teachplanMapper.updateById(teachplan);
//    }

    /**
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @description 获取最新的排序号
     */
    private Integer getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}