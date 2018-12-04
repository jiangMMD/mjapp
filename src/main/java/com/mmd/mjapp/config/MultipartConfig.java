package com.mmd.mjapp.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Value("${projectPath:}")
    private String projectPath;
//
//    @Bean
//    MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        String location = StringUtils.isNotBlank(projectPath) ? projectPath + "/home/tmp" : "/home/tmp";
//        factory.setLocation(location);
//        return factory.createMultipartConfig();
//    }

}
