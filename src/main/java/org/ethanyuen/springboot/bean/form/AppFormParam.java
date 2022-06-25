package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@ApiModel("app信息")
public class AppFormParam extends Param {
  @ApiModelProperty(
      value = "app应用标识",
      position = 2
  )
  @Validations(
      errorMsg = "应用标识不能为空",
      required = true
  )
  String appId;

  @ApiModelProperty(
      example = "1.1.1",
      value = "应用版本名称",
      position = 3
  )
  @Validations(
      errorMsg = "版本号不能为空",
      required = true
  )
  String version;

  @ApiModelProperty(
      example = "111",
      value = "应用版本号",
      position = 4
  )
  @Validations(
      el = "value>0",
      errorMsg = "版本号不能为空"
  )
  Long versionCode;

  @ApiModelProperty(
      value = "版本信息",
      position = 5
  )
  String versionInfo;

  @ApiModelProperty(
      value = "应用描述",
      position = 6
  )
  String description;

  @ApiModelProperty(
      value = "名称",
      position = 7
  )
  @Validations(
      errorMsg = "名称不能为空",
      required = true
  )
  String name;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 8
  )
  Long id;
}
