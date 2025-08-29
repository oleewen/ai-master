# AI-Master Service模块

[← 返回项目根目录](../CLAUDE.md)

## 模块定位
**层级**: 用户接口层 (Service层)  
**职责**: 提供HTTP/REST接口实现，处理Web层请求，调用应用层服务  
**消费者**: 前端应用、移动端、第三方集成

## 模块结构

```
ai-master-service/
├── pom.xml                          # Maven配置
├── src/
│   ├── main/
│   │   └── java/com/ai/master/
│   │       ├── appeal/service/      # 申诉服务实现
│   │       └── order/service/       # 订单服务实现
│   │           ├── factory/         # DTO/Command工厂
│   │           ├── rpc/             # Dubbo服务提供者
│   │           └── web/             # Web层控制器
│   │               ├── config/      # Web配置
│   │               └── controller/  # REST控制器
│   └── test/                        # 测试代码
```

## 核心组件

### 申诉服务实现
- **AppealServiceImpl** - 申诉服务实现类
  - 处理申诉相关的HTTP请求
  - 调用应用层AppealApplicationService
  - 提供RESTful API接口

### 订单服务实现
- **OrderServiceProvider** - Dubbo服务提供者实现
  - 实现API层定义的OrderService接口
  - 处理RPC调用请求
  - 委托给应用层处理业务逻辑

### Web层控制器
- **OrderController** - 订单HTTP控制器
  - RESTful API接口
  - 请求参数验证
  - 响应数据封装

### 工厂类
- **OrderBuyDTOFactory** - DTO对象工厂
- **OrderCommandFactory** - 命令对象工厂
- **OrderResultFactory** - 结果对象工厂

## 技术实现

### REST控制器示例
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderApplicationService orderAppService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderCommand command = OrderCommandFactory.create(request);
        OrderResult result = orderAppService.createOrder(command);
        return ResponseEntity.ok(OrderResultFactory.createResponse(result));
    }
}
```

### Dubbo服务提供者示例
```java
@Service(version = "1.0.0")
public class OrderServiceProvider implements OrderService {
    
    @Autowired
    private OrderApplicationService orderAppService;
    
    @Override
    public OrderBuyResponse buy(OrderBuyRequest request) {
        OrderCommand command = OrderCommandFactory.createCommand(request);
        OrderResult result = orderAppService.buy(command);
        return OrderResultFactory.createResponse(result);
    }
}
```

### 工厂模式实现
```java
public class OrderCommandFactory {
    
    public static OrderCommand createCommand(OrderBuyRequest request) {
        return OrderCommand.builder()
            .orderId(request.getOrderId())
            .userId(request.getUserId())
            .amount(request.getAmount())
            .build();
    }
    
    public static OrderCommand createCommand(OrderRequest request) {
        return OrderCommand.builder()
            .orderId(request.getOrderId())
            .userId(request.getUserId())
            .amount(request.getAmount())
            .build();
    }
}
```

## 配置管理

### Swagger配置
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.ai.master"))
            .paths(PathSelectors.any())
            .build();
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("AI-Master API文档")
            .description("申诉和订单管理接口文档")
            .version("1.0.0")
            .build();
    }
}
```

## Maven配置

### 核心依赖
```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Dubbo Spring Boot -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
</dependency>

<!-- Swagger -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
</dependency>

<!-- 应用层依赖 -->
<dependency>
    <groupId>com.ai.master</groupId>
    <artifactId>ai-master-application</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- API接口依赖 -->
<dependency>
    <groupId>com.ai.master</groupId>
    <artifactId>ai-master-api</artifactId>
    <version>${project.version}</version>
</dependency>
```

## RESTful API设计

### 申诉API
```
POST   /api/appeals                    # 创建申诉
GET    /api/appeals/{id}              # 查询申诉详情
PUT    /api/appeals/{id}              # 更新申诉
POST   /api/appeals/{id}/submit       # 提交申诉
GET    /api/appeals/{id}/progress     # 查询进度
POST   /api/appeals/{id}/audit        # 审核申诉
```

### 订单API
```
POST   /api/orders                    # 创建订单
GET    /api/orders/{id}               # 查询订单详情
PUT    /api/orders/{id}/cancel        # 取消订单
GET    /api/orders                    # 查询订单列表
```

## 异常处理

### 全局异常处理器
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse error = ErrorResponse.builder()
            .code(e.getCode())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        ErrorResponse error = ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

## 测试策略

### 单元测试
```java
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setOrderId("ORD001");
        request.setAmount(new BigDecimal("100.00"));
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORD001"));
    }
}
```

### 集成测试
- **Dubbo集成测试**: 验证RPC调用链
- **Spring Boot测试**: 验证Web层功能
- **Mock测试**: 模拟应用层服务

## 部署配置

### 应用配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: ai-master-service

dubbo:
  application:
    name: ai-master-service
  registry:
    address: zookeeper://localhost:2181
  protocol:
    name: dubbo
    port: 20880

swagger:
  enabled: true
  title: AI-Master API
  description: 申诉和订单管理接口
  version: 1.0.0
```

## 性能优化

### 连接池配置
```yaml
dubbo:
  protocol:
    threads: 200
    threadpool: fixed
  provider:
    timeout: 5000
    retries: 0
```

### 缓存策略
- **HTTP缓存**: 对GET请求启用ETag
- **Dubbo缓存**: 对频繁调用结果启用缓存
- **应用缓存**: Redis集成用于热点数据

## 监控指标

### 关键指标
- **请求QPS**: HTTP请求每秒处理量
- **响应时间**: API接口响应时间
- **错误率**: 接口错误率统计
- **Dubbo指标**: RPC调用指标

### 监控集成
- **Spring Boot Actuator**: 健康检查和指标
- **Micrometer**: 指标收集和导出
- **Prometheus**: 指标存储和查询

## 安全考虑

### 认证授权
- **JWT Token**: API请求认证
- **权限控制**: 基于角色的访问控制
- **接口鉴权**: 敏感操作需要额外权限验证

### 数据保护
- **敏感信息脱敏**: 日志中不记录敏感信息
- **HTTPS强制**: 生产环境强制HTTPS
- **输入验证**: 防止SQL注入和XSS攻击

## 开发指南

### 新增接口步骤
1. **定义API**: 在ai-master-api模块定义接口
2. **实现服务**: 在service模块实现接口
3. **配置路由**: 配置RESTful路由或Dubbo服务
4. **添加测试**: 编写单元和集成测试
5. **更新文档**: 更新Swagger文档

### 调试技巧
- **本地调试**: 使用Spring Boot DevTools
- **日志配置**: 开启详细日志便于调试
- **Postman**: 使用Postman测试REST API
- **Dubbo Admin**: 使用Dubbo Admin监控服务

## 相关链接
- [API接口定义](../ai-master-api/README.md)
- [应用层服务](../ai-master-application/README.md)
- [领域模型](../ai-master-domain/README.md)