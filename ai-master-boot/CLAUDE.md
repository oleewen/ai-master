# AI-Master Boot模块

[← 返回项目根目录](../CLAUDE.md)

## 模块定位
**层级**: 应用启动模块 (Boot Module)  
**职责**: Spring Boot应用启动、配置管理、环境配置  
**核心价值**: 提供应用启动入口，集中管理所有配置

## 模块结构

```
ai-master-boot/
├── pom.xml                          # Maven配置
├── src/
│   ├── main/
│   │   ├── java/com/ai/master/boot/
│   │   │   ├── AppealApplication.java    # 主启动类
│   │   │   └── ApplicationStarter.java   # 应用启动器
│   │   └── resources/
│   │       ├── application.yml          # 主配置文件
│   │       ├── application-dev.yml      # 开发环境配置
│   │       ├── application-prod.yml     # 生产环境配置
│   │       ├── application-test.yml     # 测试环境配置
│   │       └── logback-boot.xml         # 日志配置
│   └── test/                          # 启动测试
```

## 启动类

### 主应用类 (AppealApplication)
```java
@SpringBootApplication
@EnableDubbo
@MapperScan("com.ai.master.**.infrastructure.persistence.mapper")
@EnableTransactionManagement
public class AppealApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AppealApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("AI-Master 申诉平台启动成功！");
            System.out.println("访问地址: http://localhost:8080/swagger-ui.html");
        };
    }
}
```

### 应用启动器 (ApplicationStarter)
```java
@Component
public class ApplicationStarter {
    
    @Autowired
    private Environment environment;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = environment.getProperty("server.port");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                   AI-Master 申诉平台                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  应用名称: " + environment.getProperty("spring.application.name"));
        System.out.println("║  运行端口: " + port);
        System.out.println("║  环境配置: " + Arrays.toString(environment.getActiveProfiles()));
        System.out.println("║  Swagger文档: http://localhost:" + port + contextPath + "/swagger-ui.html");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}
```

## 配置管理

### 主配置文件 (application.yml)
```yaml
# 应用配置
spring:
  application:
    name: ai-master-appeal
  profiles:
    active: dev
  
# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /ai-master
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_master?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      max-lifetime: 1800000
      connection-timeout: 30000

# MyBatis配置
mybatis:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.ai.master.**.infrastructure.persistence.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    lazy-loading-enabled: true
    multiple-result-sets-enabled: true
    use-column-label: true
    use-generated-keys: true
    default-executor-type: reuse

# Dubbo配置
dubbo:
  application:
    name: ${spring.application.name}
    logger: slf4j
  protocol:
    name: dubbo
    port: 20880
    threads: 200
    threadpool: fixed
  registry:
    address: zookeeper://${ZK_HOST:localhost}:${ZK_PORT:2181}
    timeout: 5000
  provider:
    timeout: 5000
    retries: 0
    delay: -1
  scan:
    base-packages: com.ai.master

# Redis配置
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 1000ms

# 日志配置
logging:
  level:
    root: INFO
    com.ai.master: DEBUG
    org.mybatis: DEBUG
    com.zaxxer.hikari: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/ai-master.log
    max-size: 100MB
    max-history: 30
```

### 开发环境配置 (application-dev.yml)
```yaml
# 开发环境配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_master_dev?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: dev_user
    password: dev_password
  
  redis:
    host: localhost
    port: 6379
    database: 1

# 日志级别
logging:
  level:
    root: DEBUG
    com.ai.master: DEBUG
    org.springframework.web: DEBUG

# Swagger配置
springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
    path: /swagger-ui.html

# 开发工具
spring:
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true
```

### 生产环境配置 (application-prod.yml)
```yaml
# 生产环境配置
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/ai_master_prod?useSSL=true&serverTimezone=UTC&characterEncoding=utf8
    username: ${PROD_DB_USERNAME}
    password: ${PROD_DB_PASSWORD}
  
  redis:
    host: ${PROD_REDIS_HOST}
    port: ${PROD_REDIS_PORT}
    password: ${PROD_REDIS_PASSWORD}
    database: 0

# 服务器配置
server:
  port: 80
  
# 日志级别
logging:
  level:
    root: WARN
    com.ai.master: INFO
  file:
    name: /opt/logs/ai-master/ai-master.log

# 监控配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

## 日志配置

### logback-boot.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollPolicy>
    </appender>
    
    <!-- 错误日志 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}_error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}_error.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>5GB</totalSizeCap>
        </rollPolicy>
    </appender>
    
    <!-- 日志级别配置 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
    <!-- 特定包日志级别 -->
    <logger name="com.ai.master" level="DEBUG"/>
    <logger name="org.mybatis" level="DEBUG"/>
    <logger name="com.zaxxer.hikari" level="INFO"/>
    
</configuration>
```

## 启动脚本

### 启动脚本 (start.sh)
```bash
#!/bin/bash

# 设置JVM参数
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# 设置应用参数
APP_NAME="ai-master-appeal"
APP_PORT=8080

# 检查进程是否已存在
PID=$(lsof -t -i:${APP_PORT})
if [ -n "$PID" ]; then
    echo "应用已在运行，进程ID: $PID"
    exit 1
fi

# 启动应用
echo "正在启动 ${APP_NAME}..."
nohup java ${JAVA_OPTS} -jar ${APP_NAME}.jar > logs/app.out 2>&1 &
echo "应用启动完成，端口: ${APP_PORT}"
```

