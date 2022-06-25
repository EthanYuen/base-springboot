package org.ethanyuen.springboot.utilbean;

import org.ethanyuen.springboot.bean.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.ethanyuen.springboot.enums.DaoOperator;


@Data
@FieldNameConstants
@ApiModel("排序combo")
public class OrderCombo {

    @ApiModelProperty(value = "字段名",example = BaseEntity.Fields.updateTime)
    private String field;

    @ApiModelProperty(value = "操作符",example ="DESC")
    private DaoOperator operator;
    public OrderCombo(){

    }

    public OrderCombo(String field, DaoOperator operator) {
        this.field = field;
        this.operator = operator;
    }
}
