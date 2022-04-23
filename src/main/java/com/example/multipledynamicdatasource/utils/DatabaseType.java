package com.example.multipledynamicdatasource.utils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 不同数据库类型的配置类
 * 封装了不同数据库独有的特定配置
 * 使用枚举类实现不同数据类型配置的单例模式和数据库类型的枚举
 */
@Getter
public enum DatabaseType {
    MYSQL("MYSQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://") {
        @Override
        public String buildUrl(DataSourceProperties properties) {
            String url = getUrlPrefix() + properties.getIp();
            if (StringUtils.isNotBlank(properties.getPort())) {
                url += ":" + properties.getPort();
            }
            if (StringUtils.isNotBlank(properties.getDataBaseName())) {
                url += "/" + properties.getDataBaseName();
            }
            if (properties.getOptional() != null && properties.getOptional().size() > 0) {
                List<String> optional = new ArrayList<>();
                properties.getOptional().forEach((k,v)-> optional.add(k + "=" + v));
                url += "?" + String.join("&", optional);
            }
            return url;
        }
    },

    SQLSERVER("SQLSERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost:1433;DatabaseName=数据库名") {
        @Override
        public String buildUrl(DataSourceProperties properties) {
            return null;
        }
    },
    ORACLE("ORACLE", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:orcl") {
        @Override
        public String buildUrl(DataSourceProperties properties) {
            return null;
        }
    };

    private final String type;
    private final String driverName;
    private final String urlPrefix;

    DatabaseType(String type, String driverName, String urlPrefix) {
        this.type = type;
        this.driverName = driverName;
        this.urlPrefix = urlPrefix;
    }

    public DatabaseType ofType(String type) {
        switch (type) {
            case "MYSQL":
                return MYSQL;
            case "SQLSERVER":
                return SQLSERVER;
            case "ORACLE":
                return ORACLE;
            default:
                throw new UnsupportedOperationException("unknown database type :" + type);
        }
    }

    public abstract String buildUrl(DataSourceProperties properties);

}
