package com.webstudy.devicemanage;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication
public class DeviceManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceManageApplication.class, args);
    }

}
