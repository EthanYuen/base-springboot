package org.ethanyuen.springboot.utilbean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;


@ApiModel("树combo")
@Data
@FieldNameConstants
public class TreeCombo {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("标签")
    private String label;
    @ApiModelProperty("是否选中")
    private boolean checked;
    @ApiModelProperty("子树")
    private List<TreeCombo> children;

    public TreeCombo() {

    }
    public TreeCombo(Long id, String title, List<TreeCombo> children) {
        this.id = id;
        this.label = title;
        this.children = children;
    }
}
