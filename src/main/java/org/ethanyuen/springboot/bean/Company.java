package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.plugins.validation.annotation.Validations;

@Table
@Data
@FieldNameConstants
@ApiModel("公司")
public class Company  extends BaseNameEntity {

    @Column
    @Search
    @ApiModelProperty("省份")
    @Validations(required = true,errorMsg = "省份不能为空")
    private String province;

    @Column
    @Search
    @ApiModelProperty("市")
    @Validations(required = true,errorMsg = "市不能为空")
    private  String city;

    @Column
    @Search
    @ApiModelProperty("县区")
    private String county;

    @Column
    @Search
    @ApiModelProperty("地址")
    private  String address;

    @Column
    @ApiModelProperty("纬度")
    private String latitude;

    @Column
    @ApiModelProperty("经度")
    private String longitude;

    @Search
    @ApiModelProperty("联系人")
    @Column
    @Validations(required = true,errorMsg = "联系人不能为空")
    String contact;

    @ApiModelProperty("联系电话")
    @Column
    @Search
    @Validations(mobile = true,errorMsg = "请输入正确的电话")
    String phone;

    @ApiModelProperty("管理员id")
    @Column
    @Search
    private Long managerId;

    @ApiModelProperty("管理员")
    @One(field = Fields.managerId)
    private User manager;

}
