# AI-Master Infrastructure模块

[← 返回项目根目录](../CLAUDE.md)

## 模块定位
**层级**: 基础设施层 (Infrastructure Layer)  
**职责**: 技术实现细节、持久化、外部系统集成  
**核心价值**: 为领域层提供技术实现支持，隔离技术复杂性

## 模块结构

```
ai-master-infrastructure/
├── pom.xml                          # Maven配置
├── src/
│   ├── main/
│   │   ├── java/com/ai/master/
│   │   │   ├── appeal/infrastructure/  # 申诉基础设施
│   │   │   ├── order/infrastructure/   # 订单基础设施
│   │   │   ├── goods/infrastructure/   # 商品基础设施
│   │   │   └── common/infrastructure/  # 通用基础设施
│   │   └── resources/
│   │       ├── db/migration/        # 数据库迁移脚本
│   │       ├── mapper/              # MyBatis映射文件
│   │       └── generatorConfig.xml  # MyBatis生成器配置
│   └── test/                        # 基础设施测试
```

## 技术栈实现

### 持久化层

#### MyBatis集成
```java
// 配置类
@Configuration
@MapperScan(basePackages = "com.ai.master.**.infrastructure.persistence.mapper")
public class MyBatisConfig {
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
            .getResources("classpath*:mapper/**/*.xml"));
        return factoryBean.getObject();
    }
}
```

#### 实体映射
```java
// 申诉实体
@Entity
@Table(name = "appeals")
public class AppealEntity {
    @Id
    private String id;
    private String orderId;
    private String appealerId;
    private String type;
    private String status;
    private String reason;
    private BigDecimal claimedAmount;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    
    @OneToMany(mappedBy = "appealId", cascade = CascadeType.ALL)
    private List<AppealItemEntity> items;
}

// 申诉项实体
@Entity
@Table(name = "appeal_items")
public class AppealItemEntity {
    @Id
    private String id;
    private String appealId;
    private String itemId;
    private String itemName;
    private BigDecimal originalPrice;
    private BigDecimal claimedPrice;
    private String reason;
    private String status;
    private String evidence;
}
```

### 仓储实现

#### 申诉仓储实现
```java
@Repository
public class AppealRepositoryImpl implements AppealRepository {
    
    @Autowired
    private AppealMapper appealMapper;
    
    @Autowired
    private AppealItemMapper appealItemMapper;
    
    @Override
    public Appeal save(Appeal appeal) {
        AppealEntity entity = AppealMapper.toEntity(appeal);
        
        if (appealMapper.existsById(appeal.getId().getValue())) {
            appealMapper.updateByPrimaryKey(entity);
            // 更新申诉项
            appealItemMapper.deleteByAppealId(appeal.getId().getValue());
        } else {
            appealMapper.insert(entity);
        }
        
        // 保存申诉项
        for (AppealItem item : appeal.getItems()) {
            AppealItemEntity itemEntity = AppealItemMapper.toEntity(item);
            appealItemMapper.insert(itemEntity);
        }
        
        return appeal;
    }
    
    @Override
    public Optional<Appeal> findById(AppealId id) {
        AppealEntity entity = appealMapper.selectByPrimaryKey(id.getValue());
        if (entity == null) {
            return Optional.empty();
        }
        
        List<AppealItemEntity> itemEntities = appealItemMapper.selectByAppealId(id.getValue());
        entity.setItems(itemEntities);
        
        Appeal appeal = AppealMapper.toDomain(entity);
        return Optional.of(appeal);
    }
    
    @Override
    public List<Appeal> findByOrderId(OrderId orderId) {
        List<AppealEntity> entities = appealMapper.selectByOrderId(orderId.getValue());
        return entities.stream()
            .map(AppealMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<Appeal> findByCondition(AppealQueryCondition condition, Pageable pageable) {
        int total = appealMapper.countByCondition(condition);
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        List<AppealEntity> entities = appealMapper.selectByCondition(condition, pageable);
        List<Appeal> appeals = entities.stream()
            .map(AppealMapper::toDomain)
            .collect(Collectors.toList());
        
        return new PageImpl<>(appeals, pageable, total);
    }
}
```

