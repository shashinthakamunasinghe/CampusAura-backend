<h1 align="center">CampusAura Backend API</h1>

<div align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Firebase-Admin-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/Stripe-Payment-008CDD?style=for-the-badge&logo=stripe&logoColor=white" alt="Stripe" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Azure-Container_Apps-0089D6?style=for-the-badge&logo=microsoft-azure&logoColor=white" alt="Azure" />
</div>

## 📌 Project Overview

CampusAura is an enterprise-grade campus community platform designed to streamline event discovery, secure ticketing, fundraising, and a student-exclusive marketplace.

This repository hosts the **Spring Boot Microservice/Backend API**, providing a robust, highly available, and secure foundation for the platform. Built with a focus on modern software engineering principles, the system leverages containerization, cloud-native deployments, and strict role-based access control (RBAC).

🔗 **Live Frontend**: [campus-aura-frontend.vercel.app](https://campus-aura-frontend.vercel.app)  
🔗 **Production API Endpoint**: [campusaura-backend.lemontree-0868690c.centralindia.azurecontainerapps.io](https://campusaura-backend.lemontree-0868690c.centralindia.azurecontainerapps.io)  

---

## 🏗️ Architecture & Engineering Practices

As a showcase of professional software engineering, this project implements the following practices:

- **Layered Architecture (N-Tier)**: Strict separation of concerns across Controllers, Services, Repositories, and Security layers to ensure maintainability, scalability, and testability.
- **RESTful API Design**: Predictable resource-oriented URIs, standardized HTTP methods, and unified JSON error handling with proper HTTP status codes.
- **Stateless Authentication**: Leveraging Firebase JWTs for stateless, highly scalable API security without session overhead.
- **Containerization**: Fully Dockerized environment ensuring parity across local development, CI/CD, and production.
- **Cloud-Native Deployment**: Hosted on Microsoft Azure Container Apps for automatic scaling, load balancing, and high availability.
- **Secure Secrets Management**: Environment variables and secure volume-mounted secrets are utilized instead of hardcoded credentials.

---

## 🚀 Key Features

### Security & Identity Management
- **Firebase Auth Integration**: Intercepts and validates JWTs via custom Spring Security filters.
- **Dynamic Role-Based Access Control (RBAC)**: Supports `ADMIN`, `COORDINATOR`, `STUDENT`, and `EXTERNAL_USER` roles.
- **Smart Domain Verification**: Automatically assigns roles based on the authenticated user's email domain (e.g., auto-promoting `@std.uwu.ac.lk` to `STUDENT`).

### Core Business Domains
- **Event & Ticketing Engine**: Securely manages event lifecycles. Integrated with **Stripe** for seamless ticket purchasing and payment workflows.
- **Student Marketplace**: A peer-to-peer ecosystem with restricted access logic (only verified students can create listings).
- **Fundraising Module**: Handles donation tracking and campaign management.

### Cloud & Database
- **Firestore Database**: NoSQL document store for highly flexible and fast read/write operations.
- **File & Image Uploads**: Secure handling of multipart file uploads for event banners and marketplace product images.

---

## 🐳 Docker & Containerization

The project is fully containerized, making it resilient, platform-agnostic, and easy to deploy.

- **Dockerfile**: Optimized multi-stage build using a robust Linux base image (`Ubuntu Jammy`) to ensure native library compatibility (specifically resolving SIGSEGV issues with gRPC/Netty dependencies required by Firebase).
- **Docker Compose**: Orchestrates local development, handling environment variable injection and secure credential mounting.

```bash
# Build the production-ready image locally
docker build -t campusaura-backend .

# Run the full stack locally with Docker Compose
docker-compose up -d --build
```

---

## ☁️ Deployment Pipeline (Azure)

The backend is configured for continuous delivery and is currently deployed to **Microsoft Azure Container Apps** directly from the Azure Container Registry. 

**Deployment Highlights:**
1. **Continuous Integration**: Automated via GitHub Actions (`.github/workflows/deploy-backend.yml`).
2. **Serverless Infrastructure**: Utilizes Azure Container Apps to minimize operational overhead while maximizing scalability to meet student traffic spikes.
3. **Health Probes**: Integrated with Spring Boot Actuator to expose `/actuator/health` endpoints, allowing Azure to route traffic only to healthy container instances and gracefully manage container restarts.

---

## 💻 Tech Stack Summary

| Category | Technologies |
|---|---|
| **Language & Framework** | Java 17, Spring Boot 3.x, Spring Security |
| **Authentication & DB** | Firebase Authentication, Google Cloud Firestore |
| **Payments** | Stripe API |
| **Build & Tooling** | Maven, Git, GitHub Actions |
| **DevOps & Cloud** | Docker, Docker Compose, Azure Container Apps, Azure Container Registry (ACR) |

---

## 🛠️ Local Development Guide

### Prerequisites
- Java 17+ & Maven 3.6+
- Docker & Docker Desktop (Optional but recommended)
- Firebase Service Account Key (`firebase-service-account.json`)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/sandaluruba/CampusAura-backend.git
   cd CampusAura-backend
   ```

2. **Configure Secrets**
   Place your `firebase-service-account.json` into the `src/main/resources/` directory.

3. **Run via Maven**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Run via Docker**
   Ensure the `docker-compose.yml` points to the correct secret path, then execute:
   ```bash
   docker-compose up -d
   ```

The server will initialize and bind to `http://localhost:8080`.

---
<div align="center">
  <i>Developed to demonstrate robust, scalable, and modern Software Engineering principles.</i>
</div>
