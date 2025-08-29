# AI-Master Common模块

[← 返回项目根目录](../CLAUDE.md)

## 模块定位
**层级**: 共享模块 (Shared Module)  
**职责**: 通用工具、常量、异常处理、基础类型  
**核心价值**: 提供跨层共享的基础能力，避免重复造轮子

## 模块结构

```
ai-master-common/
├── pom.xml                          # Maven配置
├── src/
│   ├── main/
│   │   └── java/com/ai/master/
│   │       ├── common/              # 通用工具包
│   │       │   ├── consts/          # 常量定义
│   │       │   ├── exception/       # 异常类
│   │       │   ├── result/          # 统一结果封装
│   │       │   └── util/            # 工具类
│   │       └── order/common/        # 订单通用模块
│   └── test/                        # 通用工具测试
```

## 核心组件

### 统一结果封装 (Result)
```java
// 统一API响应结果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;           # 响应码
    private String message;        # 响应消息
    private T data;               # 响应数据
    private Long timestamp;       # 时间戳
    
    // 成功响应
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, System.currentTimeMillis());
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, System.currentTimeMillis());
    }
    
    // 错误响应
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message, null, System.currentTimeMillis());
    }
    
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, System.currentTimeMillis());
    }
}
```

### 响应码枚举 (ResultCode)
```java
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    // 成功响应
    SUCCESS("200", "操作成功"),
    
    // 客户端错误
    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未授权访问"),
    FORBIDDEN("403", "禁止访问"),
    NOT_FOUND("404", "资源不存在"),
    
    // 业务错误
    BUSINESS_ERROR("5001", "业务处理失败"),
    VALIDATION_ERROR("5002", "数据验证失败"),
    DUPLICATE_ERROR("5003", "数据已存在"),
    NOT_ENOUGH_INVENTORY("5004", "库存不足"),
    
    // 系统错误
    ERROR("500", "系统内部错误"),
    SERVICE_UNAVAILABLE("503", "服务暂不可用");
    
    private final String code;
    private final String message;
}
```

### 分页工具类 (PageResult)
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<T> data;          # 数据列表
    private long total;            # 总记录数
    private int pageNo;            # 当前页码
    private int pageSize;          # 每页记录数
    private int totalPages;        # 总页数
    
    public static <T> PageResult<T> of(List<T> data, long total, int pageNo, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setData(data);
        result.setTotal(total);
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        return result;
    }
    
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0, 1, 10, 0);
    }
}
```

### 异常基类

#### 业务异常 (BusinessException)
```java
public class BusinessException extends RuntimeException {
    private final String code;
    private final Object[] args;
    
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.args = null;
    }
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.args = null;
    }
    
    public BusinessException(String code, String message, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
    }
    
    public String getCode() {
        return code;
    }
    
    public Object[] getArgs() {
        return args;
    }
}
```

#### 系统异常 (SystemException)
```java
public class SystemException extends RuntimeException {
    private final String code;
    
    public SystemException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
    }
    
    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.ERROR.getCode();
    }
    
    public SystemException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
    }
    
    public String getCode() {
        return code;
    }
}
```

### 通用工具类

#### 字符串工具类 (StringUtils)
```java
public class StringUtils {
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
    
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
    
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return collection.stream()
            .map(Object::toString)
            .collect(Collectors.joining(separator));
    }
}
```

#### 日期工具类 (DateUtils)
```java
public class DateUtils {
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    public static LocalDateTime parse(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }
    
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
```

#### 集合工具类 (CollectionUtils)
```java
public class CollectionUtils {
    
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }
    
    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }
    
    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }
    
    public static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? emptyList() : list;
    }
    
    public static <T> boolean containsAny(Collection<T> source, Collection<T> target) {
        return source != null && target != null && 
               source.stream().anyMatch(target::contains);
    }
}
```

### 常量定义

#### 系统常量 (SystemConstants)
```java
public class SystemConstants {
    
