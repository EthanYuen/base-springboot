package org.ethanyuen.springboot.mvc;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @SneakyThrows
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
         RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        if (annotation!=null&& annotation.value().length==0&&!method.getName().equals("swaggerResources")) {
            info = info.combine(RequestMappingInfo.paths(method.getName()).methods(annotation.method().length==0?RequestMethod.GET:annotation.method()[0]).build()) ;
        }
        return info;
    }

}
