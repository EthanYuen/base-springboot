package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@ApiModel("角色信息")
public class RoleFormParam extends Param {
  @ApiModelProperty(
      value = "名称",
      position = 1
  )
  @Validations(
      errorMsg = "名称不能为空",
      required = true
  )
  String name;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 2
  )
  Long id;
}
