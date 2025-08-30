# AI-Master项目

## 项目愿景
AI-Master是一个基于DDD六边形架构的申诉业务处理平台，提供完整的申诉生命周期管理，包括申诉创建、验证、提交、审核、结果查询等功能，支持订单相关业务的申诉处理。

## 核心业务领域

### 订单域 (Order Domain)
- **聚合根**: Order - 订单聚合，管理订单生命周期
- **核心能力**: 订单创建、状态流转、金额计算
- **状态机**: CREATED → SUBMITTED → (AUDITING) → COMPLETED/CANCELLED

### 申诉域 (Appeal Domain) - 新增
- **聚合根**: Appeal - 申诉单聚合，管理申诉完整生命周期
- **核心能力**: 申诉创建、验证、提交、审核、结果计算
- **状态机**: CREATED → VERIFIED → SUBMITTED → AUDITING → (ALL_PASSED/PARTIAL_PASSED/ALL_REJECTED/CANCELLED)
- **申诉项**: AppealItem - 细粒度的单个申诉事项

### 库存域 (Inventory Domain)
- **聚合根**: Inventory - 库存聚合
- **核心能力**: 库存锁定、释放、扣减

### 商品域 (Goods Domain)
- **聚合根**: Goods - 商品聚合
- **核心能力**: 商品价格计算、信息查询


## 运行与开发

### 环境要求
- Java 8+ (建议升级到17)
- Maven 3.6+
- MySQL 8.0
- Zookeeper 3.6+

### 启动步骤
```bash
# 1. 启动Zookeeper
zkServer start

# 2. 启动MySQL并创建数据库
mysql -u root -p
CREATE DATABASE ai_master DEFAULT CHARACTER SET utf8mb4;

# 3. 启动应用
mvn clean package -DskipTests
java -jar ai-master-boot/target/ai-master-boot.jar
```

### 本地开发
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn clean package

# 生成MyBatis代码
mvn mybatis-generator:generate -pl ai-master-infrastructure
```


## 变更记录 (Changelog)

### 2025-08-28 - 初始化架构分析
- **新增**: 项目整体架构分析与文档化
- **新增**: 申诉业务领域模型分析
- **新增**: 模块依赖关系图
- **更新**: 技术栈与运行环境文档
- **发现**: 
  - Java版本需要升级 (当前8，规范要求17+)
  - 申诉功能已完成核心领域模型设计
  - 测试覆盖率良好 (>90%)

## 下一步建议
1. **技术升级**: 将Java版本从8升级到17
2. **模块文档**: 为每个模块创建详细的CLAUDE.md文档
3. **API文档**: 完善Swagger API文档
4. **部署配置**: 添加Docker部署支持
5. **监控集成**: 集成Actuator和Micrometer监控