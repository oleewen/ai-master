# 项目架构说明

## 1. 整体架构

本项目采用DDD（领域驱动设计）架构，遵循六边形架构（Hexagonal Architecture）原则，将系统分为以下几个主要层次：

### 1.1 核心层
- **Domain Layer**: 包含核心业务逻辑和领域模型
- **Application Layer**: 协调领域对象完成用户用例

### 1.2 适配层
- **Infrastructure Layer**: 实现技术细节和外部服务集成
- **Interface Layer**: 处理外部请求和响应

### 1.3 通信层
- **API Layer**: 定义外部服务接口
- **Client Layer**: 提供服务调用客户端

## 2. 模块说明

### 2.1 cursor-master-api
- 位置：`cursor-master-api/`
- 职责：定义系统对外提供的API接口
- 主要内容：
  - DTO对象定义
  - 接口请求/响应对象
  - API版本控制

### 2.2 cursor-master-application
- 位置：`cursor-master-application/`
- 职责：实现应用层业务逻辑
- 主要内容：
  - 应用服务
  - 命令处理
  - 查询处理
  - 事件处理

### 2.3 cursor-master-domain
- 位置：`cursor-master-domain/`
- 职责：实现核心领域逻辑
- 主要内容：
  - 领域模型
  - 领域服务
  - 领域事件
  - 仓储接口

### 2.4 cursor-master-infrastructure
- 位置：`cursor-master-infrastructure/`
- 职责：提供技术实现和外部服务集成
- 主要内容：
  - 数据库访问实现
  - 缓存实现
  - 消息队列实现
  - 第三方服务集成

### 2.5 cursor-master-boot
- 位置：`cursor-master-boot/`
- 职责：提供应用程序启动和配置
- 主要内容：
  - 应用配置
  - 依赖注入配置
  - 启动类

### 2.6 cursor-master-client
- 位置：`cursor-master-client/`
- 职责：提供服务调用客户端
- 主要内容：
  - RPC客户端
  - HTTP客户端
  - 客户端配置

### 2.7 cursor-master-common
- 位置：`cursor-master-common/`
- 职责：提供公共工具和组件
- 主要内容：
  - 工具类
  - 通用组件
  - 常量定义

### 2.8 cursor-master-service
- 位置：`cursor-master-service/`
- 职责：提供对外服务实现
- 主要内容：
  - Controller实现
  - RPC服务实现
  - 服务配置

## 3. 依赖关系

### 3.1 模块依赖
```
cursor-master-service
    ├── cursor-master-application
    ├── cursor-master-infrastructure
    └── cursor-master-common

cursor-master-application
    ├── cursor-master-domain
    └── cursor-master-common

cursor-master-infrastructure
    ├── cursor-master-domain
    └── cursor-master-common

cursor-master-client
    ├── cursor-master-api
    └── cursor-master-common

cursor-master-boot
    └── cursor-master-service
```

### 3.2 依赖原则
1. 领域层（Domain）不依赖其他任何层
2. 应用层（Application）只依赖领域层
3. 基础设施层（Infrastructure）实现领域层接口
4. 接口层（Service）依赖应用层，不直接依赖领域层

## 4. 设计原则

### 4.1 DDD原则
- 领域模型驱动设计
- 界限上下文清晰划分
- 领域事件驱动
- 聚合根管理实体生命周期

### 4.2 六边形架构原则
- 关注点分离
- 依赖倒置
- 端口和适配器模式
- 可测试性设计

### 4.3 CQRS原则
- 命令和查询职责分离
- 读写分离
- 事件溯源

## 5. 部署架构

### 5.1 开发环境
- 独立部署
- H2内存数据库
- 本地缓存

### 5.2 测试环境
- 集群部署
- MySQL数据库
- Redis缓存
- RocketMQ消息队列

### 5.3 生产环境
- 多区域部署
- 数据库主从分离
- 分布式缓存
- 高可用消息队列

## 6. 技术栈

### 6.1 基础框架
- Spring Boot
- Spring Cloud
- MyBatis
- Maven

### 6.2 存储
- MySQL
- Redis
- Elasticsearch

### 6.3 消息队列
- RocketMQ

### 6.4 监控告警
- Prometheus
- Grafana
- SkyWalking

### 6.5 其他
- Nacos：注册中心和配置中心
- Sentinel：限流熔断
- Seata：分布式事务 