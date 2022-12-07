package com.trendyol.fleetmanagement;

import com.trendyol.fleetmanagement.config.CommonConfig;
import com.trendyol.fleetmanagement.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {CommonConfig.class, SwaggerConfig.class})
public class FleetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetManagementApplication.class, args);
    }

}
