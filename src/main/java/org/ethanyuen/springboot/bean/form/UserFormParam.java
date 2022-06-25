package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.String;
import lombok.Data;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@ApiModel("用户")
public class UserFormParam extends Param {
  @ApiModelProperty(
      value = "地址",
      position = 2
  )
  String address;

  @ApiModelProperty(
      value = "联系电话",
      position = 3
  )
  String phone;

  @ApiModelProperty(
      value = "是否禁用",
      position = 5
  )
  Boolean locked;

  @ApiModelProperty(
      value = "所属公司id",
      position = 7
  )
  Long companyId;

  @ApiModelProperty(
      value = "名称",
      position = 9
  )
  @Validations(
      errorMsg = "名称不能为空",
      required = true
  )
  String name;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 10
  )
  Long id;
}
