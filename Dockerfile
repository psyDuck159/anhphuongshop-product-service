# =====================================
# Stage 1: Build
# =====================================
FROM maven:3.9-eclipse-temurin-25-alpine AS build

WORKDIR /app

ARG NEXUS_USER
ARG NEXUS_PASS

# Copy Maven wrapper và pom.xml để cache dependencies
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY settings.xml /root/.m2/settings.xml

ENV NEXUS_USER=${NEXUS_USER}
ENV NEXUS_PASS=${NEXUS_PASS}

# Download dependencies (sẽ được cache nếu pom.xml không đổi)
RUN mvn dependency:go-offline -B -s /root/.m2/settings.xml

# Copy source code
COPY src ./src

# Build application (skip tests để build nhanh hơn)
RUN mvn clean package -DskipTests -B -s /root/.m2/settings.xml

# =====================================
# Stage 2: Runtime
# =====================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Tạo user non-root để chạy app (security best practice)
RUN mkdir -p /config &&\
    addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    chown -R appuser:appgroup /app /config

# Copy jar file từ build stage
COPY --from=build /app/target/*.jar app.jar

# Chuyển sang user non-root
USER appuser

# Expose ports
# 8084: HTTP REST API
# 9090: gRPC server
EXPOSE 8084 9090

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8084/actuator/health || exit 1

# JVM options để optimize container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
