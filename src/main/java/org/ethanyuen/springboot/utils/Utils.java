package org.ethanyuen.springboot.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.poi.excel.ExcelBase;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
@Component
public class Utils {
    @Autowired
    public static ApplicationContext applicationContext;
    static Map<Class, Object> iocMap = new HashMap<>();
    public static  <T> T getIocBean(Class<T> clazz) {
        Object object= iocMap.get(clazz);
        if (object==null) {
            object = SpringContextUtil.getBean(clazz);
            iocMap.put(clazz, object);
        }
        return (T) object;
    }
    public static  <T> void addExcelHeaderAlias(ExcelBase excelBase, Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(ApiModelProperty.class)) {
                String comment = declaredField.getAnnotation(ApiModelProperty.class).value();
                String fieldName = declaredField.getName();
                if (excelBase instanceof ExcelReader) {
                    ((ExcelReader)excelBase).addHeaderAlias(comment, fieldName);
                }else  if (excelBase instanceof ExcelWriter) {
                    ((ExcelWriter)excelBase).addHeaderAlias(fieldName, comment);
                }
            }
        }
    }
    public static <T> T transferPojo(Class<T> clazz, Object source) throws IllegalAccessException, InstantiationException {
        T target=clazz.newInstance();
        BeanUtil.copyProperties(source,target);
        return target;
    }
}

