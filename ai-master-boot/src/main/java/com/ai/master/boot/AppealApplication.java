package com.ai.master.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 申诉服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.ai.master")
@MapperScan(basePackages = "com.ai.master.appeal.infrastructure.persistence.mapper")
public class AppealApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AppealApplication.class, args);
    }
}