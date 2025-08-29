# AI-Master Domain模块

[← 返回项目根目录](../CLAUDE.md)

## 模块定位
**层级**: 领域层 (Domain Layer)  
**职责**: 核心业务逻辑、领域模型、业务规则  
**核心价值**: 封装业务知识，确保业务逻辑的一致性和完整性

## 模块结构

```
ai-master-domain/
├── pom.xml                          # Maven配置
├── src/
│   ├── main/
│   │   └── java/com/ai/master/
│   │       ├── appeal/domain/       # 申诉域
│   │       ├── order/domain/        # 订单域
│   │       ├── inventory/domain/    # 库存域
│   │       ├── goods/domain/        # 商品域
│   │       ├── user/domain/         # 用户域
│   │       └── common/domain/       # 通用域概念
│   └── test/                        # 领域模型测试
```

## 领域模型概览

### 核心聚合根

#### 申诉域 (Appeal Domain)
```
entity/
├── Appeal.java                     # 申诉聚合根
├── AppealItem.java                 # 申诉项实体
└── Appealer.java                   # 申诉人实体

valueobject/
├── AppealId.java                   # 申诉ID值对象
├── AppealItemId.java               # 申诉项ID值对象
└── AppealerId.java                 # 申诉人ID值对象

enums/
├── AppealStatus.java               # 申诉状态
├── AppealItemStatus.java           # 申诉项状态
├── AppealType.java                 # 申诉类型
└── AppealerType.java               # 申诉人类型

repository/
├── AppealRepository.java           # 申诉仓储接口
└── AppealerRepository.java         # 申诉人仓储接口

service/
├── AppealDomainService.java        # 申诉领域服务
└── AppealValidationService.java    # 申诉验证服务
```

#### 订单域 (Order Domain)
```
model/
├── Order.java                      # 订单聚合根
├── OrderId.java                    # 订单ID值对象
├── OrderAmount.java                # 订单金额值对象
├── OrderState.java                 # 订单状态
└── OrderStatus.java                # 订单状态枚举

repository/
└── OrderRepository.java            # 订单仓储接口

service/
└── OrderDomainService.java         # 订单领域服务
```

## 领域模型详解

### 申诉聚合根 (Appeal Aggregate)

#### 核心属性
```java
public class Appeal extends BaseEntity {
    private AppealId id;                    # 申诉ID
    private OrderId orderId;               # 关联订单ID
    private AppealerId appealerId;         # 申诉人ID
    private AppealType type;               # 申诉类型
    private AppealStatus status;           # 申诉状态
    private String reason;                 # 申诉原因
    private List<AppealItem> items;       # 申诉项列表
    private MonetaryAmount claimedAmount;  # 索赔金额
    private LocalDateTime createdAt;       # 创建时间
    private LocalDateTime submittedAt;     # 提交时间
}
```

#### 业务行为
```java
public class Appeal {
    
    // 创建申诉
    public static Appeal create(OrderId orderId, AppealType type, String reason) {
        Appeal appeal = new Appeal();
        appeal.id = AppealId.newId();
        appeal.orderId = orderId;
        appeal.type = type;
        appeal.reason = reason;
        appeal.status = AppealStatus.CREATED;
        appeal.items = new ArrayList<>();
        appeal.createdAt = LocalDateTime.now();
        return appeal;
    }
    
    // 添加申诉项
    public void addItem(AppealItem item) {
        if (status != AppealStatus.CREATED) {
            throw new BusinessException("当前状态不允许添加申诉项");
        }
        items.add(item);
    }
    
    // 提交申诉
    public void submit() {
        if (status != AppealStatus.CREATED) {
            throw new BusinessException("当前状态不允许提交");
        }
        if (items.isEmpty()) {
            throw new BusinessException("申诉项不能为空");
        }
        this.status = AppealStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        
        // 发布领域事件
        registerEvent(new AppealSubmittedEvent(this.id));
    }
    
    // 审核申诉
    public void audit(AppealAuditResult auditResult) {
        if (status != AppealStatus.AUDITING) {
            throw new BusinessException("当前状态不允许审核");
        }
        
        this.status = auditResult.getStatus();
        this.auditResult = auditResult;
        
        // 发布审核完成事件
        registerEvent(new AppealAuditedEvent(this.id, auditResult));
    }
    
    // 业务规则验证
    public boolean canSubmit() {
        return status == AppealStatus.CREATED && !items.isEmpty();
    }
    
    public boolean canAddItem() {
        return status == AppealStatus.CREATED;
    }
}
```

### 申诉项实体 (Appeal Item)
```java
public class AppealItem extends BaseEntity {
    private AppealItemId id;            # 申诉项ID
    private AppealId appealId;          # 所属申诉ID
    private String itemId;              # 商品项ID
    private String itemName;            # 商品名称
    private BigDecimal originalPrice;   # 原始价格
    private BigDecimal claimedPrice;    # 申诉价格
    private String reason;              # 申诉理由
    private AppealItemStatus status;    # 处理状态
    private String evidence;            # 证据材料
}
```

