package org.ethanyuen.springboot.bean;

import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.aop.RequestAop;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Date;


@Data
@FieldNameConstants
@ApiModel("基础实体")
public abstract class BaseEntity implements Serializable {

    @Id
    @ApiModelProperty("id,修改时传入")
    private Long id;

    @Column
    @NotEditable
    @ApiModelProperty("创建时间")
    @Prev(els=@EL("$me.now()"))
    private Date createTime;

    @Column
    @NotEditable
    @ApiModelProperty("更新时间")
    @Prev(els=@EL("$me.now()"))
    private Date updateTime;

    @Column
    @NotEditable
    @Search
    @ApiModelProperty("创建人id")
    @Prev(els = @EL("$me.logUser('id')"))
    private Long authorId;

    @Column
    @NotEditable
    @ApiModelProperty("创建人姓名")
    @Prev(els = @EL("$me.logUser('name')"))
    private String author;

    @Column
    @NotEditable
    @ApiModelProperty("是否删除 0:未删除 1:已删除")
    @Default("0")
    private Boolean isDeleted;
    public Date now() {
        return new Date();
    }
    public Object logUser(String field) {
        User user=RequestAop.getLogUser();
        Object object=null;
        if ("id".equals(field)) {
            object= user==null?0:user.getId();
        }else if ("name".equals(field)) {
            object= user==null?"系统":user.getName();
        }
        return object;
    }
}
