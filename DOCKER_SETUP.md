# CampusAura Backend - Docker Setup

## 🐳 Docker Files Created

1. **Dockerfile** - Multi-stage build for optimized image size
2. **.dockerignore** - Excludes unnecessary files from Docker context
3. **docker-compose.yml** - Easy orchestration and configuration

---

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the container
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the container
docker-compose down
```

### Option 2: Using Docker Commands

```bash
# Build the image
docker build -t campusaura-backend:latest .

# Run the container
docker run -d \
  -p 8080:8080 \
  --name campusaura-backend \
  campusaura-backend:latest

# View logs
docker logs -f campusaura-backend

# Stop the container
docker stop campusaura-backend
docker rm campusaura-backend
```

---

## 📋 Dockerfile Features

### Multi-Stage Build
- **Stage 1 (Build)**: Uses Maven to compile and package the application
- **Stage 2 (Runtime)**: Uses lightweight JRE Alpine image (smaller size)

### Security
- Runs as non-root user (`spring:spring`)
- Minimal attack surface with Alpine Linux

### Optimization
- Dependencies cached separately for faster rebuilds
- Only includes necessary runtime files
- Health check endpoint configured

### Image Size
- Build stage: ~500MB (Maven + JDK)
- Final image: ~200MB (JRE + application)

---

## 🔧 Configuration

### Environment Variables

You can override these in `docker-compose.yml` or pass them with `-e`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - SERVER_PORT=8080
  - FIREBASE_SERVICE_ACCOUNT_KEY=/app/firebase-service-account.json
  - FIREBASE_DATABASE_URL=https://campusaura-12c16.firebaseio.com
  - ALLOWED_ORIGINS=http://localhost:5173
```

### Ports

- **8080** - Main application port

### Volumes (Optional)

```yaml
volumes:
  # Mount external firebase credentials
  - ./firebase-service-account.json:/app/firebase-service-account.json:ro
  
  # Persist application logs
  - ./logs:/app/logs
```

---

## 🏗️ Build Process

The Dockerfile uses a **multi-stage build**:

1. **Build Stage**:
   - Uses `maven:3.9.6-eclipse-temurin-17`
   - Downloads dependencies
   - Compiles and packages the application
   - Skips tests for faster builds

2. **Runtime Stage**:
   - Uses `eclipse-temurin:17-jre-alpine`
   - Copies only the JAR file
   - Includes Firebase service account
   - Sets up non-root user

---

## 📊 Health Check

The container includes a health check that:
- Runs every 30 seconds
- Times out after 3 seconds
- Starts checking after 40 seconds (allows app to start)
- Retries 3 times before marking unhealthy

Check health status:
```bash
docker ps
# or
docker inspect --format='{{.State.Health.Status}}' campusaura-backend
```

---

## 🔍 Troubleshooting

### View logs
```bash
docker-compose logs -f campusaura-backend
# or
docker logs -f campusaura-backend
```

### Access container shell
```bash
docker exec -it campusaura-backend sh
```

### Rebuild after changes
```bash
# Force rebuild
docker-compose up -d --build

# or with Docker
docker build --no-cache -t campusaura-backend:latest .
```

### Check if app is running
```bash
curl http://localhost:8080/actuator/health
```

---

## 🌐 Production Deployment

### Environment-Specific Configuration

Create different compose files for environments:

**docker-compose.prod.yml**:
```yaml
version: '3.8'

services:
  campusaura-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: campusaura-backend-prod
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - ALLOWED_ORIGINS=https://campusaura.com
    restart: always
    networks:
      - prod-network

networks:
  prod-network:
    driver: bridge
```

Run with:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Using Secrets (Recommended for Production)

Instead of embedding `firebase-service-account.json` in the image:

1. **Mount as volume**:
```yaml
volumes:
  - ./secrets/firebase-service-account.json:/app/firebase-service-account.json:ro
```

2. **Use Docker secrets** (Swarm mode):
```yaml
secrets:
  - firebase_credentials

services:
  campusaura-backend:
    secrets:
      - firebase_credentials
```

---

## 📦 Image Management

### Tag and push to registry

```bash
# Tag the image
docker tag campusaura-backend:latest your-registry/campusaura-backend:1.0.0

# Push to Docker Hub
docker push your-registry/campusaura-backend:1.0.0

# Push to Azure Container Registry
az acr login --name yourregistry
docker push yourregistry.azurecr.io/campusaura-backend:1.0.0
```

---

## 🔐 Security Best Practices

✅ **Implemented**:
- Non-root user
- Minimal base image (Alpine)
- .dockerignore to exclude sensitive files
- Health checks

📝 **Additional Recommendations**:
- Use Docker secrets for sensitive data
- Scan images for vulnerabilities: `docker scan campusaura-backend`
- Keep base images updated
- Use specific version tags, not `latest`

---

## 🎯 Next Steps

1. **Test locally**: `docker-compose up`
2. **Verify health**: `curl http://localhost:8080/actuator/health`
3. **Test API endpoints**: `curl http://localhost:8080/api/auth/registration-info`
4. **Deploy to cloud**: Push to container registry
5. **Set up CI/CD**: Automate builds and deployments

---

## 📚 Useful Commands

```bash
# Build
docker-compose build

# Start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Remove volumes too
docker-compose down -v

# Restart specific service
docker-compose restart campusaura-backend

# View running containers
docker ps

# View all containers (including stopped)
docker ps -a

# Remove unused images
docker image prune -a

# View image size
docker images campusaura-backend
```

---

Your backend is now Docker-ready! 🎉
