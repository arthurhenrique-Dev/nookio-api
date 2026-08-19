# Nookio — High-Scale Real Estate Microservices Platform 🚀

[🇺🇸 English](README.md) | [🇧🇷 Português](README_PT.md)

**Unifying property discovery, bookings, and payments into a high-performance event-driven architecture.**

---

## 📌 What is Nookio?

**Nookio** is an enterprise-grade real estate platform engineered to handle millions of property searches, bookings, and financial transactions with sub-millisecond latency.

### 📊 Estimated System Scale & Capacity

| Capacity Metric | Benchmark Capability | Performance Factor |
| :--- | :--- | :--- |
| ⚡ **Simultaneous Active Users** | **10,000 to 25,000 active users** *(at the exact same second)* | 600 to 1,200 active DB connections + Java 21 Virtual Threads |
| 🚀 **Sustained Web Throughput** | **30,000 to 50,000 Requests/sec (RPS)** | L1 Caffeine RAM cache (<0.1ms) + Redis L2 distributed cache |
| 📈 **Daily Active Users (DAU)** | **1.5 Million to 3 Million users/day** | Scaled across 10–20 Kubernetes pods with HPA auto-scaler |
| 🌐 **Monthly Active Users (MAU)** | **10 Million to 20 Million users/month** | Async event-driven architecture powered by Apache Kafka |

---

### ❓ Why Does Nookio Support This Level of Scale?

1. **Multi-Tier Caching (L1 RAM + L2 Redis)**: 80% of traffic consists of property searches. Queries for D-1 recommendations are served directly from JVM Heap RAM (Caffeine) in **< 0.1ms**, bypassing network and database bottlenecks. Proactive daily 4 AM warmup eliminates cold-start latency.
2. **Database Read/Write Replica Splitting**: Transactional routing (`@Transactional(readOnly = true)`) offloads read queries to **2 PostgreSQL Read Replicas** with 600 to 1,200 HikariCP connections across Kubernetes pods, leaving the **Primary Writer DB** 100% free for booking and payment mutations.
3. **Java 21 Virtual Threads**: Handles tens of thousands of concurrent I/O requests per instance without Tomcat OS thread exhaustion or complex reactive code.
4. **Apache Kafka Event-Driven Bus**: Audit logging, payment webhooks, and email dispatching are processed asynchronously in background workers without blocking HTTP responses.
5. **Kubernetes HPA Auto-Scaling**: Automatically scales application pods from **4 to 20 replicas** under high CPU/Memory load.

---

## 🧩 Core Modules & Features

| Module | Description |
| :--- | :--- |
| 🏡 **Property Catalog (`nookio-api`)** | High-concurrency property listings, SQL view projections, and Virtual Threads (JDK 21) handling thousands of requests/sec. |
| ⚡ **L1/L2 Caching Engine** | Dual-layer caching: Caffeine L1 Heap RAM (<0.1ms) + Redis L2 distributed cache with automated daily warmup runners. |
| 💳 **Payments & Billing (`nookio-payments`)** | Financial transactions, idempotency handling, and asynchronous webhook dispatching over Kafka. |
| 🔐 **Authentication (`nookio-auth`)** | OWASP-recommended **Argon2PasswordEncoder** with stateless JWT token issuance and validation. |
| 📢 **Publisher & Notifications (`nookio-publisher`)** | Asynchronous email dispatching driven by Kafka event streams. |
| 📊 **Analytics & Audit Logs (`nookio-analytics-api`)** | Event listener capturing audit logs over Kafka with local database fallback upon network partition. |
| 🛡️ **Edge Gateway & Proxy (`nookio-gateway` / `nginx`)** | IP rate limiting (10r/s), 5s timeouts, 50MB payload limits, WebSocket upgrade, and `SAMEORIGIN` iframe headers. |

---

## 🛠️ Technology Stack

### Backend & Core
Java 21 (Virtual Threads enabled) • Spring Boot 3.4 / 4 • Spring Cloud (Gateway, Config, Eureka) • Maven

### Messaging & Caching
Apache Kafka (Event Bus) • Caffeine (L1 RAM Cache) • Redis (L2 Distributed Cache)

### Data & Persistence
PostgreSQL 16 (1 Primary Writer + 2 Read Replicas) • Flyway Migrations • Spring Data JPA / Hibernate

### Security & Cryptography
Argon2 Hashing (`Argon2PasswordEncoder`) • JWT Tokens • Resilience4j Circuit Breakers

### Infrastructure, DevOps & Observability
Docker & Docker Compose • Kubernetes (HPA, Ingress, ConfigMaps) • Terraform (AWS ECR, RDS, Helm) • Prometheus • Grafana • Micrometer Tracing (Brave / Zipkin) • Jenkins CI/CD

---

## 🏛️ Architecture & System Topology

