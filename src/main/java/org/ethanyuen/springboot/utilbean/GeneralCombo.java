package org.ethanyuen.springboot.utilbean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;


@ApiModel("普通combo")
@Data
@FieldNameConstants
public class GeneralCombo {

    @ApiModelProperty("属性名")
    private String text;

    @ApiModelProperty("属性值")
    private String value;

    @ApiModelProperty("属性值2")
    private String value1;
    
    @ApiModelProperty("是否选中")
    private boolean checked;

    public GeneralCombo() {

    }
    /**
     * 带参构造函数
     * @param text
     * @param value
     */
    public GeneralCombo( String value,String text){
        this.text=text;
        this.value=value;
    }
}
