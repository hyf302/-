package com.scenic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.io.File;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = {"com.scenic.mapper"})
@EnableScheduling
@ComponentScan(basePackages = {"com.scenic"})
public class ScenicSpotApplication {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ScenicSpotApplication.class, args);
    }
} 