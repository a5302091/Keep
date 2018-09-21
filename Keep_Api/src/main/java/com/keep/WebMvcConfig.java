package com.keep;

import com.keep.Interceptor.KeepInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:C:/Keep/");
    }

    @Bean
    public KeepInterceptor keepInterceptor() {
        return new KeepInterceptor();
    }

    /**
     * 注册拦截器
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(keepInterceptor()).addPathPatterns("/user/**").excludePathPatterns("/user/login").
                excludePathPatterns("/user/regist").
                excludePathPatterns("/user/queryPublisher")
                .addPathPatterns("/bgm/**")
                .addPathPatterns("/video/upload","/video/uploadCover","/video/userLike","/video/userUnlike","/video/saveComment");
        super.addInterceptors(registry);
    }
}
