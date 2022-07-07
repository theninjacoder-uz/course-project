package com.itransition.courseproject;

import com.itransition.courseproject.config.component.OpenApiProperties;
import com.itransition.courseproject.config.security.AppProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition
@SpringBootApplication
@EnableConfigurationProperties({
        AppProperties.class,
        OpenApiProperties.class
})
public class CourseProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseProjectApplication.class, args);
    }

}