### 值对象设计

#### 申诉ID (AppealId)
```java
public class AppealId extends ValueObject {
    private final String value;
    
    private AppealId(String value) {
        this.value = value;
    }
    
    public static AppealId of(String value) {
        return new AppealId(value);
    }
    
    public static AppealId newId() {
        return new AppealId(UUID.randomUUID().toString());
    }
    
    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
```

#### 金额值对象 (MonetaryAmount)
```java
public class MonetaryAmount extends ValueObject {
    private final BigDecimal amount;
    private final String currency;
    
    public MonetaryAmount(BigDecimal amount, String currency) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }
    
    public MonetaryAmount add(MonetaryAmount other) {
        return new MonetaryAmount(this.amount.add(other.amount), this.currency);
    }
    
    public boolean isGreaterThan(MonetaryAmount other) {
        return this.amount.compareTo(other.amount) > 0;
    }
}
```

## 领域服务

### 申诉领域服务
```java
public class AppealDomainService {
    
    public AppealValidationResult validateAppeal(Appeal appeal) {
        // 验证申诉的完整性
        if (appeal.getItems().isEmpty()) {
            return AppealValidationResult.failure("申诉项不能为空");
        }
        
        // 验证申诉金额的合理性
        BigDecimal totalClaimed = appeal.getItems().stream()
            .map(item -> item.getClaimedPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalClaimed.compareTo(BigDecimal.ZERO) <= 0) {
            return AppealValidationResult.failure("申诉金额必须大于0");
        }
        
        return AppealValidationResult.success();
    }
    
    public AppealAuditResult calculateAuditResult(Appeal appeal) {
        // 计算审核结果
        List<AppealItem> approvedItems = appeal.getItems().stream()
            .filter(item -> item.getStatus() == AppealItemStatus.APPROVED)
            .collect(Collectors.toList());
        
        if (approvedItems.isEmpty()) {
            return AppealAuditResult.allRejected();
        }
        
        if (approvedItems.size() == appeal.getItems().size()) {
            return AppealAuditResult.allApproved();
        }
        
        return AppealAuditResult.partialApproved(approvedItems);
    }
}
```

### 订单领域服务
```java
public class OrderDomainService {
    
    public Order createOrder(OrderCreateCommand command) {
        // 业务规则验证
        validateOrderCreate(command);
        
        // 创建订单聚合根
        Order order = Order.create(
            OrderId.newId(),
            command.getBuyerId(),
            command.getItems(),
            command.getAmount()
        );
        
        // 计算订单金额
        order.calculateAmount();
        
        return order;
    }
    
    private void validateOrderCreate(OrderCreateCommand command) {
        // 验证商品有效性
        for (OrderItem item : command.getItems()) {
            Goods goods = goodsRepository.findById(item.getGoodsId())
                .orElseThrow(() -> new BusinessException("商品不存在: " + item.getGoodsId()));
            
            if (!goods.isAvailable()) {
                throw new BusinessException("商品不可用: " + item.getGoodsId());
            }
        }
        
        // 验证金额计算
        BigDecimal calculatedAmount = calculateTotalAmount(command.getItems());
        if (calculatedAmount.compareTo(command.getAmount()) != 0) {
            throw new BusinessException("金额计算错误");
        }
    }
}
```

## 仓储接口

### 申诉仓储接口
```java
public interface AppealRepository {
    
    Appeal save(Appeal appeal);
    
    Optional<Appeal> findById(AppealId id);
    
    List<Appeal> findByOrderId(OrderId orderId);
    
    List<Appeal> findByAppealerId(AppealerId appealerId);
    
    boolean existsByOrderIdAndStatus(OrderId orderId, AppealStatus status);
    
    // 分页查询
    Page<Appeal> findByCondition(AppealQueryCondition condition, Pageable pageable);
}
```

### 规格模式 (Specification Pattern)
```java
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);
}

public class AppealCanSubmitSpecification implements Specification<Appeal> {
    @Override
    public boolean isSatisfiedBy(Appeal appeal) {
        return appeal.getStatus() == AppealStatus.CREATED
            && !appeal.getItems().isEmpty()
            && appeal.getItems().stream()
                .allMatch(item -> item.getStatus() == AppealItemStatus.PENDING);
    }
}
```

## 领域事件

