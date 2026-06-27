# ====== 阶段 1：Maven 构建 ======
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY model/pom.xml model/
COPY common/pom.xml common/
COPY service/pom.xml service/
COPY web/pom.xml web/
COPY web/web-admin/pom.xml web/web-admin/
# 先下载依赖（利用 Docker 缓存）
RUN mvn dependency:go-offline -pl web/web-admin -am -B || true
COPY . .
RUN mvn clean package -pl web/web-admin -am -DskipTests -B

# ====== 阶段 2：运行 ======
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/web/web-admin/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
