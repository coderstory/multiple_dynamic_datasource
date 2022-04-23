package com.example.multipledynamicdatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MultipleDynamicDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleDynamicDatasourceApplication.class, args);
    }

}

