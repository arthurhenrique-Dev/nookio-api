# Nookio Microservices Architecture 🚀

A high-scale, resilient, event-driven microservices architecture built for real estate property search, bookings, and payments.

---

## 🏗️ System Architecture

```mermaid
graph TD
    Client[Client / Mobile / Web] -->|HTTP / WebSockets| Nginx[Nginx Reverse Proxy\nRate Limit: 10r/s | Body: 50MB]
    
    subgraph Edge Layer
        Nginx --> Gateway[nookio-gateway\nSpring Cloud Gateway]
    end

    subgraph Authentication & Security
        Gateway --> Auth[nookio-auth\nArgon2 Hashing | JWT Issuer]
    end

    subgraph Core Business Services
        Gateway --> API[nookio-api\nCore API - Java 21 Virtual Threads]
        Gateway --> Payments[nookio-payments\nPayments & Billing]
        Gateway --> Publisher[nookio-publisher\nEmail & Notifications]
    end

    subgraph Multi-Tier Caching & Persistence
        API --> Caffeine[L1 Cache: Caffeine RAM\nSub-ms D-1 Warmup]
        API --> Redis[L2 Cache: Redis]
        API --> DB_Writer[(PostgreSQL Primary\nWriter DB)]
        API --> DB_Reader1[(PostgreSQL Replica 1\nRead DB)]
        API --> DB_Reader2[(PostgreSQL Replica 2\nRead DB)]
    end

    subgraph Event-Driven Messaging
        API -->|Kafka Topic: audit-logs| Analytics[nookio-analytics-api]
        API -->|Kafka Topic: email-send| Publisher
        Payments -->|Kafka Topic: payment-webhook| API
        Kafka[(Apache Kafka Event Bus)]
    end

    subgraph Observability & Monitoring
        Prometheus[Prometheus\nMetrics Scraper :9090] --> API
        Prometheus --> Auth
        Prometheus --> Gateway
        Grafana[Grafana Dashboards :3000] --> Prometheus
    end
```

---

## 🌟 Key Architecture & Engineering Features

| Category | Technology | Feature Details |
| :--- | :--- | :--- |
| **Language & Runtime** | Java 21 (JDK 21) | Virtual Threads enabled for massive non-blocking I/O concurrency. |
| **Authentication** | Argon2 + JWT | OWASP-recommended `Argon2PasswordEncoder` in `nookio-auth`. Stateless JWT verification. |
| **Performance Caching** | Caffeine (L1) + Redis (L2) | Caffeine RAM cache (<0.1ms) with **proactive startup & daily 4 AM warmup** for D-1 recommendations. |
| **Database Architecture** | PostgreSQL + HikariCP | **1 Primary Writer + 2 Read Replicas**. `TransactionRoutingDataSource` routes `@Transactional(readOnly = true)` to replicas. |
| **Event Messaging** | Apache Kafka | Event-driven topics (`audit-logs`, `email-send`, `payment-webhook`) with Resilience4j fallback. |
| **Reverse Proxy / Edge** | Nginx | 10 req/s per IP rate limit, 5s timeout, 50MB payload limit, `SAMEORIGIN` iframe headers, WebSocket support. |
| **Orchestration** | Kubernetes (k8s) | `HorizontalPodAutoscaler` (HPA) scaling between **4 and 20 pods** based on CPU/Memory load. |
| **Infrastructure as Code** | Terraform | Automated AWS ECR repositories, RDS PostgreSQL (Primary + Replicas), and Helm Ingress. |
| **Observability** | Prometheus + Grafana | Centralized metrics (`/actuator/prometheus`) and Micrometer Tracing context (`traceId` / `spanId`). |
| **CI/CD Automation** | Jenkins | Declarative `Jenkinsfile` pipeline for automated build, Terraform validation, Docker build, and K8s deploy. |

---

## ⚡ Quick Start & Local Testing (R$ 0,00 / $ 0.00 AWS Cost)

### 1. Run Everything Locally with Docker Compose
```bash
# Clone the repository
git clone https://github.com/henrique/nookio.git
cd nookio-api

# Start Nginx, Postgres, Redis, Kafka, Zookeeper, Prometheus, and Grafana
docker compose up -d
```

#### Local Endpoint Access:
- **Nginx Reverse Proxy**: `http://localhost/`
- **Prometheus Metrics**: `http://localhost:9090`
- **Grafana Dashboards**: `http://localhost:3000` *(Login: `admin` / Password: `admin`)*
- **PostgreSQL**: `localhost:5432`
- **Redis**: `localhost:6379`
- **Kafka**: `localhost:9092`

---

### 2. Local Kubernetes Deployment (Minikube / Docker Desktop)
```bash
# Apply all Kubernetes Deployments, Services, ConfigMaps, HPA, and Ingress
kubectl apply -f k8s/

# Verify running pods and HPA auto-scaler
kubectl get pods
kubectl get hpa
```

---

### 3. Validate Terraform Infrastructure (Dry-Run)
```bash
cd terraform
terraform init -backend=false
terraform plan
```

---

## 🔀 Git Branching Strategy & Conventional Commits

| Branch Name | Description |
| :--- | :--- |
| `feat/kafka-interservice-messaging` | Active main branch with Apache Kafka integration. |
| `feat/auth-argon2-password-encoder` | Argon2 password encoder implementation in `nookio-auth`. |
| `feat/caffeine-l1-cache-and-warmup` | Caffeine L1 in-memory RAM cache and proactive startup/daily warmup. |
| `feat/database-read-write-replicas-and-hikaricp` | Read/Write DataSource transaction routing and HikariCP tuning. |
| `feat/kubernetes-and-hpa-manifests` | Kubernetes Deployments, Services, Ingress, ConfigMaps, and HPA. |
| `feat/terraform-infrastructure-as-code` | Terraform manifests for ECR, RDS Read Replicas, and Helm Ingress. |
| `feat/observability-prometheus-grafana` | Prometheus metrics, Grafana dashboards, and Micrometer Tracing. |
| `refactor/audit-logs-kafka-publisher` | Kafka Audit Log Publisher refactoring. |
| `ci/jenkins-pipeline` | Declarative `Jenkinsfile` CI/CD pipeline automation. |

---

## 📄 License
This project is licensed under the MIT License.
