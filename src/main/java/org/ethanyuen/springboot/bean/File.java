package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.enums.Filetype;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.plugins.validation.annotation.Validations;


@ApiModel("文件类")
@Table
@Data
@FieldNameConstants
@TableIndexes({@Index( fields = {File.Fields.filePath, File.Fields.filetype}, unique = false)})
public  class File extends BaseEntity {

    @ApiModelProperty("相关记录id")
    @Column
    @Search
    @Validations(el = "value>0",errorMsg = "请选择相关记录id")
    Long referenceId;

    @NotEditable
    @ApiModelProperty("文件路径")
    @Column
    String filePath;

    @ApiModelProperty("文件类型")
    @Column
    @Search
    @Validations(required = true,errorMsg = "请选择文件类型")
    Filetype filetype;
}
