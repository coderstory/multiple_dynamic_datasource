package com.example.multipledynamicdatasource.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现多数据源的核心类 实现了DataSource的功能和动态切换的能力
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractDataSource implements InitializingBean {

    private DataSource primary;
    private final ThreadLocal<String> currentDatabaseKey = new ThreadLocal<>();
    // ds缓存
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    public DynamicRoutingDataSource() {
    }

    public void setPrimary(DataSource ds) {
        primary = ds;
    }

    protected void setCurrentDatabaseKey(String key) {
        Assert.isTrue(dataSourceMap.containsKey(key), "target datasource not exists");
        currentDatabaseKey.set(key);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) {
        throw new UnsupportedOperationException();
    }

    private DataSource determineTargetDataSource() {
        if (currentDatabaseKey.get() == null || !dataSourceMap.containsKey(currentDatabaseKey.get())) {
            Assert.notNull(primary, "primary datasource must be not null");
            return primary;
        } else {
            return dataSourceMap.get(currentDatabaseKey.get());
        }
    }

    protected boolean containsDataBaseKey(String key) {
        return dataSourceMap.containsKey(key);
    }

    protected void addDataSource(String key, DataSource dataSource) {
        dataSourceMap.put(key, dataSource);
    }


    @Override
    public void afterPropertiesSet() {
    }
}
