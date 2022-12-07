package com.trendyol.fleetmanagement.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan(basePackages = "com.trendyol.fleetmanagement")
@EnableJpaRepositories(value = "com.trendyol.fleetmanagement.repository")
@EntityScan(value = "com.trendyol.fleetmanagement.model")
public class CommonConfig extends SpringBootServletInitializer {

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

}