### 停止脚本 (stop.sh)
```bash
#!/bin/bash

APP_PORT=8080
PID=$(lsof -t -i:${APP_PORT})

if [ -n "$PID" ]; then
    echo "正在停止应用，进程ID: $PID"
    kill -15 $PID
    sleep 5
    
    # 强制停止
    if kill -0 $PID 2>/dev/null; then
        echo "强制停止应用..."
        kill -9 $PID
    fi
    
    echo "应用已停止"
else
    echo "应用未运行"
fi
```

### Docker配置

#### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

LABEL maintainer="AI-Master Team"
LABEL version="1.0.0"
LABEL description="AI-Master 申诉平台"

# 设置工作目录
WORKDIR /app

# 复制应用jar包
COPY target/ai-master-boot.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 暴露端口
EXPOSE 8080 20880

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  ai-master:
    build: .
    container_name: ai-master-appeal
    ports:
      - "8080:8080"
      - "20880:20880"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=prod_user
      - DB_PASSWORD=prod_password
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - ZK_HOST=zookeeper
      - ZK_PORT=2181
    depends_on:
      - mysql
      - redis
      - zookeeper
    volumes:
      - ./logs:/app/logs
    networks:
      - ai-master-network

  mysql:
    image: mysql:8.0
    container_name: ai-master-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: ai_master_prod
      MYSQL_USER: prod_user
      MYSQL_PASSWORD: prod_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - ai-master-network

  redis:
    image: redis:7-alpine
    container_name: ai-master-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - ai-master-network

  zookeeper:
    image: zookeeper:3.8
    container_name: ai-master-zk
    ports:
      - "2181:2181"
    environment:
      ZOO_MY_ID: 1
      ZOO_SERVERS: server.1=zookeeper:2888:3888;2181
    volumes:
      - zk_data:/data
      - zk_datalog:/datalog
    networks:
      - ai-master-network

volumes:
  mysql_data:
  redis_data:
  zk_data:
  zk_datalog:

networks:
  ai-master-network:
    driver: bridge
```

## 部署指南

### 本地开发部署
```bash
# 1. 启动依赖服务
# MySQL
mysql -u root -p < docs/sql/init.sql

# Redis
redis-server

# Zookeeper
zkServer start

# 2. 编译项目
mvn clean package -DskipTests

# 3. 启动应用
java -jar target/ai-master-boot.jar

# 4. 访问应用
# Swagger文档: http://localhost:8080/ai-master/swagger-ui.html
# 健康检查: http://localhost:8080/ai-master/actuator/health
```

### Docker部署
```bash
# 1. 构建镜像
docker build -t ai-master-appeal:latest .

# 2. 启动服务
docker-compose up -d

# 3. 查看日志
docker-compose logs -f ai-master

# 4. 停止服务
docker-compose down
```

### Kubernetes部署

#### deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-master-appeal
  labels:
    app: ai-master-appeal
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ai-master-appeal
  template:
    metadata:
      labels:
        app: ai-master-appeal
    spec:
      containers:
      - name: ai-master-appeal
        image: ai-master-appeal:latest
        ports:
        - containerPort: 8080
        - containerPort: 20880
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: ai-master-secret
              key: db-username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: ai-master-secret
              key: db-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

## 环境变量

### 必需环境变量
```bash
# 数据库配置
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_password

# Zookeeper配置
ZK_HOST=localhost
ZK_PORT=2181

# 生产环境额外配置
PROD_DB_USERNAME=prod_user
PROD_DB_PASSWORD=prod_password
PROD_REDIS_HOST=prod_redis
PROD_REDIS_PORT=6379
PROD_REDIS_PASSWORD=prod_password
```

### 可选环境变量
```bash
# JVM参数
JAVA_OPTS="-Xms512m -Xmx1024m"

# 应用参数
SERVER_PORT=8080
LOG_LEVEL=INFO
```

## 监控与运维

### 健康检查端点
```bash
# 健康检查
curl http://localhost:8080/ai-master/actuator/health

# 详细信息
curl http://localhost:8080/ai-master/actuator/info

# 指标监控
curl http://localhost:8080/ai-master/actuator/metrics
```

### 性能监控
```bash
# JVM监控
curl http://localhost:8080/ai-master/actuator/metrics/jvm.memory.used
curl http://localhost:8080/ai-master/actuator/metrics/jvm.gc.pause

# 数据库监控
curl http://localhost:8080/ai-master/actuator/metrics/hikaricp.connections.active
```

## 故障排查

### 常见问题

#### 端口冲突
```bash
# 检查端口占用
lsof -i:8080
lsof -i:20880
lsof -i:2181

# 终止占用进程
kill -9 [PID]
```

#### 数据库连接失败
```bash
# 检查MySQL服务
systemctl status mysql

# 测试连接
mysql -h localhost -u root -p
```

#### Redis连接失败
```bash
# 检查Redis服务
redis-cli ping

# 测试连接
redis-cli -h localhost -p 6379
```

### 日志分析
```bash
# 查看应用日志
tail -f logs/ai-master.log

# 查看错误日志
tail -f logs/ai-master_error.log

# 搜索特定错误
grep "ERROR" logs/ai-master.log
```

## 相关链接
- [项目架构](../README.md)
- [服务实现](../ai-master-service/README.md)
- [应用配置](../ai-master-application/README.md)