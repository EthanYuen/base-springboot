package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@FieldNameConstants
@ApiModel("基础名字类")
public class BaseNameEntity extends BaseEntity {
    @Column
    @ApiModelProperty("名称")
    @Validations(required = true,errorMsg = "名称不能为空")
    @Search(key = true)
    private String name;
}