    // 系统相关
    public static final String SYSTEM_NAME = "AI-Master";
    public static final String SYSTEM_VERSION = "1.0.0";
    
    // 时间相关
    public static final int DEFAULT_TIMEOUT_SECONDS = 30;
    public static final int DEFAULT_CACHE_MINUTES = 30;
    
    // 分页相关
    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    // 编码相关
    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";
    
    // 日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
}
```

#### 业务常量 (BusinessConstants)
```java
public class BusinessConstants {
    
    // 申诉相关
    public static final String APPEAL_PREFIX = "APL";
    public static final String ORDER_PREFIX = "ORD";
    
    // 状态相关
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_DELETED = "DELETED";
    
    // 缓存相关
    public static final String CACHE_KEY_APPEAL = "appeal:";
    public static final String CACHE_KEY_ORDER = "order:";
    public static final String CACHE_KEY_USER = "user:";
    
    // 文件相关
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {"jpg", "jpeg", "png", "pdf"};
}
```

### 验证工具类

#### 验证工具 (ValidationUtils)
```java
public class ValidationUtils {
    
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    
    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new BusinessException(ValidationErrorCode.VALIDATION_ERROR, message);
        }
    }
    
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");
    }
    
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    public static boolean isValidIdCard(String idCard) {
        if (isEmpty(idCard)) {
            return false;
        }
        return idCard.matches("^(\\d{15}|\\d{17}[\\dXx])$");
    }
}
```

### JSON工具类

#### JSON工具 (JsonUtils)
```java
public class JsonUtils {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }
    
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SystemException("JSON序列化失败", e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new SystemException("JSON反序列化失败", e);
        }
    }
    
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new SystemException("JSON反序列化失败", e);
        }
    }
    
    public static JsonNode parseJson(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            throw new SystemException("JSON解析失败", e);
        }
    }
}
```

### 加密工具类

#### 加密工具 (EncryptUtils)
```java
public class EncryptUtils {
    
    private static final String ALGORITHM = "AES";
    private static final String KEY = "AI-Master-2024-Key-32-Bytes-Length!!";
    
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new SystemException("加密失败", e);
        }
    }
    
    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SystemException("解密失败", e);
        }
    }
    
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException("MD5计算失败", e);
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
```

### Maven配置

#### 核心依赖
```xml
<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Jackson -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>

<!-- Jackson日期模块 -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>

<!-- Bean验证 -->
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
</dependency>

<!-- Apache Commons -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>

<!-- Guava -->
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.1-jre</version>
</dependency>
```

## 最佳实践

### 工具类使用规范
1. **线程安全**: 所有工具类必须是线程安全的
2. **不可实例化**: 工具类使用私有构造函数防止实例化
3. **异常处理**: 工具类方法必须妥善处理异常
4. **文档注释**: 所有公共方法必须有清晰的JavaDoc

### 常量管理规范
1. **分类管理**: 按业务领域分类管理常量
2. **命名规范**: 使用大写字母和下划线命名
3. **集中管理**: 避免在代码中硬编码字符串和数字
4. **版本兼容**: 常量变更需考虑向后兼容性

### 异常处理规范
1. **业务异常**: 使用BusinessException及其子类
2. **系统异常**: 使用SystemException及其子类
3. **异常转换**: 底层异常必须转换为业务异常
4. **错误日志**: 所有异常必须有完整的错误日志

## 测试策略

### 工具类测试
```java
class StringUtilsTest {
    
    @Test
    void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty("   "));
        assertFalse(StringUtils.isEmpty("a"));
        assertFalse(StringUtils.isEmpty(" abc "));
    }
    
    @Test
    void testJoin() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", StringUtils.join(list, ","));
        assertEquals("", StringUtils.join(Collections.emptyList(), ","));
        assertEquals("", StringUtils.join(null, ","));
    }
}
```

## 相关链接
- [所有模块都依赖此模块](../README.md)
- [基础类型定义](../ai-master-domain/README.md)
- [异常处理机制](../ai-master-service/README.md)