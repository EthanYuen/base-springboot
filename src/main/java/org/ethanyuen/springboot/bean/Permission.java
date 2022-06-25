package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;


@ApiModel("权限信息")
@Table
@Data
@FieldNameConstants
public class Permission extends BaseNameEntity {

    @ApiModelProperty("中文权限别名")
    @Column
    @Search
    protected String alias;

    @ApiModelProperty("父权限id")
    @Column
    @Search
    private Long fatherId;

    @ApiModelProperty("父权限")
    @One(field = Fields.fatherId)
    Permission father;
    public Permission(String name, String alias, Long fatherId){
        this.setAlias(alias);
        this.setName(name);
        this.setFatherId(fatherId);
    }
    public Permission(){

    }
}