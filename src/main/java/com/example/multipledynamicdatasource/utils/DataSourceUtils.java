package com.example.multipledynamicdatasource.utils;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 动态数据源的工具类  所有的公开方法 都包含在此类
 */
@Slf4j
@Component
public class DataSourceUtils {
    private final DynamicRoutingDataSource routingDataSource;
    private final DatabaseType defaultType = DatabaseType.MYSQL;

    public DataSourceUtils(DynamicRoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    public void addDataSource(String key, DataSource dataSource) {
        if (!routingDataSource.containsDataBaseKey(key)) {
            routingDataSource.addDataSource(key, dataSource);
        } else {
            log.warn("database is already existed, key is" + key);
        }
    }

    /**
     * 给定一个数据库的连接配置 创建一个DS并加入
     */
    public void addDataSource(DataSourceProperties properties) {
        properties.getType().buildUrl(properties);
        addDataSource(properties.buildKey(), buildDataSource(properties));
    }


    public DataSource buildDataSource(DataSourceProperties prop) {
        DatabaseType type = prop.getType();
        if (type == null) {
            type = defaultType;
        }

        return DataSourceBuilder.create()
                .username(prop.getUsername())
                .password(prop.getPassword())
                .driverClassName(type.getDriverName())
                .url(prop.buildUrl())
                .type(HikariDataSource.class)
                .build();
    }


    public void setCurrentKey(String key) {
        routingDataSource.setCurrentDatabaseKey(key);
    }
}
