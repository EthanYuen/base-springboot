package org.ethanyuen.springboot.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.enums.FlowNodeStatus;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.plugins.validation.annotation.Validations;

/**
 * @author EthanYuen
 */
@Data
@FieldNameConstants
public class BaseFlowNodeEntity<T extends FlowNodeStatus>extends BaseEntity {
    @Validations(required= true,errorMsg = "相关记录id不能为空")
    @Search
    @Column
    @ApiModelProperty("相关记录id")
    private Long referenceId;

    @Validations(required= true,errorMsg = "审核信息不能为空")
    @Column
    @ApiModelProperty("审核信息")
    private String info;

    @ApiModelProperty("流节点")
    @Column
    T node;

}