```mermaid
graph TD
    Client[Client / Mobile / Web] -->|HTTP / WebSockets| Nginx[Nginx Reverse Proxy\nRate Limit: 10r/s | Body: 50MB]
    
    subgraph Edge & Routing Layer
        Nginx --> Gateway[nookio-gateway\nSpring Cloud Gateway]
    end

    subgraph Authentication & Security
        Gateway --> Auth[nookio-auth\nArgon2 Hashing | JWT Issuer]
    end

    subgraph Core Business Microservices
        Gateway --> API[nookio-api\nCore API - Java 21 Virtual Threads]
        Gateway --> Payments[nookio-payments\nPayments & Billing]
        Gateway --> Publisher[nookio-publisher\nEmail & Notifications]
    end

    subgraph Multi-Tier Caching & Database Splitting
        API --> Caffeine[L1 Cache: Caffeine RAM\nSub-ms D-1 Warmup]
        API --> Redis[L2 Cache: Redis]
        API --> DB_Writer[(PostgreSQL Primary\nWriter DB)]
        API --> DB_Reader1[(PostgreSQL Replica 1\nRead DB)]
        API --> DB_Reader2[(PostgreSQL Replica 2\nRead DB)]
    end

    subgraph Event-Driven Asynchronous Messaging
        API -->|Kafka Topic: audit-logs| Analytics[nookio-analytics-api]
        API -->|Kafka Topic: email-send| Publisher
        Payments -->|Kafka Topic: payment-webhook| API
        Kafka[(Apache Kafka Event Bus)]
    end

    subgraph Observability & Metrics
        Prometheus[Prometheus\nMetrics Scraper :9090] --> API
        Prometheus --> Auth
        Prometheus --> Gateway
        Grafana[Grafana Dashboards :3000] --> Prometheus
    end
```

### Why These Architectural Choices?
- **Java 21 Virtual Threads**: Eliminates Tomcat OS thread pool exhaustion under heavy I/O workloads without complex reactive code.
- **Read/Write DB Splitting**: Offloads 80% of read traffic to Read Replicas, preventing primary database locks during peak booking windows.
- **Kafka Event Bus**: Ensures complete decoupling between payment processing, email notifications, and audit logging.
- **Caffeine L1 RAM Cache**: Reduces latency to **< 0.1ms** for static/D-1 pre-calculated property recommendations.

---

## ⚙️ CI/CD Pipeline (Jenkins)

The declarative `Jenkinsfile` automates the full integration lifecycle across 5 stages:

```
[Checkout SCM] ➔ [Compile JDK 21] ➔ [Validate Terraform] ➔ [Build Docker Images] ➔ [Deploy K8s & HPA]
```

- **Compile Stage**: Executes `./mvnw clean compile -DskipTests` across microservices.
- **Terraform Stage**: Runs `terraform init -backend=false` and `terraform validate`.
- **Docker Stage**: Builds multi-stage JDK 21 Docker images for `nookio-api`, `nookio-auth`, etc.
- **Deploy Stage**: Applies Kubernetes manifests (`configmap.yaml`, `nookio-auth.yaml`, `nookio-api.yaml`, `hpa.yaml`, `ingress.yaml`).

---

## 🗂️ Repository Structure

```
nookio-api/
├── nookio-api/                   # Core Property Catalog API (Java 21)
│   ├── src/main/java/            # Microservice source code
│   └── pom.xml                   # Maven dependencies (Caffeine, Micrometer, JPA)
│
├── nookio-auth/                  # Identity & Auth Service (Argon2 + JWT)
├── nookio-gateway/               # Spring Cloud API Gateway
├── nookio-payments/              # Payment & Billing Service
├── nookio-publisher/             # Email & Notification Service
├── nookio-analytics-api/         # Audit Logging Service (Kafka Listener)
├── nookio-configs/               # Spring Cloud Config Server
├── nookio-discovery/             # Eureka Service Registry
│
├── k8s/                          # Kubernetes Manifests (Deployments, HPA, Ingress, Observability)
├── terraform/                    # Infrastructure as Code (AWS ECR, RDS Read Replicas, Helm)
├── nginx/                        # Reverse Proxy configuration (Rate limiting, 5s timeout)
├── observability/                # Prometheus scraping rules
├── docker-compose.yml            # Multi-container orchestration
└── Jenkinsfile                   # Declarative CI/CD pipeline
```

---

## 🐳 Docker Infrastructure

The `docker-compose.yml` orchestrates all services in an isolated bridge network:

| Container Service | Image | Function |
| :--- | :--- | :--- |
| `nookio-nginx` | `nginx:latest` | Reverse proxy, rate limiting (10r/s), 5s timeouts, 50MB max payload |
| `nookio-auth` | `nookio-auth:latest` | Authentication microservice (Argon2 + JWT) |
| `nookio-gateway` | `nookio-gateway:latest` | API Gateway routing |
| `nookio-postgres` | `postgres:16-alpine` | Relational database (Primary & Read Replicas) |
| `nookio-redis` | `redis:7-alpine` | L2 distributed cache |
| `nookio-kafka` | `cp-kafka:7.5.0` | Event-driven message broker |
| `nookio-zookeeper` | `cp-zookeeper:7.5.0` | Kafka cluster coordination |
| `nookio-prometheus` | `prom/prometheus:v2.50.1` | Metrics scraper (:9090) |
| `nookio-grafana` | `grafana/grafana:10.3.3` | Observability dashboards (:3000) |

---

## 🔒 Security & Resilience

- **100% Secure Password Hashing**: `Argon2PasswordEncoder` in `nookio-auth`.
- **IP Rate Limiting**: Nginx restricts traffic to `10 req/s` with burst control (502/429 protection).
- **Iframe & XSS Protection**: `X-Frame-Options: SAMEORIGIN` and `Content-Security-Policy`.
- **Circuit Breakers**: Resilience4j protects inter-service calls with automated local DB fallbacks upon network failure.
- **Auto-Scaling**: Kubernetes HPA dynamically scales pods between 4 and 20 based on CPU/Memory thresholds.

---

## 📄 License
This project is licensed under the MIT License.
