package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;


@ApiModel("用户")
@Table
@Data
@FieldNameConstants
public class User extends BaseNameEntity
{

    @ApiModelProperty("密码")
    @Column
    @NotEditable
    private String password;

    @ApiModelProperty("地址")
    @Column
    String address;

    @ApiModelProperty("联系电话")
    @Column
    @Search
    String phone;

    @ApiModelProperty("盐")
    @Column
    @NotEditable
    private String salt;

    @ApiModelProperty("是否禁用")
    @Column
    @Default("0")
    private Boolean locked;

    @ApiModelProperty("微信id")
    @Column
    @NotEditable
    private String wxOpenId;

    @ApiModelProperty("所属公司id")
    @Column
    @Search
    private Long companyId;

    @ApiModelProperty("所属公司")
    @One(field = Fields.companyId)
    private Company company;

}

