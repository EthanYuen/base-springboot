package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import org.ethanyuen.springboot.enums.BusinessType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("日志类")
public class SystemLogQueryParam extends Param {
  @ApiModelProperty(
      value = "操作描述",
      position = 2
  )
  String operation;

  @ApiModelProperty(
      value = "业务类型",
      position = 4
  )
  BusinessType type;

  @ApiModelProperty(
      value = "创建人id",
      position = 10
  )
  Long authorId;
}
