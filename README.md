# lyc-mall-service

基于 Spring Cloud 微服务架构的高性能秒杀商城系统，提供完整的秒杀业务流程，包含认证、商品管理、秒杀服务、订单处理等核心模块。

## 技术栈

- **Java**: JDK 8+
- **框架**: Spring Boot 2.7.18 + Spring Cloud 2021.0.9
- **服务注册与发现**: Nacos
- **网关**: Spring Cloud Gateway
- **数据库**: MySQL 5.7+
- **缓存**: Redis 6.0+
- **消息队列**: RabbitMQ
- **ORM**: MyBatis Plus
- **分布式锁**: Redisson
- **对象存储**: 阿里云 OSS

## 项目结构

```
lyc-mall-service/
├── mall-auth/              # 认证服务
│   ├── mall-auth-sdk/      # 认证SDK
│   └── mall-auth-service/  # 认证服务实现
├── mall-common/            # 公共模块（工具类、DTO、异常处理）
├── mall-gateway/           # 网关服务
├── mall-product/           # 商品服务（商品管理、OSS上传）
├── mall-secKill/           # 秒杀服务（核心秒杀逻辑）
└── mall-secKill-order/     # 秒杀订单服务（订单处理、支付）
```

## 核心功能

| 模块 | 功能 | 端口 |
|------|------|------|
| mall-auth | 用户认证、JWT生成与验证 | 8082 |
| mall-gateway | 请求路由、限流熔断 | 8080 |
| mall-product | 商品CRUD、秒杀商品管理、图片上传 | 8020 |
| mall-secKill | 秒杀核心逻辑（Redis+Lua）、库存扣减 | 8080 |
| mall-secKill-order | 订单创建、支付处理、订单查询 | - |

## 秒杀架构设计

### 核心流程

1. **预热阶段**: 将秒杀商品库存加载到 Redis
2. **请求拦截**: Gateway 限流 + 重复请求过滤
3. **库存校验**: Redis Lua 脚本原子扣减库存
4. **异步下单**: RabbitMQ 消息队列异步创建订单
5. **订单处理**: 消费者监听消息，完成订单持久化

### 技术亮点

- **Redis Lua 脚本**: 保证库存扣减的原子性，防止超卖
- **Redisson 分布式锁**: 防止重复下单
- **RabbitMQ 削峰填谷**: 异步处理订单，提高系统吞吐量
- **Nacos 服务发现**: 动态感知服务实例

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- RabbitMQ 3.8+
- Nacos 2.0+

### 配置说明

1. **Nacos 配置**
   - 启动 Nacos Server: `startup.cmd -m standalone`
   - 默认地址: `http://192.168.40.128:8848/nacos`

2. **数据库配置**
   - 创建数据库: `lyc_mall`、`lyc_mall_auth`
   - 修改各模块 `application.yml` 中的数据库连接信息

3. **Redis 配置**
   - 修改各模块 `application.yml` 中的 Redis 连接信息

4. **RabbitMQ 配置**
   - 修改 `mall-secKill` 和 `mall-secKill-order` 的 RabbitMQ 连接信息

### 启动顺序

```bash
# 1. 启动 Nacos
startup.cmd -m standalone

# 2. 启动 Redis
redis-server

# 3. 启动 RabbitMQ
rabbitmq-server

# 4. 启动各微服务（按顺序）
mvn spring-boot:run -pl mall-gateway
mvn spring-boot:run -pl mall-auth/mall-auth-service
mvn spring-boot:run -pl mall-product
mvn spring-boot:run -pl mall-secKill
mvn spring-boot:run -pl mall-secKill-order
```

## API 接口示例

### 认证接口

```bash
# 用户登录
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

### 秒杀接口

```bash
# 参与秒杀
POST /secKill/seckill/{productId}
Authorization: Bearer {token}

{
  "userId": 1,
  "addressId": 1
}
```

### 商品接口

```bash
# 获取秒杀商品列表
GET /product/seckill/list

# 添加秒杀商品
POST /product/seckill/add
Authorization: Bearer {token}

{
  "productName": "秒杀商品",
  "price": 99.00,
  "seckillPrice": 9.90,
  "stock": 100,
  "startTime": "2024-01-01 10:00:00",
  "endTime": "2024-01-01 12:00:00"
}
```

## 项目特性

- ✅ **高并发支持**: Redis + Lua 保证秒杀的高性能和数据一致性
- ✅ **分布式架构**: 基于 Spring Cloud 的微服务设计
- ✅ **服务治理**: Nacos 服务注册与发现
- ✅ **异步处理**: RabbitMQ 消息队列解耦订单创建
- ✅ **异常处理**: 统一异常处理和全局异常捕获
- ✅ **代码规范**: 清晰的分层架构和代码结构

## 目录结构说明

```
mall-common/
├── dto/          # 数据传输对象
├── enums/        # 枚举定义
├── exception/    # 异常处理
├── utils/        # 工具类
└── vo/           # 视图对象

mall-secKill/
├── config/       # 配置类（RabbitMQ、Redisson）
├── controller/   # REST API 控制层
├── service/      # 业务逻辑层
├── utils/        # 工具类（MD5、Lua脚本）
└── vo/           # 请求/响应对象
```

## License

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！