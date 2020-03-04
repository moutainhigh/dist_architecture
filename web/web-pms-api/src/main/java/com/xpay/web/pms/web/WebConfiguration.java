package com.xpay.web.pms.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/9
 * Time: 17:20
 * Description:
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Autowired
    private AllowCrossFilter allowCrossFilter;

    @Autowired
    private CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/test/**","/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserMethodArgumentResolver);
    }

    @Bean
    public FilterRegistrationBean<AllowCrossFilter> allowCrossFilterRegistrationBean() {
        FilterRegistrationBean<AllowCrossFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(allowCrossFilter);
        return registrationBean;
    }


}
