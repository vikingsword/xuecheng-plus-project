package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageParams {

    //当前页码
    @ApiModelProperty(value = "页码")
    private Long pageNo = 1L;

    //每页记录数默认值
    @ApiModelProperty(value = "页数")
    private Long pageSize = 10L;


}