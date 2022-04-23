package com.example.multipledynamicdatasource.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 动态数据源的配置类
 * 支持 spring.datasource下的标准配置
 * 也可以自己配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {
    private String ip;
    private String port;
    private String username;
    private String password;
    // 数据库连接字符串中的参数
    private Map<String, String> optional;
    // 数据库类型
    private DatabaseType type;
    private String url;
    private String dataBaseName;

    public String buildUrl() {
        return type.buildUrl(this);
    }

    public String buildKey() {
        String key = StringUtils.defaultIfBlank(dataBaseName, "root");
        return key + "_" + DigestUtils.md5DigestAsHex((buildUrl() + username + password).getBytes(StandardCharsets.UTF_8));
    }
}
