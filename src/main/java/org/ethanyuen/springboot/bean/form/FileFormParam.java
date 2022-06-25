package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import org.ethanyuen.springboot.enums.Filetype;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import lombok.Data;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@ApiModel("文件类")
public class FileFormParam extends Param {
  @ApiModelProperty(
      value = "相关记录id",
      position = 1
  )
  @Validations(
      el = "value>0",
      errorMsg = "请选择相关记录id"
  )
  Long referenceId;

  @ApiModelProperty(
      value = "文件类型",
      position = 3
  )
  @Validations(
      errorMsg = "请选择文件类型",
      required = true
  )
  Filetype filetype;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 4
  )
  Long id;
}
