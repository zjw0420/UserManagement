# 🏘️ UserManagement — 物业管理系统

Spring Boot 3 + React 19 全栈项目，29 张表的 RBAC 后台管理系统。

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.5-brightgreen)
![React](https://img.shields.io/badge/React-19-61dafb)

---

## 功能

| 模块 | 功能 |
|------|------|
| 🔐 认证 | 图形验证码、JWT 登录、Token 版本号踢人、三层限流 |
| 👤 用户管理 | 分页搜索、角色筛选、CRUD、强制踢人 |
| 🏢 公寓管理 | 省市区三级联动、设施/标签勾选、图片上传 |
| 🚪 房间管理 | CRUD、关联公寓 |
| 📋 岗位管理 | CRUD |
| 📝 租赁合同 | CRUD、7 种状态流转 |
| 📅 预约看房 | 列表、状态变更 |
| 👥 租客管理 | 分页搜索 |
| 📖 字典管理 | 设施/标签/租期/支付/属性/杂费 — 6 组基础数据 |
| 🌗 主题切换 | 暗黑科技风 / 简约风，一键切换 |

## 技术栈

| 层 | 技术 |
|------|------|
| 后端 | Spring Boot 3.0.5、MyBatis Plus 3.5.3.1、MySQL 8、Redis、MinIO、Knife4j |
| 前端 | React 19、TypeScript、Vite 8、React Router 7、Axios、纯 CSS |
| 部署 | Docker + Docker Compose |
| 安全 | BCrypt、JWT（jjwt）、验证码、三重日志脱敏 |

## 快速开始

### Docker（推荐）

```bash
git clone <repo-url>
cd UserManagement_Spring
docker compose up -d
# 前端 → http://localhost
# 后端 → http://localhost:8080
# 文档 → http://localhost:8080/doc.html
```

### 手动启动

需求：Java 17、Node 22+、MySQL 8、Redis

```bash
# 1. 数据库
mysql -u root -p -e "CREATE DATABASE user_management"
mysql -u root -p user_management < sql/init.sql

# 2. 后端
cd UserManagement_Spring
mvn clean package -DskipTests
java -jar web/web-admin/target/*.jar

# 3. 前端
cd UserManagement_Frontend
npm install
npm run dev
```

**默认账号**：`admin` / `admin123`

## 项目结构

```
├── backend/                     # 后端（Spring Boot 多模块）
│   ├── model/                   # 实体 + 枚举
│   ├── common/                  # 配置 + 异常 + 结果封装
│   ├── service/                 # Mapper + Service
│   ├── web/web-admin/           # Controller + 安全 + VO
│   ├── sql/init.sql             # 29 张表
│   └── Dockerfile
│
├── frontend/                    # 前端（React + TypeScript）
│   ├── src/api/                 # API 封装
│   ├── src/pages/               # 页面组件
│   ├── src/components/          # 通用组件
│   └── Dockerfile
│
├── docker-compose.yml           # 一键启动 6 个服务
└── README.md
```

## 设计亮点

- **多模块分层**：model → common → service → web-admin，依赖单向
- **Token 版本号**：Redis 整数 INCR，O(1) 踢人
- **三层日志脱敏**：Jackson → AOP → Logback 正则兜底
- **业务码检测**：后端 HTTP 200 带错误码，前端统一拦截
- **CSS 变量主题**：`[data-theme]` 切换全局配色

## License

MIT
