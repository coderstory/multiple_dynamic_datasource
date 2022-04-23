#### Spring boot 实现多数据源和多数据库类型的支持

##  功能实现
  1. 支持动态添加数据源
  2. 支持添加多种类型的数据源
  3. 支持在yml中配置多个数据源
  4. 支持通过传入参数的方式动态切换数据源

## 基本原理
  Spring boot 在启动阶段会注入一个DataSource,使用了 `spring.datasource`下的配置,且使用了`@ConditionalOnMissingBean`注解。
  当用户声明了一个DataSource时，这个默认的DataSource就不会注入。

  而这个Datasource如何使用呢？我们来看看```JdbcTemplate```类时如何操作的。

`JdbcTemplate`的构造函数我们可以看到是直接传入了datasource，当你调用了update之类的方法时，会直接调用datasource的getConnection方法获得数据库连接
  ```
  public JdbcTemplate(DataSource dataSource) {
		setDataSource(dataSource);
		afterPropertiesSet();
	}
	
 ```

spring对jdbcTemplate的bean声明代码如下,我们可以看到jdbctemplate本身就已经是个spring bean，所以我们使用的使用直接注入即可，开箱即用。
  ```
    @Bean
    @Primary
    @ConditionalOnMissingBean({JdbcOperations.class})
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(this.dataSource);
    }
```

## 基本原理
1. 所以我们只需要自己实现DataSource接口实现类并创建为spring bean,用于替换默认的DataSource [为了方便说明，我们假设这个类叫`CustomDataSource`]
2. 此时所有依赖数据源的bean都会使用我们定义的`CustomDataSource`
3. 我们需要将所有的配置好的或者动态添加的数据库配置 转化成DataSource的实例，比如spring默认的`HikariDataSource`
4. 我们把这些DataSource 存放到 `CustomDataSource` 比如声明一个Map<String,DataSource> 这个key是是给数据库连接起的别名 方便后续切换
5. 然后再`CustomDataSource` 中定义一个`ThreadLocal<String>` 用于配置当前线程使用的数据库别名
6. 当执行数据库操作的时候，会调用DataSouce接口类中的`getConnection`方法获取数据库连接，所以我们重写它，基于当前线程设置的数据库别名，从缓存Map中读取对应的DataSource然后真正的调用`getConnection`并返回
7. 此时操作的数据库会变成我们定义的数据库

