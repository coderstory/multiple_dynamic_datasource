package com.example.multipledynamicdatasource;

import com.example.multipledynamicdatasource.utils.DataSourceProperties;
import com.example.multipledynamicdatasource.utils.DataSourceUtils;
import com.example.multipledynamicdatasource.utils.DatabaseType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MultipleDynamicDatasourceApplicationTests {

    @Resource
    JdbcTemplate jdbcTemplate;
    @Resource
    DataSourceUtils dataSourceUtils;

    @Test
    void contextLoads() {
    }

    @Test
    public void testConnection() {
        List<Map<String, Object>> show_databases = jdbcTemplate.queryForList("show databases");
    }

    @Test
    public void testAddDataSource() {
        DataSourceProperties root = DataSourceProperties.builder().username("root").password("123456").type(DatabaseType.MYSQL).ip("127.0.0.1").build();
        dataSourceUtils.addDataSource(root);
        dataSourceUtils.setCurrentKey(root.buildKey());
        List<Map<String, Object>> show_databases = jdbcTemplate.queryForList("show databases");
    }
}
