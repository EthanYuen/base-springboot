package org.ethanyuen.springboot.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.enums.FlowNodeStatus;
import org.nutz.dao.entity.annotation.Column;

/**
 * @author EthanYuen
 */
@Data
@FieldNameConstants
public class BaseFlowEntity<S extends FlowNodeStatus> extends BaseNameEntity{
    @ApiModelProperty("状态")
    @NotEditable
    @Column
    @Search
    S status;

}
