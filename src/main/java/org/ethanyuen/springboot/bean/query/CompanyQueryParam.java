package org.ethanyuen.springboot.bean.query;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;

@Data
@ApiModel("公司")
public class CompanyQueryParam extends Param {
  @ApiModelProperty(
      value = "省份",
      position = 1
  )
  String province;

  @ApiModelProperty(
      value = "市",
      position = 2
  )
  String city;

  @ApiModelProperty(
      value = "县区",
      position = 3
  )
  String county;

  @ApiModelProperty(
      value = "地址",
      position = 4
  )
  String address;

  @ApiModelProperty(
      value = "联系人",
      position = 7
  )
  String contact;

  @ApiModelProperty(
      value = "联系电话",
      position = 8
  )
  String phone;

  @ApiModelProperty(
      value = "管理员id",
      position = 9
  )
  Long managerId;

  @ApiModelProperty(
      value = "名称",
      position = 11
  )
  String name;

  @ApiModelProperty(
      value = "创建人id",
      position = 15
  )
  Long authorId;
}
