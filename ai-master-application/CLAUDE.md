# AI-Master Application模块

[← 返回项目根目录](../CLAUDE.md)

## 模块定位
**层级**: 应用层 (Application Layer)  
**职责**: 用例编排、事务边界、应用服务协调  
**核心功能**: 将领域层能力组合成具体业务用例，处理跨聚合操作

## 模块结构

```
ai-master-application/
├── pom.xml                          # Maven配置
├── src/
│   ├── main/
│   │   └── java/com/ai/master/
│   │       ├── appeal/application/  # 申诉应用服务
│   │       └── order/application/   # 订单应用服务
│   └── test/                        # 应用层测试
```

## 应用服务架构

### 申诉应用服务
- **AppealApplicationService** - 申诉用例编排服务
  - 处理申诉完整生命周期
  - 协调领域服务和基础设施层
  - 管理事务边界

### 订单应用服务
- **OrderApplicationService** - 订单用例编排服务
  - 处理订单创建、取消等业务用例
  - 协调库存锁定、订单创建等跨聚合操作
  - 管理事务一致性

## 核心概念

### 命令模式 (Command Pattern)
```java
// 申诉命令
command/
├── CreateAppealCommand.java        # 创建申诉命令
├── AddAppealItemCommand.java       # 添加申诉项命令
├── SubmitAppealCommand.java        # 提交申诉命令
├── AuditAppealCommand.java         # 审核申诉命令
└── QueryAppealsCommand.java        # 查询申诉命令

// 订单命令
command/
└── OrderBuyCommand.java           # 订单购买命令
```

### 结果对象 (Result Objects)
```java
// 申诉结果
dto/
├── CreateAppealResult.java         # 创建申诉结果
├── AppealProgressResult.java       # 申诉进度结果
├── AppealResult.java              # 申诉详情结果
└── AppealListResult.java          # 申诉列表结果

// 订单结果
result/
└── OrderBuyResult.java            # 订单购买结果
```

### 业务动作 (Business Actions)
```java
// 订单业务动作
action/
├── OrderCreateAction.java         # 订单创建动作
├── OrderEnableAction.java         # 订单启用动作
└── InventoryLockAction.java       # 库存锁定动作
```

## 用例实现

### 创建申诉用例
```java
@Service
@Transactional
public class AppealApplicationService {
    
    @Autowired
    private AppealDomainService appealDomainService;
    
    @Autowired
    private AppealRepository appealRepository;
    
    public CreateAppealResult createAppeal(CreateAppealCommand command) {
        // 1. 参数验证
        validateCommand(command);
        
        // 2. 业务规则检查
        checkBusinessRules(command);
        
        // 3. 创建申诉聚合
        Appeal appeal = Appeal.create(
            command.getOrderId(),
            command.getAppealType(),
            command.getReason()
        );
        
        // 4. 持久化
        appeal = appealRepository.save(appeal);
        
        // 5. 返回结果
        return CreateAppealResult.builder()
            .appealId(appeal.getId())
            .status(appeal.getStatus())
            .build();
    }
}
```

### 提交申诉用例
```java
public void submitAppeal(SubmitAppealCommand command) {
    // 1. 加载申诉聚合
    Appeal appeal = appealRepository.findById(command.getAppealId())
        .orElseThrow(() -> new BusinessException("申诉不存在"));
    
    // 2. 业务状态检查
    if (!appeal.canSubmit()) {
        throw new BusinessException("申诉状态不允许提交");
    }
    
    // 3. 执行提交操作
    appeal.submit();
    
    // 4. 发布领域事件
    domainEventPublisher.publish(new AppealSubmittedEvent(appeal.getId()));
    
    // 5. 持久化
    appealRepository.save(appeal);
}
```

### 订单购买用例
```java
@Service
@Transactional
public class OrderApplicationService {
    
    @Autowired
    private OrderDomainService orderDomainService;
    
    @Autowired
    private InventoryDomainService inventoryDomainService;
    
    public OrderBuyResult buy(OrderBuyCommand command) {
        // 1. 库存预锁定
        InventoryLockResult lockResult = inventoryDomainService.lockInventory(
            command.getGoodsId(),
            command.getQuantity()
        );
        
        if (!lockResult.isSuccess()) {
            throw new BusinessException("库存不足");
        }
        
        try {
            // 2. 创建订单
            Order order = orderDomainService.createOrder(command);
            
            // 3. 确认库存锁定
            inventoryDomainService.confirmLock(lockResult.getLockId(), order.getId());
            
            // 4. 返回结果
            return OrderBuyResultFactory.create(order);
            
        } catch (Exception e) {
            // 5. 回滚库存锁定
            inventoryDomainService.releaseLock(lockResult.getLockId());
            throw e;
        }
    }
}
```

## 事务管理

### 事务边界
```java
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AppealApplicationService {
    
    // 整个用例在一个事务中执行
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processAppealAudit(AuditAppealCommand command) {
        // 所有操作要么全部成功，要么全部回滚
    }
}
```

### 分布式事务
```java
// 使用Saga模式处理跨服务事务
public class OrderSaga {
    
    public void execute(OrderBuyCommand command) {
        SagaBuilder saga = SagaBuilder.newSaga("order_buy")
            .step("lock_inventory")
                .invoke(inventoryService::lockInventory)
                .compensate(inventoryService::releaseLock)
            .step("create_order")
                .invoke(orderService::createOrder)
                .compensate(orderService::cancelOrder)
            .build();
        
        saga.execute(command);
    }
}
```

## 查询模式

