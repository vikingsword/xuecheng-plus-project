package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author vikingar
 * @time 2023/4/9 8:55
 */
public interface CourseCategoryService {

    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
