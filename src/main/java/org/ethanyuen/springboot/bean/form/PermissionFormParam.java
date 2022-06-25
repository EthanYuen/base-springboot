package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@ApiModel("权限信息")
public class PermissionFormParam extends Param {
  @ApiModelProperty(
      value = "中文权限别名",
      position = 1
  )
  String alias;

  @ApiModelProperty(
      value = "父权限id",
      position = 2
  )
  Long fatherId;

  @ApiModelProperty(
      value = "名称",
      position = 4
  )
  @Validations(
      errorMsg = "名称不能为空",
      required = true
  )
  String name;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 5
  )
  Long id;
}