### CQRS实现
```java
// 查询服务
@Component
public class AppealQueryService {
    
    @Autowired
    private AppealQueryRepository queryRepository;
    
    public AppealProgressResult getAppealProgress(String appealId) {
        AppealProgressView view = queryRepository.findProgress(appealId);
        return AppealProgressResult.builder()
            .appealId(view.getAppealId())
            .status(view.getStatus())
            .progress(view.getProgress())
            .build();
    }
    
    public AppealListResult listAppeals(QueryAppealsCommand command) {
        Page<AppealListView> page = queryRepository.findByCondition(command);
        return AppealListResult.builder()
            .total(page.getTotalElements())
            .items(page.getContent())
            .build();
    }
}
```

## 事件驱动

### 领域事件发布
```java
@Component
public class DomainEventPublisher {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void publish(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }
}

// 事件监听器
@Component
public class AppealEventHandler {
    
    @EventListener
    public void handleAppealSubmitted(AppealSubmittedEvent event) {
        // 处理申诉提交事件
        notificationService.notifyAuditors(event.getAppealId());
    }
    
    @EventListener
    public void handleAppealAudited(AppealAuditedEvent event) {
        // 处理申诉审核完成事件
        notificationService.notifyUser(event.getAppealId(), event.getResult());
    }
}
```

## 输入验证

### 命令验证
```java
public class CreateAppealCommand {
    
    @NotNull(message = "订单ID不能为空")
    private String orderId;
    
    @NotNull(message = "申诉类型不能为空")
    @EnumValue(enumClass = AppealType.class)
    private String appealType;
    
    @NotBlank(message = "申诉原因不能为空")
    @Size(max = 1000, message = "申诉原因长度不能超过1000字符")
    private String reason;
}
```

### 业务规则验证
```java
@Component
public class AppealBusinessValidator {
    
    public void validateCreateAppeal(CreateAppealCommand command) {
        // 检查订单是否存在
        Order order = orderRepository.findById(command.getOrderId())
            .orElseThrow(() -> new BusinessException("订单不存在"));
        
        // 检查订单状态是否允许申诉
        if (!order.canAppeal()) {
            throw new BusinessException("当前订单状态不允许申诉");
        }
        
        // 检查是否已有进行中的申诉
        if (appealRepository.hasPendingAppeal(command.getOrderId())) {
            throw new BusinessException("该订单已有进行中的申诉");
        }
    }
}
```

## 测试策略

### 应用层测试
```java
@SpringBootTest
public class AppealApplicationServiceTest {
    
    @Autowired
    private AppealApplicationService appealAppService;
    
    @MockBean
    private AppealRepository appealRepository;
    
    @Test
    public void testCreateAppeal() {
        // Given
        CreateAppealCommand command = CreateAppealCommand.builder()
            .orderId("ORD001")
            .appealType("PRICE_DISPUTE")
            .reason("价格与实际不符")
            .build();
        
        // When
        CreateAppealResult result = appealAppService.createAppeal(command);
        
        // Then
        assertNotNull(result.getAppealId());
        assertEquals(AppealStatus.CREATED, result.getStatus());
    }
}
```

### 集成测试
```java
@SpringBootTest
@AutoConfigureMockMvc
public class AppealIntegrationTest {
    
    @Test
    public void testEndToEndAppealProcess() {
        // 1. 创建申诉
        String appealId = createAppeal();
        
        // 2. 添加申诉项
        addAppealItem(appealId);
        
        // 3. 提交申诉
        submitAppeal(appealId);
        
        // 4. 审核申诉
        auditAppeal(appealId);
        
        // 5. 验证结果
        verifyAppealResult(appealId);
    }
}
```

## 性能优化

### 缓存策略
```java
@Service
public class CachedAppealApplicationService {
    
    @Cacheable(value = "appeals", key = "#appealId")
    public AppealProgressResult getAppealProgress(String appealId) {
        return appealQueryService.getAppealProgress(appealId);
    }
    
    @CacheEvict(value = "appeals", key = "#command.appealId")
    public void submitAppeal(SubmitAppealCommand command) {
        // 提交申诉并清除缓存
    }
}
```

### 异步处理
```java
@Service
public class AsyncAppealApplicationService {
    
    @Async
    public CompletableFuture<AppealResult> processAppealAsync(CreateAppealCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            return processAppeal(command);
        });
    }
}
```

## Maven配置

### 核心依赖
```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<!-- 事务管理 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
</dependency>

<!-- 领域层依赖 -->
<dependency>
    <groupId>com.ai.master</groupId>
    <artifactId>ai-master-domain</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- 基础设施层依赖 -->
<dependency>
    <groupId>com.ai.master</groupId>
    <artifactId>ai-master-infrastructure</artifactId>
    <version>${project.version}</version>
</dependency>
```

## 最佳实践

### 用例设计原则
1. **单一职责**: 每个用例只处理一个业务场景
2. **事务边界**: 明确事务范围，避免事务嵌套
3. **幂等性**: 确保用例操作的幂等性
4. **可观测性**: 记录关键业务操作日志

### 代码组织
```
application/
├── command/           # 命令对象
├── dto/               # 数据传输对象
├── query/             # 查询对象
├── service/           # 应用服务
└── validator/         # 业务验证器
```

## 监控指标

### 业务指标
- **用例成功率**: 各业务用例的成功执行率
- **处理时间**: 用例执行耗时统计
- **并发量**: 同时处理的用例数量

### 技术指标
- **事务成功率**: 事务提交成功率
- **数据库连接**: 连接池使用情况
- **缓存命中率**: 缓存使用效果

## 相关链接
- [领域模型](../ai-master-domain/README.md)
- [基础设施层](../ai-master-infrastructure/README.md)
- [服务实现](../ai-master-service/README.md)