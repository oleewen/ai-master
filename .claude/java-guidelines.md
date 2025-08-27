# Java Coding Guidelines

## Technology Stack
- **Java**: 17+
- **Spring Boot**: 2.7.10
- **MyBatis**: tk.mybatis for database access
- **Maven**: Build management
- **Lombok**: POJO utilities
- **MapStruct**: Object mapping
- **JUnit 4**: Testing framework

## Coding Standards

### 1. General Principles
- Use Java 17+ features (records, var, switch expressions)
- Follow Spring Boot best practices
- Apply DDD principles in package structure
- Use Bean Validation (@Valid) for input validation
- Implement global exception handling

### 2. Class Structure
```java
// API Layer
@Data
public class OrderCreateRequest {
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;
    
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
}

// Domain Layer
@Entity
public class Order {
    @Id
    private OrderId orderId;
    
    @Embedded
    private OrderAmount amount;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
```

### 3. Service Layer
```java
@Service
public class OrderApplicationService {
    
    public OrderCreateResult createOrder(OrderCreateCommand command) {
        // Validate command
        // Orchestrate domain services
        // Return result
    }
}
```

### 4. Repository Pattern
```java
@Repository
public interface OrderRepository {
    Order findById(OrderId orderId);
    void save(Order order);
    List<Order> findByBuyerId(BuyerId buyerId);
}
```

### 5. Object Mapping
```java
@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    Order toEntity(OrderCreateCommand command);
}
```

### 6. Testing
```java
@SpringBootTest
public class OrderServiceTest {
    
    @Test
    public void shouldCreateOrderSuccessfully() {
        // Given
        OrderCreateCommand command = new OrderCreateCommand();
        
        // When
        OrderCreateResult result = orderService.createOrder(command);
        
        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
    }
}
```

### 7. Exception Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
```

### 8. Validation
- Use Bean Validation annotations (@NotNull, @Size, @Pattern)
- Create custom validators for complex rules
- Validate at API layer, not domain layer

### 9. Configuration
- Use @ConfigurationProperties for external configuration
- Use profiles for environment-specific settings
- Keep configuration separate from code