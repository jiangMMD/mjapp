package com.mmd.mjapp.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Administrator on 2017/9/29.
 */
@Configuration
public class AuthInterceptor extends WebMvcConfigurerAdapter {

    @Bean
    LoginHandlerInterceptor loginHandlerInterceptor() {
        return new LoginHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginHandlerInterceptor())
                .addPathPatterns("/user/*") //拦截客户
                .addPathPatterns("/product/*") //拦截订单
                .addPathPatterns("/home/*") //拦截订单

                .excludePathPatterns("/pay/*")
                .excludePathPatterns("/base/*")
                .excludePathPatterns("/user/login") //登录
                .excludePathPatterns("/user/regist") //注册
                .excludePathPatterns("/user/getYzmToken") //获取验证码token
                .excludePathPatterns("/user/sendRegistCode") //发送注册验证码
                .excludePathPatterns("/user/sendLoginCode") //发送登录验证码
                .excludePathPatterns("/user/loginout") //退出登录
                .excludePathPatterns("/user/getToken") //获取token
                .excludePathPatterns("/user/refrestToken");
    }
}