### 数据访问对象 (DAO)

#### 申诉DAO接口
```java
@Repository
public interface AppealDao {
    
    @Select("SELECT * FROM appeals WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "appealerId", column = "appealer_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "status", column = "status"),
        @Result(property = "reason", column = "reason"),
        @Result(property = "claimedAmount", column = "claimed_amount"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "submittedAt", column = "submitted_at")
    })
    AppealEntity selectById(String id);
    
    @Select("SELECT * FROM appeals WHERE order_id = #{orderId}")
    List<AppealEntity> selectByOrderId(String orderId);
    
    @Insert("INSERT INTO appeals (id, order_id, appealer_id, type, status, reason, claimed_amount, created_at) " +
            "VALUES (#{id}, #{orderId}, #{appealerId}, #{type}, #{status}, #{reason}, #{claimedAmount}, #{createdAt})")
    int insert(AppealEntity entity);
    
    @Update("UPDATE appeals SET status = #{status}, submitted_at = #{submittedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status, 
                     @Param("submittedAt") LocalDateTime submittedAt);
    
    @SelectProvider(type = AppealSqlProvider.class, method = "countByCondition")
    int countByCondition(AppealQueryCondition condition);
    
    @SelectProvider(type = AppealSqlProvider.class, method = "selectByCondition")
    List<AppealEntity> selectByCondition(@Param("condition") AppealQueryCondition condition,
                                          @Param("pageable") Pageable pageable);
}
```

### 数据库迁移

#### Flyway迁移脚本
```sql
-- V1__create_appeal_tables.sql
CREATE TABLE appeals (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    appealer_id VARCHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason TEXT,
    claimed_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    audited_at TIMESTAMP NULL,
    audit_result VARCHAR(20),
    INDEX idx_order_id (order_id),
    INDEX idx_appealer_id (appealer_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

CREATE TABLE appeal_items (
    id VARCHAR(36) PRIMARY KEY,
    appeal_id VARCHAR(36) NOT NULL,
    item_id VARCHAR(36) NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    original_price DECIMAL(10,2) NOT NULL,
    claimed_price DECIMAL(10,2) NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL,
    evidence TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appeal_id) REFERENCES appeals(id) ON DELETE CASCADE,
    INDEX idx_appeal_id (appeal_id),
    INDEX idx_item_id (item_id)
);

CREATE TABLE appealers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);
```

### MyBatis生成器配置

#### generatorConfig.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <context id="Mysql" targetRuntime="MyBatis3Simple" defaultModelType="flat">
        <property name="beginningDelimiter" value="`"/>
        <property name="endingDelimiter" value="`"/>
        
        <!-- 通用插件 -->
        <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
            <property name="mappers" value="tk.mybatis.mapper.common.Mapper"/>
        </plugin>
        
        <!-- 数据库连接 -->
        <jdbcConnection driverClass="com.mysql.cj.jdbc.Driver"
                       connectionURL="jdbc:mysql://localhost:3306/ai_master"
                       userId="root"
                       password="password">
        </jdbcConnection>
        
        <!-- 模型生成 -->
        <javaModelGenerator targetPackage="com.ai.master.appeal.infrastructure.persistence.entity"
                           targetProject="src/main/java">
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>
        
        <!-- SQL映射文件 -->
        <sqlMapGenerator targetPackage="mapper"
                         targetProject="src/main/resources"/>
        
        <!-- Mapper接口 -->
        <javaClientGenerator type="XMLMAPPER"
                            targetPackage="com.ai.master.appeal.infrastructure.persistence.mapper"
                            targetProject="src/main/java"/>
        
        <!-- 表配置 -->
        <table tableName="appeals" domainObjectName="AppealEntity">
            <generatedKey column="id" sqlStatement="JDBC" identity="true"/>
        </table>
        
        <table tableName="appeal_items" domainObjectName="AppealItemEntity">
            <generatedKey column="id" sqlStatement="JDBC" identity="true"/>
        </table>
        
    </context>
