package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import org.ethanyuen.springboot.enums.BusinessType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("日志类")
public class SystemLogFormParam extends Param {
  @ApiModelProperty(
      value = "类方法名行号",
      position = 1
  )
  String source;

  @ApiModelProperty(
      value = "操作描述",
      position = 2
  )
  String operation;

  @ApiModelProperty(
      value = "其他信息",
      position = 3
  )
  String info;

  @ApiModelProperty(
      value = "业务类型",
      position = 4
  )
  BusinessType type;

  @ApiModelProperty(
      value = "所属公司id",
      position = 5
  )
  Long companyId;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 7
  )
  Long id;
}
