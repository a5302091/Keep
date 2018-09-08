package com.keep;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Administrator on 2018/9/3.
 */
@Configuration
@EnableSwagger2
public class swagger2 {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.keep.controller"))
                .paths(PathSelectors.any()).build();
    }
    /**
     * @Description: 构建 api文档的信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("使用swagger2构建keep接口文档")
                // 设置联系人
                .contact(new Contact("keep", "http://www.shiguangkey.com", "419454569@qq.com"))
                // 描述
                .description("欢迎访问keep接口文档，这里是描述信息")
                // 定义版本号
                .version("1.0").build();
    }
}
