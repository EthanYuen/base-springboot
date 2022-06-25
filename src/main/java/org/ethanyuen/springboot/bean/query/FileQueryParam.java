package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import org.ethanyuen.springboot.enums.Filetype;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import lombok.Data;

@Data
@ApiModel("文件类")
public class FileQueryParam extends Param {
  @ApiModelProperty(
      value = "相关记录id",
      position = 1
  )
  Long referenceId;

  @ApiModelProperty(
      value = "文件类型",
      position = 3
  )
  Filetype filetype;

  @ApiModelProperty(
      value = "创建人id",
      position = 7
  )
  Long authorId;
}
