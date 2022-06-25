package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("用户")
public class UserQueryParam extends Param {
  @ApiModelProperty(
      value = "联系电话",
      position = 3
  )
  String phone;

  @ApiModelProperty(
      value = "所属公司id",
      position = 7
  )
  Long companyId;

  @ApiModelProperty(
      value = "名称",
      position = 9
  )
  String name;

  @ApiModelProperty(
      value = "创建人id",
      position = 13
  )
  Long authorId;
}
