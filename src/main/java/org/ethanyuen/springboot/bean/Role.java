package org.ethanyuen.springboot.bean;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Table;


@ApiModel("角色信息")
@Table
@Data
@FieldNameConstants
public class Role extends BaseNameEntity {

}