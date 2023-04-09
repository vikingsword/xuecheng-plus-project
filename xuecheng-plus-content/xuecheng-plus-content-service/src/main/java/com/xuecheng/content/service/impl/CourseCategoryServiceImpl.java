package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vikingar
 * @time 2023/4/9 8:56
 */
@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        // 找到每个节点的子节点，最终封装成List<CourseCategoryTreeDto>
        // 将list转map,key:id,value:CourseCategoryTreeDto，目的是为了方便从map获取节点,并排除根节点
        Map<String, CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));

        // 定义一个list作为最终返回的list
        List<CourseCategoryTreeDto> resultList = new ArrayList<>();

        // 从头遍历 List<CourseCategoryTreeDto>, 一边遍历一边找子节点放在父节点的childrenTreeNodes中
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach(item -> {

            if (item.getParentid().equals(id)) {
                resultList.add(item);
            }
            // 找到每个节点的子节点，放到父节点的childrenNodes中, 由于操作的是同一份数据, 返回的结果就是正确的
            CourseCategoryTreeDto courseCategoryTreeDtoParent = map.get(item.getParentid());
            if (courseCategoryTreeDtoParent != null) {
                if (courseCategoryTreeDtoParent.getChildrenTreeNodes() == null) {
                    courseCategoryTreeDtoParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                courseCategoryTreeDtoParent.getChildrenTreeNodes().add(item);
            }

        });

        return resultList;
    }
}