</generatorConfiguration>
```

### 外部系统集成

#### 商品服务调用
```java
@Component
public class GoodsCall {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${external.goods.service.url}")
    private String goodsServiceUrl;
    
    public GoodsInfoDTO getGoodsInfo(String goodsId) {
        String url = goodsServiceUrl + "/api/goods/" + goodsId;
        try {
            return restTemplate.getForObject(url, GoodsInfoDTO.class);
        } catch (RestClientException e) {
            log.error("获取商品信息失败: {}", goodsId, e);
            throw new InfrastructureException("商品服务调用失败", e);
        }
    }
    
    public boolean validateGoodsPrice(String goodsId, BigDecimal price) {
        GoodsInfoDTO goods = getGoodsInfo(goodsId);
        return goods != null && goods.getPrice().equals(price);
    }
}
```

### 缓存实现

#### Redis缓存配置
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用Jackson序列化
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        template.setDefaultSerializer(serializer);
        
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

### 配置管理

#### 数据源配置
```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        
        // 配置MyBatis
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        factoryBean.setConfiguration(configuration);
        
        return factoryBean.getObject();
    }
    
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

### 监控与健康检查

#### 数据库健康检查
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public Health health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return Health.up()
                .withDetail("database", "MySQL")
                .withDetail("status", "Connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "MySQL")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### 测试策略

#### 仓储测试
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppealRepositoryImplTest {
    
    @Autowired
    private AppealRepository appealRepository;
    
    @Test
    void should_save_and_retrieve_appeal() {
        // Given
        Appeal appeal = createTestAppeal();
        
        // When
        Appeal saved = appealRepository.save(appeal);
        Optional<Appeal> retrieved = appealRepository.findById(saved.getId());
        
        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(saved.getId(), retrieved.get().getId());
        assertEquals(2, retrieved.get().getItems().size());
    }
    
    @Test
    void should_find_appeals_by_order_id() {
        // Given
        OrderId orderId = OrderId.of("ORD001");
        Appeal appeal1 = createAppealWithOrder(orderId);
        Appeal appeal2 = createAppealWithOrder(orderId);
        
        appealRepository.save(appeal1);
        appealRepository.save(appeal2);
        
        // When
        List<Appeal> appeals = appealRepository.findByOrderId(orderId);
        
        // Then
        assertEquals(2, appeals.size());
    }
}
```

### Maven配置

#### 核心依赖
```xml
<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- MyBatis通用Mapper -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>2.1.5</version>
</dependency>

<!-- MySQL驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- 数据库连接池 -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
</dependency>

<!-- MyBatis生成器 -->
<dependency>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-core</artifactId>
    <version>1.4.0</version>
    <scope>provided</scope>
</dependency>
```

## 性能优化

### 数据库优化
- **索引优化**: 为查询字段添加合适索引
- **分页查询**: 使用基于游标的分页提高性能
- **批量操作**: 批量插入/更新减少数据库往返
- **连接池调优**: 合理配置HikariCP参数

### 缓存策略
- **查询缓存**: 对热点查询结果进行缓存
- **写穿透**: 先更新数据库，再删除缓存
- **缓存预热**: 启动时预热关键数据

## 监控与运维

### 数据库监控
- **慢查询监控**: 监控慢SQL查询
- **连接池监控**: 监控连接池使用情况
- **缓存命中率**: 监控Redis缓存效果

### 日志配置
```yaml
# application.yml
logging:
  level:
    com.ai.master.infrastructure.persistence: DEBUG
    org.mybatis: DEBUG
    org.springframework.jdbc: DEBUG
```

## 相关链接
- [领域模型](../ai-master-domain/README.md)
- [应用层服务](../ai-master-application/README.md)
- [服务实现](../ai-master-service/README.md)