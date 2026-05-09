# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B


# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user
RUN groupadd -r campusaura && useradd -r -g campusaura campusaura

# Copy jar
COPY --from=build --chown=campusaura:campusaura /app/target/*.jar app.jar

USER campusaura:campusaura

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Optional: remove HEALTHCHECK entirely for Docker Compose
# Azure / Render / Railway can do health checks externally

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]