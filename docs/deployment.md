# Deployment Guide

This guide details the deployment procedure, environment configuration, and verification checklist for deploying the Genesis platform to production.

---

## 1. Prerequisites

Before deploying the platform, ensure you have the following resources provisioned:
- **Kubernetes Cluster**: Active cluster (e.g. DigitalOcean DOKS).
- **NGINX Ingress Controller**: Installed and bound to an external LoadBalancer IP.
- **Wildcard DNS**: Wildcard records (e.g. `*.genesis-app.uk` and `api.genesis-app.uk`) pointing to the Ingress LoadBalancer IP.
- **TLS Certificates**: A wildcard SSL certificate (e.g. Let's Encrypt) loaded as a Secret in the target namespace.
- **Container Registry**: A registry accessible by the Kubernetes cluster (e.g. Docker Hub, GHCR, DigitalOcean Container Registry).
- **PostgreSQL Database**: Instance with the `pgvector` extension enabled.
- **Object Storage**: S3-compatible bucket (e.g. Cloudflare R2) to store React project workspace templates and user codebases.
- **OpenAI API Credentials**: Active API key for AI code generation.
- **Stripe Credentials**: Stripe API secret keys and webhook signing secret.

---

## 2. Step-by-Step Deployment

### Step 2.1: Build & Push Container Images

Build and push the Docker images for the core Spring Boot application and custom Node.js routing proxy to your container registry.

**Spring Boot Backend:**
```bash
docker build -t your-registry/genesis-backend:latest .
docker push your-registry/genesis-backend:latest
```

**Genesis Proxy:**
```bash
cd proxy
docker build -t your-registry/genesis-proxy:latest .
docker push your-registry/genesis-proxy:latest
```

### Step 2.2: Apply Configurations & Secrets

> [!WARNING]  
> **Security Warning**: Do not commit populated secrets files to source control. Store production credentials using a secure secret management solution (like HashiCorp Vault, Kubernetes SealedSecrets) or inject them via a secured CI/CD deployment pipeline.

Configure the active environment settings by updating `k8s/configmap.yml` and `k8s/secrets.yml`.

#### Configurable Environment Properties (`k8s/configmap.yml`)
Configure only variables that are environment-dependent:
| Variable | Description | Example / Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://your-database-host:5432/your-db-name` |
| `REDIS_HOST` | Hostname of the in-cluster Redis service | `redis-service` |
| `REDIS_PORT` | Port of the in-cluster Redis service | `6379` |
| `CLIENT_URL` | Frontend root URL (for CORS allowance) | `https://genesis-app.uk` |
| `MINIO_URL` | S3-compatible Object Storage endpoint | `https://your-account-id.r2.cloudflarestorage.com` |
| `MINIO_BUCKET` | Destination bucket name | `lovable` |
| `APP_PREVIEW_BASE_DOMAIN`| Base domain used to route live preview subdomains | `genesis-app.uk` |
| `OPENAI_MODEL` | Active model selection | `gpt-4o-mini` |

#### Environment Secrets (`k8s/secrets.yml`)
| Secret Key | Description |
|---|---|
| `DB_USERNAME` | PostgreSQL database user |
| `DB_PASSWORD` | PostgreSQL database password |
| `OPENAI_API_KEY` | OpenAI API authentication key |
| `JWT_SECRET_KEY` | 32-character secret key to sign user auth tokens |
| `STRIPE_SECRET_KEY` | Stripe developer API secret key |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signature validation secret |
| `MINIO_ACCESS_KEY` | S3-compatible Access Key ID |
| `MINIO_SECRET_KEY` | S3-compatible Secret Access Key |

### Step 2.3: Deploy Manifests to Kubernetes

Apply the resource definitions to the cluster in the following logical order to avoid dependency errors:

```bash
# 1. Namespace, Redis Service, and RBAC Bindings
kubectl apply -f k8s/infra.yml

# 2. ConfigMap and Secrets
kubectl apply -f k8s/secrets.yml
kubectl apply -f k8s/configmap.yml

# 3. Spring Boot Backend Deployment
kubectl apply -f k8s/backend.yml

# 4. Genesis Routing Proxy Deployment
kubectl apply -f k8s/genesis-proxy.yml

# 5. Pre-Warmed Runner Pod Pool Deployment
kubectl apply -f k8s/runner-pods.yml

# 6. Wildcard Routing Ingress Configuration
kubectl apply -f k8s/ingress.yml

# 7. Cluster Network Policy Security Rules
kubectl apply -f k8s/policy.yml
```

---

## 3. Post-Deployment Verification

### 3.1 Verification Commands
Verify that all cluster resources are running and mapped correctly:

```bash
# Verify all namespace pods are active (Running status)
kubectl get pods -n genesis-ns

# Verify the wildcard ingress has acquired an external IP address
kubectl get ingress -n genesis-ns

# Verify Kubernetes services are created and discoverable
kubectl get svc -n genesis-ns
```

### 3.2 Verification Checklist
- [ ] **API Reachability**: Validate that the backend API is reachable using `/actuator/health` and verify a protected endpoint can be accessed with a valid JWT.
- [ ] **Swagger Documentation**: Confirm that interactive API documentation loads at `https://api.yourdomain.com/swagger-ui/index.html`.
- [ ] **Preview Routing**: Launch a preview environment and confirm that the generated subdomain remains reachable throughout the session and becomes unavailable after the configured idle timeout.
