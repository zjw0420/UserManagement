# ====== 阶段 1：Maven 构建 ======
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY backend/pom.xml .
COPY backend/model/pom.xml model/
COPY backend/common/pom.xml common/
COPY backend/service/pom.xml service/
COPY backend/web/pom.xml web/
COPY backend/web/web-admin/pom.xml web/web-admin/
RUN mvn dependency:go-offline -pl web/web-admin -am -B || true
COPY backend/ .
RUN mvn clean package -pl web/web-admin -am -DskipTests -B

# ====== 阶段 2：运行 ======
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/web/web-admin/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
