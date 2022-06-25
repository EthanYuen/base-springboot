package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Boolean;
import java.lang.Long;
import lombok.Data;

@Data
@ApiModel("用户权限")
public class MidUserPermissionQueryParam extends Param {
  @ApiModelProperty(
      value = "靠左的类id，传参说明  查询:leftId=1",
      position = 1
  )
  Long leftId;

  @ApiModelProperty(
      value = "靠右的类id，传参说明  参考leftId",
      position = 2
  )
  Long rightId;

  @ApiModelProperty(
      value = "是否未选择",
      position = 5
  )
  Boolean notSelected;

  @ApiModelProperty(
      value = "创建人id",
      position = 9
  )
  Long authorId;
}
