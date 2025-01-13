package com.cuzz.webac.config;

import com.cuzz.webac.filter.JwtAuthenticationFilter;
import com.cuzz.webac.servers.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
public class FilterConfig {

    @Autowired
    JwtService jwtService;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new JwtAuthenticationFilter(jwtService)); // 注册过滤器
        registrationBean.addUrlPatterns("/*");  // 设置过滤路径，可以只过滤特定路径
        registrationBean.setOrder(1); // 设置过滤器的顺序，数字越小优先级越高
        System.out.println("LoveME");
        return registrationBean;
    }
}
