package org.ethanyuen.springboot.utilbean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@ApiModel("历史数据combo")
public class HistoryCombo {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("值")
    private double value;
    @ApiModelProperty("时间戳")
    private Long time;
}
