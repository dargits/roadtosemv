FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests


# Run stage
# Sửa lỗi image "openjdk:17-jdk-slim: not found" bằng eclipse-temurin
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Đồng bộ tên file là shorturl.war cho cả lệnh COPY và ENTRYPOINT
COPY --from=build /app/target/shorturl-0.0.1-SNAPSHOT.war shorturl.war
EXPOSE 8080

ENTRYPOINT ["java","-jar","shorturl.war"]