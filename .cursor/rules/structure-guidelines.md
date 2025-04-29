# 架构规范指南

## 1. 整体架构

### 1.1 架构层次
本项目采用DDD（领域驱动设计）架构，结合六边形架构（Hexagonal Architecture）原则，将系统分为以下标准层次：

#### 核心层（Core Layers）
- **Domain Layer**: 领域核心层，包含领域模型、领域服务和领域事件
- **Application Layer**: 应用层，协调领域对象，实现用例流程

#### 适配层（Adapter Layers）
- **Infrastructure Layer**: 基础设施层，实现技术细节和外部服务集成
- **Interface Layer (Service)**: 接口适配层，处理外部请求和响应

#### 通信层（Communication Layers）
- **API Layer**: 接口定义层，定义外部服务契约
- **Client Layer**: 客户端层，提供服务调用适配

#### 启动层（Bootstrap Layer）
- **Boot Layer**: 应用启动层，负责组装和配置

### 1.2 架构原则
- 领域驱动设计（DDD）原则
  * 领域模型是核心
  * 领域逻辑内聚
  * 限界上下文清晰
  
- 六边形架构原则
  * 内外边界分明
  * 依赖指向内部
  * 关注点分离
  * 可测试性设计

- 模块依赖原则
  * 显式依赖原则
  * 稳定依赖原则
  * 抽象依赖原则

## 2. 标准依赖关系

### 2.1 模块依赖图
```
cursor-master-boot
    ├── cursor-master-service
    └── cursor-master-infrastructure

cursor-master-service
    ├── cursor-master-application
    └── cursor-master-api

cursor-master-application
    └── cursor-master-domain

cursor-master-infrastructure
    └── cursor-master-domain

cursor-master-client
    └── cursor-master-api
```

### 2.2 依赖规则
1. Domain Layer（领域核心层）
   - 不依赖任何其他层
   - 包含领域模型和业务规则
   - 定义领域接口和事件

2. Application Layer（应用层）
   - 仅依赖Domain Layer
   - 实现应用用例和业务流程
   - 编排领域对象

3. Infrastructure Layer（基础设施层）
   - 依赖Domain Layer
   - 实现领域接口
   - 提供技术实现

4. Service Layer（接口适配层）
   - 依赖Application Layer和API Layer
   - 实现API契约
   - 处理请求响应

5. API Layer（接口定义层）
   - 不依赖其他层
   - 定义服务契约
   - 声明DTO对象

6. Client Layer（客户端层）
   - 仅依赖API Layer
   - 提供服务调用
   - 处理远程通信

7. Boot Layer（启动层）
   - 依赖Service Layer和Infrastructure Layer
   - 装配应用组件
   - 管理配置信息

### 2.3 架构边界
1. 内部边界（Inner Boundary）
   - Domain Layer作为最内层
   - 不依赖外部系统
   - 纯业务规则实现

2. 中间边界（Middle Boundary）
   - Application Layer作为协调层
   - Infrastructure Layer作为适配层
   - 连接内外部系统

3. 外部边界（Outer Boundary）
   - Service Layer作为外部入口
   - API Layer定义外部契约
   - Client Layer适配外部调用

## 3. 模块划分

### 3.1 核心模块
- **cursor-master-domain**
  - 领域模型
  - 领域服务
  - 领域事件
  - 仓储接口
  - 领域规则

### 3.2 应用模块
- **cursor-master-application**
  - 应用服务
  - 命令处理
  - 查询处理
  - 事件处理
  - 事务管理

### 3.3 基础设施模块
- **cursor-master-infrastructure**
  - 数据库访问实现
  - 缓存实现
  - 消息队列实现
  - 第三方服务集成
  - 技术组件

### 3.4 接口模块
- **cursor-master-api**
  - 接口定义
  - DTO对象
  - 接口文档
  - 版本控制

### 3.5 启动模块
- **cursor-master-boot**
  - 应用配置
  - 依赖注入
  - 启动类
  - 环境配置

### 3.6 客户端模块
- **cursor-master-client**
  - RPC客户端
  - HTTP客户端
  - 客户端配置
  - 服务发现

### 3.7 公共模块
- **cursor-master-common**
  - 工具类
  - 通用组件
  - 常量定义
  - 共享模型

### 3.8 服务模块
- **cursor-master-service**
  - 服务实现
  - 控制器
  - 服务配置
  - 服务注册

## 4. 设计规范

### 4.1 领域模型规范
- 实体（Entity）
  - 继承领域基类
  - 包含唯一标识
  - 状态封装
  - 行为驱动

- 值对象（Value Object）
  - 不可变性
  - 完整验证
  - 无副作用
  - 领域语义

- 聚合（Aggregate）
  - 一致性边界
  - 事务边界
  - 并发控制
  - 引用管理

### 4.2 应用服务规范
- 用例实现
  - 事务控制
  - 权限校验
  - 参数验证
  - 结果转换

- 命令处理
  - 意图表达
  - 单一职责
  - 幂等性
  - 可追踪

- 查询处理
  - 性能优化
  - 结果缓存
  - 分页支持
  - 条件过滤

### 4.3 基础设施规范
- 资源访问
  - 连接池管理
  - 事务传播
  - 异常处理
  - 性能监控

- 缓存策略
  - 多级缓存
  - 一致性维护
  - 过期策略
  - 容量控制

- 消息处理
  - 可靠投递
  - 顺序保证
  - 重复消费
  - 死信处理

## 5. 通信规范

### 5.1 同步调用
- 服务定义
  - 接口语义
  - 参数约束
  - 返回值规范
  - 异常处理

- 调用方式
  - 超时控制
  - 重试策略
  - 熔断降级
  - 负载均衡

### 5.2 异步消息
- 消息定义
  - 消息结构
  - 版本控制
  - 兼容策略
  - 序列化方式

- 消息处理
  - 顺序保证
  - 幂等处理
  - 事务一致性
  - 失败恢复

### 5.3 事件驱动
- 事件定义
  - 事件类型
  - 事件属性
  - 事件版本
  - 事件路由

- 事件处理
  - 异步处理
  - 顺序保证
  - 错误处理
  - 监控追踪

## 6. 部署架构

### 6.1 环境定义
- 开发环境
  - 本地开发
  - 单机部署
  - 快速迭代

- 测试环境
  - 集成测试
  - 性能测试
  - 稳定性测试

- 生产环境
  - 高可用部署
  - 容灾备份
  - 监控告警

### 6.2 部署策略
- 容器化部署
  - 镜像管理
  - 资源配置
  - 网络规划
  - 存储方案

- 服务编排
  - 服务发现
  - 负载均衡
  - 配置管理
  - 日志收集

- 监控运维
  - 性能监控
  - 链路追踪
  - 告警管理
  - 运维工具 