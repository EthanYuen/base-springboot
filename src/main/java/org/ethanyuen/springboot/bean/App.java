package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.NotEditable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.plugins.validation.annotation.Validations;


@Data
@FieldNameConstants
@Table
@ApiModel("app信息")
public class App  extends BaseNameEntity {
    @Column
    @NotEditable
    @ApiModelProperty("文件路径")
    private String appFile;

    @Column
    @Validations(required = true,errorMsg = "应用标识不能为空")
    @ApiModelProperty("app应用标识")
    String appId;

    @Column
    @Validations(required = true,errorMsg = "版本号不能为空")
    @ApiModelProperty(value = "应用版本名称",example = "1.1.1")
    String version;

    @Column
    @Validations(el = "value>0",errorMsg = "版本号不能为空")
    @ApiModelProperty(value = "应用版本号",example = "111")
    Long versionCode;

    @Column
    @ApiModelProperty("版本信息")
    String versionInfo;

    @Column
    @ApiModelProperty("应用描述")
    String description;

}
