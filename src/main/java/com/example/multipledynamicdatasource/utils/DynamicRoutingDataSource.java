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
 * 数据库的切换是基于ThreadLocal实现的，所以多线程情况下需要注意下使用。
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractDataSource implements InitializingBean {

    private DataSource primary;
    private final ThreadLocal<String> currentDatabaseKey = new ThreadLocal<>();
    // ds缓存
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    public DynamicRoutingDataSource() {
    }

    /**
     * 设置默认数据库
     */
    public void setPrimary(DataSource ds) {
        primary = ds;
    }

    /**
     * 指定数据库指向
     * @param key 数据库连接的别名
     */
    protected void setCurrentDatabaseKey(String key) {
        Assert.isTrue(dataSourceMap.containsKey(key), "target datasource not exists");
        currentDatabaseKey.set(key);
    }

    /**
     * 基于当前的数据库指向，获取对应的数据库连接
     */
    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    /**
     * 尚未实现指定账户名和密码来获取连接
     * @param username the database user on whose behalf the connection is
     *  being made
     * @param password the user's password
     */
    @Override
    public Connection getConnection(String username, String password) {
        throw new UnsupportedOperationException();
    }

    /**
     * 基于当前的数据库指向，获取对应的DS
     */
    private DataSource determineTargetDataSource() {
        if (currentDatabaseKey.get() == null) {
            Assert.notNull(primary, "primary datasource must be not null");
            return primary;
        } else if (!dataSourceMap.containsKey(currentDatabaseKey.get())) {
            return dataSourceMap.get(currentDatabaseKey.get());
        } else {
            throw new IllegalStateException("invalid database key :"+ currentDatabaseKey.get());
        }
    }

    /**
     * 判断是否已存在指定别名的数据库连接
     * @param key 别名
     * @return 是否存在
     */
    protected boolean containsDataBaseKey(String key) {
        return dataSourceMap.containsKey(key);
    }

    /**
     * 主动添加一个DS
     * @param key DS的别名
     * @param dataSource DS
     */
    protected void addDataSource(String key, DataSource dataSource) {
        dataSourceMap.put(key, dataSource);
    }


    @Override
    public void afterPropertiesSet() {
    }

    /**
     * 因为线程存在复用的场景 所以数据库主动切换并使用完毕后 必须调用reset方法
     * 否则下次复用线程的时候 调用了的是本次修改后的数据库。
     */
   public void reset() {
       currentDatabaseKey.remove();
    }
}
