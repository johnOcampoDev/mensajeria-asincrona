# =========================
# Stage 1: Build
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/message-processor-service-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

EXPOSE 8081

#ENTRYPOINT ["java","-jar","app.jar"]
ENTRYPOINT ["/wait-for-it.sh", "mongodb:27017", "--timeout=60", "--strict", "--", "/wait-for-it.sh", "rabbitmq:5672", "--timeout=60", "--strict", "--", "java", "-jar", "/app/app.jar"]
