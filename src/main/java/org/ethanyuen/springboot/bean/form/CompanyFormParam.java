package org.ethanyuen.springboot.bean.form;

import org.ethanyuen.springboot.bean.Param;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.Long;
import java.lang.String;
import lombok.Data;
import org.nutz.plugins.validation.annotation.Validations;

@Data
@ApiModel("公司")
public class CompanyFormParam extends Param {
  @ApiModelProperty(
      value = "省份",
      position = 1
  )
  @Validations(
      errorMsg = "省份不能为空",
      required = true
  )
  String province;

  @ApiModelProperty(
      value = "市",
      position = 2
  )
  @Validations(
      errorMsg = "市不能为空",
      required = true
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
      value = "纬度",
      position = 5
  )
  String latitude;

  @ApiModelProperty(
      value = "经度",
      position = 6
  )
  String longitude;

  @ApiModelProperty(
      value = "联系人",
      position = 7
  )
  @Validations(
      errorMsg = "联系人不能为空",
      required = true
  )
  String contact;

  @ApiModelProperty(
      value = "联系电话",
      position = 8
  )
  @Validations(
      errorMsg = "请输入正确的电话",
      mobile = true
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
  @Validations(
      errorMsg = "名称不能为空",
      required = true
  )
  String name;

  @ApiModelProperty(
      value = "id,修改时传入",
      position = 12
  )
  Long id;
}
