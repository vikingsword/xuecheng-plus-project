package com.xuecheng.content.model.dto;

import com.xuecheng.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author vikingar
 * @time 2023/4/11 9:46
 */
@Data
@ApiModel(value = "EditCourseDto", description = "编辑 课程基本信息")
public class EditCourseDto extends AddCourseDto {

    @NotEmpty(message = "修改课程id不能为空", groups = ValidationGroups.Update.class)
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;

}
