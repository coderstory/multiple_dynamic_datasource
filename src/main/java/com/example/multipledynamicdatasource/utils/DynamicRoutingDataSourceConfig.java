package com.example.multipledynamicdatasource.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 创建动态数据源的bean 在动态数据源类中注入默认的主datasource
 */
@Configuration
public class DynamicRoutingDataSourceConfig {
    private final DatabaseType defaultType = DatabaseType.MYSQL;

    /**
     * 创建动态理路由数据源
     * @param properties  主数据源的配置
     */
    @Bean
    public DynamicRoutingDataSource build(DataSourceProperties properties) {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        dynamicRoutingDataSource.setPrimary(buildPrimaryDataSource(properties));
        return dynamicRoutingDataSource;
    }
    private DataSource buildPrimaryDataSource(DataSourceProperties dataSourceProperties) {
        return DataSourceBuilder.create()
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .url(dataSourceProperties.getUrl())
                .type(HikariDataSource.class)
                .driverClassName(defaultType.getDriverName())
                .build();
    }
}
