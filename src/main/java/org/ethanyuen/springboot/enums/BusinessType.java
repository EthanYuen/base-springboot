package org.ethanyuen.springboot.enums;

import lombok.Getter;

/**
 * 业务日志类别
 */
@Getter
public enum  BusinessType {
    ADD("新增"),
    EDIT("编辑"),
    SET("设置"),
    DELETE("删除"),
    LOGIN("登陆"),
    GET("获取"),
    OTHER("其它"),
    ERROR("异常");
    BusinessType(String title) {
        this.title=title;
    }
    String title;
}
