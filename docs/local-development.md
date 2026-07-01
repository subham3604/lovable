# Local Development Setup

This guide walks you through setting up and running the Genesis backend on your local machine for development.

> [!NOTE]  
> **Kubernetes Dependency**: Core features (user authentication, workspace management, file storage, and AI chat generation) can be run and developed entirely locally without a Kubernetes cluster. However, provisioning live preview environments (`POST /api/projects/{id}/deploy`) requires an active connection to a Kubernetes cluster via your local `kubeconfig`.

> [!NOTE]
> **Project Naming**: The repository, folder names, and codebase packages still use the legacy name `lovable` or `lovableClone`. The project's public branding is Genesis.

---

## 1. Prerequisites

Ensure you have the following installed locally:
- **Java 25** (OpenJDK / GraalVM)
- **Maven 3.9+**
- **Docker Desktop** (to run local databases and caches)
- **Node.js 20+** (if running the companion frontend locally)
- **OpenAI API Key**: Required for AI chat features.
- **Stripe Test Credentials**: Required for billing workflows.

---

## 2. Clone the Repositories

For the full-system experience, clone both the backend and frontend repositories:

**Backend (This Repository):**
```bash
git clone https://github.com/subham3604/lovable.git
cd lovable
```

**Frontend Client:**
Clone the **[lovable-frontend](https://github.com/subham3604/lovable-frontend)** repository alongside the backend. Refer to the frontend repository README for setup instructions. By default, the frontend should run on `http://localhost:5173` and point to the backend at `http://localhost:8080`.

---

## 3. Start Infrastructure Services

### Step 3.1: Start Database & Object Storage
Start the PostgreSQL (with `pgvector` enabled) and MinIO services using the compose file at the root of the project:

```bash
docker compose up -d
```
This runs:
- **PostgreSQL** (pgvector) listening on port `5435`.
- **MinIO** (S3-compatible Object Storage) listening on port `9000` (API) and `9001` (Console).

### Step 3.2: Start Redis Cache
Redis is required for preview routing and deployment coordination. Developers who are not testing preview environments can ignore Redis-related functionality, but the Redis service must still be running locally on port `6379` since the Spring Boot application configuration expects a valid Redis connection factory on startup.

Start a local Redis container:
```bash
docker run -d --name redis-local -p 6379:6379 redis:alpine
```

---

## 4. Configure Environment

Create a `src/main/resources/application-local.yaml` file to supply credentials for your local runtime:

```yaml
spring:
  datasource:
    username: your-database-username
    password: your-database-password
    url: jdbc:postgresql://localhost:5435/pgvector_lovable_test
  ai:
    openai:
      api-key: your-openai-api-key

jwt:
  secret-key: your-32-character-secret-key-here

stripe:
  api:
    secret-key: your-stripe-secret-key
  webhook:
    secret: your-stripe-webhook-signing-secret

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin123

client.url: http://localhost:5173
```

---

## 5. Run the Backend

Start the Spring Boot server using the Maven wrapper:

```bash
./mvnw spring-boot:run
```
Once started, the API server will listen at `http://localhost:8080`.

---

## 6. Startup Verification

Verify that the local environment is operational using the following checks:
- [ ] **Server Boot**: The application successfully starts and listens on port `8080`.
- [ ] **Database Connectivity**: JPA/Hibernate successfully establishes database connections and populates the schema in `pgvector_lovable_test`.
- [ ] **API Documentation**: Access the local Swagger UI at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
- [ ] **Authentication Flow**: Register a user through `POST /api/auth/signup` and verify that authentication endpoints function correctly and return a valid JWT token.
- [ ] **AI Chat Streams**: Exercise the streaming AI endpoints (`POST /api/chat/stream`) with your OpenAI key configured.
