package org.ethanyuen.springboot.utilbean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;


@Data
@FieldNameConstants
@ApiModel("基础查询字段类")
public class BaseCondition {

    @ApiModelProperty(value = "开始时间",position = 1000)
    private Date startTime;

    @ApiModelProperty(value = "结束时间",position = 1001)
    private Date endTime;

    @ApiModelProperty(value = "页码",position = 1002)
    private int pageNo;

    @ApiModelProperty(value = "页大小",position = 1003)
    private int pageSize;

    @ApiModelProperty(value = "排序",position = 1004)
    private List<OrderCombo> orders;

    @ApiModelProperty(value = "需要获取的相关one，many，多个以|分隔",position = 1005)
    private String links;

    @ApiModelProperty(value = "需要获取的中间表，多个以|分隔",position = 1005)
    private String midLinks;

    @ApiModelProperty(value = "关键字",position = 1006)
    private String key;

    @ApiModelProperty(value = "是否为获取combo列表",position = 1007)
    boolean combo;

    @ApiModelProperty(value = "是否获取树形列表",position = 1008)
    boolean isTree;
}
