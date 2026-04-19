# Stage 1: Build 
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Cache dependencies before copying source (faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build JAR (skip tests — run them in CI)
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install wget for healthcheck
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Non-root user for security (required for Azure Container Apps)
RUN groupadd -r campusaura && useradd -r -g campusaura campusaura
USER campusaura:campusaura

# Copy JAR from build stage
COPY --from=build --chown=campusaura:campusaura /app/target/*.jar app.jar

# Port 8080 (default Spring Boot)
EXPOSE 8080

# JVM tuning for containers (prevents OOM in cloud environments)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Health check — needs wget (installed above)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
