package org.ethanyuen.springboot.mvc;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import org.ethanyuen.springboot.annotation.NotEditable;
import org.ethanyuen.springboot.annotation.Search;
import org.ethanyuen.springboot.bean.BaseMidEntity;
import org.ethanyuen.springboot.bean.Param;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.utilbean.Params;
import org.nutz.plugins.validation.annotation.Validations;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {"org.nutz.dao.entity.annotation.Table"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public final class ParamProcessor extends AbstractProcessor {
    private Filer filer;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler(); // for creating file
    }

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement element : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(element);
            for (Element clazz: elements) {
                int queryOrder=0;
                int formOrder=0;
                String className = clazz.getSimpleName().toString();
                Class<?> bean = Class.forName(Params.BASE_PACKAGE+".bean." + className);
                Field[] fields = ReflectUtil.getFields(bean);
                TypeSpec.Builder queryBuilder = TypeSpec.classBuilder(className + "QueryParam")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Data.class).addAnnotation(AnnotationSpec.get(bean.getAnnotation(ApiModel.class))).superclass(Param.class);
                TypeSpec.Builder formBuilder = TypeSpec.classBuilder(className + "FormParam")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Data.class).addAnnotation(AnnotationSpec.get(bean.getAnnotation(ApiModel.class))).superclass(Param.class);
                for (Field field : fields) {
                    AnnotationSpec annotationSpec = AnnotationSpec.get(field.getAnnotation(ApiModelProperty.class));
                    annotationSpec=annotationSpec.toBuilder().addMember("position",""+ ++queryOrder).build();
                    if (field.isAnnotationPresent(Search.class)) {
                         queryBuilder.addField(FieldSpec.builder(field.getType(), field.getName()).addAnnotation(annotationSpec).build());
                    }
                    if (bean.getSuperclass()== BaseMidEntity.class&&field.getName().equals("id")) {
                        continue;
                    }
                    if (!field.isAnnotationPresent(NotEditable.class)&& (ClassUtil.isBasicType(field.getType())||field.getType()== Date.class||field.getType()== String.class||field.getType().isEnum())) {
                        FieldSpec.Builder builder = FieldSpec.builder(field.getType(), field.getName()).addAnnotation(annotationSpec);
                        if (field.isAnnotationPresent(Validations.class)) {
                            builder.addAnnotation(AnnotationSpec.get(field.getAnnotation(Validations.class)));
                        }
                        formBuilder.addField(builder.build());
                    }
                }
                try {
                    JavaFile javaFile = JavaFile.builder(Params.BASE_PACKAGE+".bean.query", queryBuilder.build()).build();
                    javaFile.writeTo(new File("src/main/java"));
                    JavaFile javaFile2 = JavaFile.builder(Params.BASE_PACKAGE+".bean.form", formBuilder.build()).build();
                    javaFile2.writeTo(new File("src/main/java"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }

}
