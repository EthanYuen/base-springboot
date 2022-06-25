package org.ethanyuen.springboot.enums;

import lombok.Getter;

/**
 * 文件类型
 */
@Getter
public enum Filetype {
    OTHER("其他");
    Filetype(String title) {
        this.title=title;
    }
    String title;
}