### 申诉领域事件
```java
public class AppealSubmittedEvent extends DomainEvent {
    private final AppealId appealId;
    private final OrderId orderId;
    private final LocalDateTime submittedAt;
    
    public AppealSubmittedEvent(AppealId appealId) {
        super(UUID.randomUUID().toString(), LocalDateTime.now());
        this.appealId = appealId;
        this.submittedAt = LocalDateTime.now();
    }
}

public class AppealAuditedEvent extends DomainEvent {
    private final AppealId appealId;
    private final AppealAuditResult result;
    private final LocalDateTime auditedAt;
    
    public AppealAuditedEvent(AppealId appealId, AppealAuditResult result) {
        super(UUID.randomUUID().toString(), LocalDateTime.now());
        this.appealId = appealId;
        this.result = result;
        this.auditedAt = LocalDateTime.now();
    }
}
```

## 业务规则

### 申诉状态机
```
CREATED → VERIFIED → SUBMITTED → AUDITING → [ALL_PASSED/PARTIAL_PASSED/ALL_REJECTED/CANCELLED]
```

### 业务约束
1. **申诉创建约束**
   - 订单必须处于完成状态
   - 申诉人必须是订单相关方
   - 同一订单不能有多条未完成的申诉

2. **申诉提交约束**
   - 必须至少有一个申诉项
   - 所有申诉项必须处于待处理状态
   - 申诉金额必须大于0

3. **申诉审核约束**
   - 只能审核已提交的申诉
   - 审核结果必须包含所有申诉项的处理意见
   - 审核通过后金额调整必须合理

## 测试策略

### 单元测试
```java
class AppealTest {
    
    @Test
    void should_create_appeal_with_valid_data() {
        // Given
        OrderId orderId = OrderId.of("ORD001");
        AppealType type = AppealType.PRICE_DISPUTE;
        String reason = "价格与实际不符";
        
        // When
        Appeal appeal = Appeal.create(orderId, type, reason);
        
        // Then
        assertNotNull(appeal.getId());
        assertEquals(orderId, appeal.getOrderId());
        assertEquals(type, appeal.getType());
        assertEquals(AppealStatus.CREATED, appeal.getStatus());
    }
    
    @Test
    void should_submit_appeal_when_valid() {
        // Given
        Appeal appeal = createValidAppeal();
        appeal.addItem(createValidAppealItem());
        
        // When
        appeal.submit();
        
        // Then
        assertEquals(AppealStatus.SUBMITTED, appeal.getStatus());
        assertNotNull(appeal.getSubmittedAt());
    }
    
    @Test
    void should_not_submit_appeal_without_items() {
        // Given
        Appeal appeal = createValidAppeal();
        
        // When & Then
        assertThrows(BusinessException.class, () -> appeal.submit());
    }
}
```

### 领域服务测试
```java
class AppealDomainServiceTest {
    
    @Test
    void should_calculate_correct_audit_result() {
        // Given
        Appeal appeal = createAppealWithItems();
        AppealDomainService service = new AppealDomainService();
        
        // When
        AppealAuditResult result = service.calculateAuditResult(appeal);
        
        // Then
        assertEquals(AppealAuditResultType.PARTIAL_APPROVED, result.getType());
        assertEquals(new BigDecimal("50.00"), result.getApprovedAmount());
    }
}
```

## 性能考虑

### 聚合边界优化
- **小聚合**: 避免聚合根过大，保持聚合的完整性
- **延迟加载**: 使用延迟加载优化性能
- **快照模式**: 对复杂聚合使用快照模式

### 缓存策略
```java
public class CachedAppealRepository implements AppealRepository {
    
    @Autowired
    private RedisTemplate<String, Appeal> redisTemplate;
    
    @Override
    public Optional<Appeal> findById(AppealId id) {
        String key = "appeal:" + id.getValue();
        Appeal appeal = redisTemplate.opsForValue().get(key);
        
        if (appeal == null) {
            appeal = delegate.findById(id).orElse(null);
            if (appeal != null) {
                redisTemplate.opsForValue().set(key, appeal, Duration.ofMinutes(30));
            }
        }
        
        return Optional.ofNullable(appeal);
    }
}
```

## Maven配置

### 核心依赖
```xml
<!-- 领域模型无外部依赖，保持纯净 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <scope>provided</scope>
</dependency>

<!-- 测试依赖 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>test</scope>
</dependency>
```

## 设计原则

### DDD原则
1. **聚合根完整性**: 确保聚合根维护其不变量
2. **值对象不可变性**: 值对象一旦创建不可修改
3. **领域服务无状态**: 领域服务不维护任何状态
4. **仓储接口抽象**: 仓储接口定义在领域层，实现放在基础设施层

### 代码规范
```
domain/
├── {aggregate}/
│   ├── entity/          # 实体
│   ├── valueobject/     # 值对象
│   ├── repository/      # 仓储接口
│   ├── service/         # 领域服务
│   ├── event/           # 领域事件
│   ├── specification/   # 规格
│   └── factory/         # 工厂
```

## 相关链接
- [应用层服务](../ai-master-application/README.md)
- [基础设施层](../ai-master-infrastructure/README.md)
- [仓储实现](../ai-master-infrastructure/README.md)