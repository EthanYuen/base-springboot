package org.ethanyuen.springboot.enums;

import lombok.Getter;

/**
 * 数据库操作符
 */
@Getter
public enum DaoOperator {
    EQUAL("="),
    GREATERTHAN(">"),
    GREATEREQUAL(">="),
    LESSTHAN("<"),
    LESSEQUAL("<="),
    NOTEQUAL("!="),
    LIKE("like"),
    NOTLIKE("not like"),
    ASC("asc"),
    DESC("desc"),
    IN("in"),
    NOTIN("not in")
    ;
    private String name;

    DaoOperator(String name) {
        this.name = name;
    }
}
