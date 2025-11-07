# Library Management System

傻呗期末作业

一个基于 Spring Boot 和 Vue 3 的图书馆管理系统，支持用户认证、图书管理、借阅记录等功能

## 技术栈

### 后端
- **Java 21** + **Spring Boot 3.5.7**
- **Spring Security** - 用户认证与授权
- **Spring Data JPA** - ORM 框架
- **MySQL** - 数据库
- **Redis** - 缓存和会话存储

### 前端
- **Vue 3** - UI 框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Axios** - HTTP 客户端

## 快速开始

### 前置要求
- Java 21+
- Node.js 22.12.0+
- MySQL 8.0+
- Redis

### 后端启动
```bash
./gradlew bootRun
```

### 前端启动
```bash
cd frontend
pnpm install
pnpm dev
```

## 项目结构

```
library-management-system/
├── src/                    # 后端源代码
├── frontend/              # 前端源代码
├── build.gradle           # Gradle 配置
└── compose.yaml           # Docker Compose 配置
```

## 许可证

MIT
