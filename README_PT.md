# Arquitetura de Microsserviços Nookio 🚀

[🇺🇸 English](README.md) | [🇧🇷 Português](README_PT.md)

Uma arquitetura de microsserviços orientada a eventos, de alta escala e resiliente, construída para busca de imóveis, agendamentos e pagamentos.

---

## 🏗️ Arquitetura do Sistema

```mermaid
graph TD
    Client[Cliente / Mobile / Web] -->|HTTP / WebSockets| Nginx[Nginx Reverse Proxy\nRate Limit: 10r/s | Body: 50MB]
    
    subgraph Camada de Borda (Edge)
        Nginx --> Gateway[nookio-gateway\nSpring Cloud Gateway]
    end

    subgraph Autenticação & Segurança
        Gateway --> Auth[nookio-auth\nHashing Argon2 | Emissor JWT]
    end

    subgraph Microsserviços Principais
        Gateway --> API[nookio-api\nCore API - Java 21 Virtual Threads]
        Gateway --> Payments[nookio-payments\nPagamentos & Faturamento]
        Gateway --> Publisher[nookio-publisher\nEnvio de E-mails & Notificações]
    end

    subgraph Cache Multicamada & Persistência
        API --> Caffeine[L1 Cache: Caffeine RAM\nWarmup Sub-milissegundo D-1]
        API --> Redis[L2 Cache: Redis]
        API --> DB_Writer[(PostgreSQL Primary\nBanco de Escrita)]
        API --> DB_Reader1[(PostgreSQL Replica 1\nBanco de Leitura)]
        API --> DB_Reader2[(PostgreSQL Replica 2\nBanco de Leitura)]
    end

    subgraph Mensageria Orientada a Eventos
        API -->|Kafka Topic: audit-logs| Analytics[nookio-analytics-api]
        API -->|Kafka Topic: email-send| Publisher
        Payments -->|Kafka Topic: payment-webhook| API
        Kafka[(Apache Kafka Event Bus)]
    end

    subgraph Observabilidade & Monitoramento
        Prometheus[Prometheus\nMetrics Scraper :9090] --> API
        Prometheus --> Auth
        Prometheus --> Gateway
        Grafana[Grafana Dashboards :3000] --> Prometheus
    end
```

---

## 🌟 Recursos de Engenharia e Arquitetura

| Categoria | Tecnologia | Detalhes da Funcionalidade |
| :--- | :--- | :--- |
| **Linguagem & Runtime** | Java 21 (JDK 21) | Virtual Threads ativadas para concorrência de I/O massiva e sem bloqueio. |
| **Autenticação** | Argon2 + JWT | `Argon2PasswordEncoder` recomendado pela OWASP no `nookio-auth`. Validação de JWT sem estado. |
| **Performance & Cache** | Caffeine (L1) + Redis (L2) | Cache em memória RAM Caffeine (<0,1ms) com **warmup automático no startup e diariamente às 04:00 AM** para recomendados D-1. |
| **Banco de Dados** | PostgreSQL + HikariCP | **1 Banco Principal (Escrita) + 2 Réplicas (Leitura)**. `TransactionRoutingDataSource` redireciona consultas `@Transactional(readOnly = true)` para as réplicas. |
| **Mensageria de Eventos** | Apache Kafka | Tópicos assíncronos (`audit-logs`, `email-send`, `payment-webhook`) com fallback do Resilience4j. |
| **Reverse Proxy / Borda** | Nginx | Limite de 10 req/s por IP, timeout de 5s, limite de payload de 50MB, headers de segurança `SAMEORIGIN` para iFrames e suporte a WebSockets. |
| **Orquestração** | Kubernetes (k8s) | `HorizontalPodAutoscaler` (HPA) auto-escalando entre **4 e 20 pods** conforme uso de CPU/Memória. |
| **Infraestrutura como Código** | Terraform | Repositórios AWS ECR, instâncias PostgreSQL RDS (Primary + Réplicas) e Helm Ingress. |
| **Observabilidade** | Prometheus + Grafana | Coleta de métricas centralizada (`/actuator/prometheus`) e rastreamento distribuído via Micrometer Tracing (`traceId` / `spanId`). |
| **Automação CI/CD** | Jenkins | Pipeline declarativo no `Jenkinsfile` para build automatizado, validação de Terraform, build Docker e deploy no Kubernetes. |

---

## ⚡ Guia Rápido & Testes Locais (R$ 0,00 de Custo na AWS)

### 1. Rodar Tudo Localmente via Docker Compose
```bash
# Clonar o repositório
git clone https://github.com/henrique/nookio.git
cd nookio-api

# Subir Nginx, Postgres, Redis, Kafka, Zookeeper, Prometheus e Grafana
docker compose up -d
```

#### Acesso aos Serviços Locais:
- **Nginx Reverse Proxy**: `http://localhost/`
- **Métricas Prometheus**: `http://localhost:9090`
- **Dashboards Grafana**: `http://localhost:3000` *(Login: `admin` / Senha: `admin`)*
- **PostgreSQL**: `localhost:5432`
- **Redis**: `localhost:6379`
- **Kafka**: `localhost:9092`

---

### 2. Implantação no Kubernetes Local (Minikube / Docker Desktop)
```bash
# Aplicar todos os Deployments, Services, ConfigMaps, HPA e Ingress
kubectl apply -f k8s/

# Verificar pods em execução e o auto-scaler HPA
kubectl get pods
kubectl get hpa
```

---

### 3. Validar Infraestrutura no Terraform (Dry-Run / Sem Custos)
```bash
cd terraform
terraform init -backend=false
terraform plan
```

---

## 🔀 Estratégia de Branches & Commits Semânticos

| Nome da Branch | Descrição |
| :--- | :--- |
| `feat/kafka-interservice-messaging` | Branch principal ativa com integração do Apache Kafka. |
| `feat/auth-argon2-password-encoder` | Implementação do encoder de senhas Argon2 no `nookio-auth`. |
| `feat/caffeine-l1-cache-and-warmup` | Cache Caffeine L1 em memória RAM e warmup proativo no startup/diário. |
| `feat/database-read-write-replicas-and-hikaricp` | Roteamento de transações de leitura/escrita e ajuste do HikariCP. |
| `feat/kubernetes-and-hpa-manifests` | Deployments, Services, Ingress, ConfigMaps e HPA no Kubernetes. |
| `feat/terraform-infrastructure-as-code` | Manifestos do Terraform para ECR, Réplicas RDS e Helm Ingress. |
| `feat/observability-prometheus-grafana` | Métricas no Prometheus, dashboards no Grafana e Micrometer Tracing. |
| `refactor/audit-logs-kafka-publisher` | Refatoração do publicador de logs de auditoria para o Kafka. |
| `ci/jenkins-pipeline` | Pipeline declarativo automatizado no `Jenkinsfile`. |

---

## 📄 Licença
Este projeto está sob a licença MIT.
