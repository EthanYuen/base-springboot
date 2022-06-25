package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;


@Data
@FieldNameConstants
@ApiModel("基础中间类")
public abstract class BaseMidEntity extends BaseEntity {

    @ApiModelProperty("靠左的类id，传参说明  查询:leftId=1")
    @Column
    @Search
    @NotEditable
    Long leftId;

    @ApiModelProperty("靠右的类id，传参说明  参考leftId")
    @Column
    @Search
    @NotEditable
    Long rightId;

    @ApiModelProperty("靠左的类id，逗号分隔,传参说明  新增删除:leftIds=1,2&rightIds=1,2")
    String leftIds;

    @ApiModelProperty("靠右的类id，逗号分隔,传参说明  参考leftIds")
    String rightIds;

    @NotEditable
    @Search
    @ApiModelProperty("是否未选择")
    Boolean notSelected;
}
