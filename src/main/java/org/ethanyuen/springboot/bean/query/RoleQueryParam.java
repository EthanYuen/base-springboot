package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("角色信息")
public class RoleQueryParam extends Param {
  @ApiModelProperty(
      value = "名称",
      position = 1
  )
  String name;

  @ApiModelProperty(
      value = "创建人id",
      position = 5
  )
  Long authorId;
}
