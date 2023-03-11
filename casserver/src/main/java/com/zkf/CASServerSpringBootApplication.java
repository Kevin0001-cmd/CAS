package com.zkf;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "com.zkf")
public class CASServerSpringBootApplication implements WebMvcConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(CASServerSpringBootApplication.class, args);
    }

}
