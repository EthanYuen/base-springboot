package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("用户权限")
public class MidUserPermissionFormParam extends Param {
  @ApiModelProperty(
      value = "靠左的类id，逗号分隔,传参说明  新增删除:leftIds=1,2&rightIds=1,2",
      position = 3
  )
  String leftIds;

  @ApiModelProperty(
      value = "靠右的类id，逗号分隔,传参说明  参考leftIds",
      position = 4
  )
  String rightIds;
}
