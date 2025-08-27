# Testing Guidelines

## Quality Metrics
- **Unit Test Coverage**: ≥80%
- **Code Duplication**: ≤5%
- **Critical Issues**: 0
- **Compilation Warnings**: 0

## Testing Strategy

### 1. Testing Pyramid
```
Unit Tests (80%)
├── Domain Model Tests
├── Service Tests
├── Repository Tests
└── Controller Tests

Integration Tests (15%)
├── API Integration Tests
├── Database Integration Tests
└── Service Integration Tests

End-to-End Tests (5%)
├── User Journey Tests
└── Critical Path Tests
```

### 2. Test Structure
```
src/test/java/
├── com/ai/master/
│   ├── order/
│   │   ├── api/
│   │   │   └── OrderApiTest.java
│   │   ├── application/
│   │   │   └── OrderApplicationServiceTest.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── OrderTest.java
│   │   │   └── service/
│   │   │       └── OrderDomainServiceTest.java
│   │   └── infrastructure/
│   │       └── OrderRepositoryTest.java
│   └── appeal/
│       └── ...
```

### 3. Unit Testing

#### Domain Model Tests
```java
@Test
public void shouldCreateOrderWithValidParameters() {
    // Given
    BuyerId buyerId = BuyerId.of(1L);
    Goods goods = Goods.create("商品名称", Price.of(1000));
    
    // When
    Order order = Order.create(buyerId, goods, 2);
    
    // Then
    assertNotNull(order.getOrderId());
    assertEquals(buyerId, order.getBuyerId());
    assertEquals(2, order.getQuantity());
}

@Test
public void shouldRejectInvalidOrderQuantity() {
    // Given
    BuyerId buyerId = BuyerId.of(1L);
    Goods goods = Goods.create("商品名称", Price.of(1000));
    
    // When/Then
    assertThrows(IllegalArgumentException.class, 
        () -> Order.create(buyerId, goods, 0));
}
```

#### Service Layer Tests
```java
@SpringBootTest
public class OrderApplicationServiceTest {
    
    @Autowired
    private OrderApplicationService orderService;
    
    @MockBean
    private OrderRepository orderRepository;
    
    @Test
    public void shouldCreateOrderSuccessfully() {
        // Given
        OrderCreateCommand command = new OrderCreateCommand(1L, 2L, 3);
        
        // When
        OrderCreateResult result = orderService.createOrder(command);
        
        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
    }
}
```

### 4. Integration Testing

#### API Integration
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderApiIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void shouldCreateOrderViaApi() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setGoodsId(1L);
        request.setQuantity(2);
        
        // When
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
            "/api/orders", request, OrderResponse.class);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getOrderId());
    }
}
```

### 5. Test Data Management

#### Test Data Builders
```java
public class OrderTestBuilder {
    
    public static Order createValidOrder() {
        return Order.create(
            BuyerId.of(1L),
            GoodsTestBuilder.createValidGoods(),
            2
        );
    }
    
    public static Order createOrderWithStatus(OrderStatus status) {
        Order order = createValidOrder();
        // Set status appropriately
        return order;
    }
}
```

### 6. Test Categories

#### Unit Tests
- Test individual classes in isolation
- Mock external dependencies
- Focus on business logic
- Fast execution (< 100ms)

#### Integration Tests
- Test component interactions
- Use test database (H2)
- Test Spring context loading
- Include API endpoints

#### End-to-End Tests
- Test complete user flows
- Use real database
- Include external services
- Focus on critical paths

### 7. Mocking Strategy
```java
@MockBean
private OrderRepository orderRepository;

@Before
public void setup() {
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    
    when(orderRepository.findById(any(OrderId.class)))
        .thenReturn(Optional.of(createValidOrder()));
}
```

### 8. Test Coverage
- **Line Coverage**: ≥80%
- **Branch Coverage**: ≥70%
- **Method Coverage**: ≥80%
- **Class Coverage**: ≥90%

### 9. Test Naming
- **Unit Tests**: `should{Action}When{Condition}`
- **Integration Tests**: `should{Action}Via{Component}`
- **Edge Cases**: `should{ExpectedBehavior}When{EdgeCase}`

### 10. Continuous Testing
```bash
# Run specific test
mvn test -Dtest=OrderServiceTest

# Run with coverage
mvn test jacoco:report

# Run integration tests
mvn verify -P integration-test
```