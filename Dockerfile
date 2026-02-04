# Multi-stage Dockerfile for CampusAura Backend
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy Firebase service account file
COPY --chown=spring:spring src/main/resources/firebase-service-account.json /app/firebase-service-account.json

# Expose port 8080
EXPOSE 8080

# Environment variables (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=prod
ENV FIREBASE_SERVICE_ACCOUNT_KEY=/app/firebase-service-account.json

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
