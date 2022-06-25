package org.ethanyuen.springboot.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.ethanyuen.springboot.annotation.Search;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;

@Data
@FieldNameConstants
public class BaseTreeEntity extends BaseNameEntity {

    @Search
    @Column
    @ApiModelProperty("上级Id")
    private Long fatherId;

    @Column
    @Default("0")
    @ApiModelProperty("权重")
    private Integer weight;
}
