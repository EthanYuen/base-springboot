package org.ethanyuen.springboot.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.ethanyuen.springboot.utilbean.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfiguration {
    private final OpenApiExtensionResolver openApiExtensionResolver;
    @Value("${knife4j.documents[0].group}")
    String groupName;
    @Autowired
    public SwaggerConfiguration(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
//        String groupName = "平台接口文档";
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        //.title("swagger-bootstrap-ui-demo RESTful APIs")
                        .version("1.0")
                        .contact("EthanYuen")
                        .title(groupName)
                        .build())
                //分组名称
                .groupName(groupName)
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage(Params.BASE_PACKAGE+".module"))
                .paths(PathSelectors.any())
                .build()
        //赋予插件体系
                .extensions(openApiExtensionResolver.buildExtensions(groupName));
        return docket;
    }

}
