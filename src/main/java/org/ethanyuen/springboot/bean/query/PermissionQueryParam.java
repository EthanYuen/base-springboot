package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("权限信息")
public class PermissionQueryParam extends Param {
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
  String name;

  @ApiModelProperty(
      value = "创建人id",
      position = 8
  )
  Long authorId;
}
