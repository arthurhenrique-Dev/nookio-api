# Nookio — Plataforma de Microsserviços Imobiliários em Alta Escala 🚀

[🇺🇸 English](README.md) | [🇧🇷 Português](README_PT.md)

**Unindo busca de imóveis, pagamentos e agendamentos em uma arquitetura orientada a eventos de altíssima performance.**

---

## 📌 O que é o Nookio?

**Nookio** é uma plataforma imobiliária de nível corporativo projetada para suportar milhões de buscas de imóveis, agendamentos e transações financeiras com latência sub-milissegundo.

### 📊 Escala e Capacidade Estimada do Sistema

| Métrica de Capacidade | Capacidade do Sistema | Fator Determinante de Performance |
| :--- | :--- | :--- |
| ⚡ **Usuários Ativos Simultâneos** | **10.000 a 25.000 usuários simultâneos** *(no exato mesmo segundo)* | 600 a 1.200 conexões ativas no banco + Virtual Threads (Java 21) |
| 🚀 **Vazão Sustentada (RPS)** | **30.000 a 50.000 Requisições/seg (RPS)** | Cache L1 Caffeine RAM (<0,1ms) + Cache L2 Redis distribuído |
| 📈 **Usuários Ativos Diários (DAU)** | **1,5 Milhão a 3 Milhões usuários/dia** | Escalonado entre 10 e 20 pods no Kubernetes com auto-scaler HPA |
| 🌐 **Usuários Ativos Mensais (MAU)** | **10 Milhões a 20 Milhões usuários/mês** | Arquitetura assíncrona orientada a eventos via Apache Kafka |

---

### ❓ Por que o Nookio suporta esse nível de escala?

1. **Cache Multicamada (L1 RAM + L2 Redis)**: 80% do tráfego de um app imobiliário consiste em consultas. As recomendações D-1 são servidas diretamente da memória RAM da JVM (Caffeine) em **< 0,1ms**, eliminando gargalos de rede e de banco de dados. O warmup diário automático às 04:00 AM elimina atrasos por *cold start*.
2. **Divisão de Banco de Dados de Leitura e Escrita (Read/Write Splitting)**: O roteamento de transações (`@Transactional(readOnly = true)`) redireciona consultas para **2 Réplicas de Leitura PostgreSQL** com 600 a 1.200 conexões HikariCP nos pods do K8s, deixando o **Banco Principal de Escrita** 100% livre para transações de agendamento e pagamento.
3. **Java 21 Virtual Threads**: Processa dezenas de milhares de requisições concorrentes por instância sem esgotar o pool de threads do SO do Tomcat e sem a complexidade de código reativo.
4. **Barramento de Eventos Apache Kafka**: Logs de auditoria, webhooks de pagamento e disparo de e-mails são processados assincronamente por workers em segundo plano sem bloquear a resposta HTTP do usuário.
5. **Auto-Escalonamento no Kubernetes (HPA)**: Escala automaticamente os pods de aplicação de **4 para até 20 réplicas** sob alta demanda de CPU e Memória.

---

## 🧩 Módulos & Funcionalidades Principais

| Módulo | Descrição |
| :--- | :--- |
| 🏡 **Catálogo de Imóveis (`nookio-api`)** | Listagem de imóveis de alta concorrência, projeções de views SQL e Virtual Threads (JDK 21) suportando milhares de requisições/seg. |
| ⚡ **Mecanismo de Cache L1/L2** | Cache em duas camadas: Caffeine L1 Heap RAM (<0,1ms) + Redis L2 distribuído com agendador de warmup diário automático. |
| 💳 **Pagamentos & Faturamento (`nookio-payments`)** | Transações financeiras, controle de idempotência e disparo assíncrono de webhooks via Kafka. |
| 🔐 **Autenticação (`nookio-auth`)** | Encoder **Argon2PasswordEncoder** recomendado pela OWASP com emissão e validação sem estado de tokens JWT. |
| 📢 **Notificações & Envio (`nookio-publisher`)** | Envio assíncrono de e-mails orientado a fluxos de eventos do Kafka. |
| 📊 **Analytics & Auditoria (`nookio-analytics-api`)** | Ouvinte de eventos capturando logs de auditoria via Kafka com fallback em banco local em caso de partição de rede. |
| 🛡️ **Borda & Proxy (`nookio-gateway` / `nginx`)** | Rate limit de 10 req/s por IP, timeouts de 5s, limite de payload de 50MB, suporte a WebSockets e headers `SAMEORIGIN` para iFrames. |

---

## 🛠️ Stack Tecnológica

### Backend & Core
Java 21 (Virtual Threads ativadas) • Spring Boot 3.4 / 4 • Spring Cloud (Gateway, Config, Eureka) • Maven

### Mensageria & Cache
Apache Kafka (Event Bus) • Caffeine (Cache L1 RAM) • Redis (Cache L2 Distribuído)

### Dados & Persistência
PostgreSQL 16 (1 Banco Principal + 2 Réplicas de Leitura) • Flyway Migrations • Spring Data JPA / Hibernate

### Segurança & Criptografia
Hashing Argon2 (`Argon2PasswordEncoder`) • Tokens JWT • Circuit Breakers Resilience4j

### Infraestrutura, DevOps & Observabilidade
Docker & Docker Compose • Kubernetes (HPA, Ingress, ConfigMaps) • Terraform (AWS ECR, RDS, Helm) • Prometheus • Grafana • Micrometer Tracing (Brave / Zipkin) • Jenkins CI/CD

---

## 🏛️ Arquitetura & Topologia do Sistema

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

    subgraph Cache Multicamada & Separação de Banco
        API --> Caffeine[L1 Cache: Caffeine RAM\nWarmup Sub-ms D-1]
        API --> Redis[L2 Cache: Redis]
        API --> DB_Writer[(PostgreSQL Primary\nBanco de Escrita)]
        API --> DB_Reader1[(PostgreSQL Replica 1\nBanco de Leitura)]
        API --> DB_Reader2[(PostgreSQL Replica 2\nBanco de Leitura)]
    end

    subgraph Mensageria Assíncrona Orientada a Eventos
        API -->|Kafka Topic: audit-logs| Analytics[nookio-analytics-api]
        API -->|Kafka Topic: email-send| Publisher
        Payments -->|Kafka Topic: payment-webhook| API
        Kafka[(Apache Kafka Event Bus)]
    end

    subgraph Observabilidade & Métricas
        Prometheus[Prometheus\nMetrics Scraper :9090] --> API
        Prometheus --> Auth
        Prometheus --> Gateway
        Grafana[Grafana Dashboards :3000] --> Prometheus
    end
```

### Por que essas escolhas arquiteturais?
- **Java 21 Virtual Threads**: Elimina o esgotamento do pool de threads OS do Tomcat em cargas pesadas de I/O sem a complexidade de código reativo.
- **Separação de Leitura e Escrita no Banco**: Redireciona 80% do tráfego de consulta para Réplicas de Leitura, evitando travamentos no banco principal em horários de pico.
- **Barramento Kafka**: Garante o desacoplamento total entre pagamentos, e-mails de notificação e logs de auditoria.
- **Cache Caffeine L1 RAM**: Reduz a latência para **< 0,1ms** para recomendações pré-calculadas D-1 de imóveis.

---

## ⚙️ Pipeline CI/CD (Jenkins)

O `Jenkinsfile` declarativo automatiza o ciclo de vida de integração em 5 estágios:

```
[Checkout SCM] ➔ [Compilar JDK 21] ➔ [Validar Terraform] ➔ [Construir Imagens Docker] ➔ [Deploy K8s & HPA]
```

- **Estágio de Compilação**: Executa `./mvnw clean compile -DskipTests` nos microsserviços.
- **Estágio do Terraform**: Executa `terraform init -backend=false` e `terraform validate`.
- **Estágio do Docker**: Constrói imagens multi-stage JDK 21 para `nookio-api`, `nookio-auth`, etc.
- **Estágio de Implantação**: Aplica manifestos Kubernetes (`configmap.yaml`, `nookio-auth.yaml`, `nookio-api.yaml`, `hpa.yaml`, `ingress.yaml`).

---

## 🗂️ Estrutura do Repositório

```
nookio-api/
├── nookio-api/                   # Core API de Imóveis (Java 21)
│   ├── src/main/java/            # Código fonte do microsserviço
│   └── pom.xml                   # Dependências Maven (Caffeine, Micrometer, JPA)
│
├── nookio-auth/                  # Serviço de Autenticação & Identidade (Argon2 + JWT)
├── nookio-gateway/               # API Gateway (Spring Cloud Gateway)
├── nookio-payments/              # Serviço de Pagamentos & Faturamento
├── nookio-publisher/             # Serviço de Notificações & E-mails
├── nookio-analytics-api/         # Serviço de Logs de Auditoria (Kafka Listener)
├── nookio-configs/               # Config Server Centralizado (Spring Cloud Config)
├── nookio-discovery/             # Registro de Serviços (Eureka)
│
├── k8s/                          # Manifestos Kubernetes (Deployments, HPA, Ingress, Observabilidade)
├── terraform/                    # Infraestrutura como Código (AWS ECR, Réplicas RDS, Helm)
├── nginx/                        # Configuração do Reverse Proxy (Rate limit, timeouts 5s)
├── observability/                # Regras de raspagem do Prometheus
├── docker-compose.yml            # Orquestração local multi-contêiner
└── Jenkinsfile                   # Pipeline declarativo de CI/CD
```

---

## 🐳 Infraestrutura Docker

O `docker-compose.yml` orquestra todos os serviços em uma rede bridge isolada:

| Serviço Contêiner | Imagem | Função |
| :--- | :--- | :--- |
| `nookio-nginx` | `nginx:latest` | Reverse proxy, rate limit (10r/s), timeouts 5s, payload máximo de 50MB |
| `nookio-auth` | `nookio-auth:latest` | Microsserviço de autenticação (Argon2 + JWT) |
| `nookio-gateway` | `nookio-gateway:latest` | Roteamento do API Gateway |
| `nookio-postgres` | `postgres:16-alpine` | Banco de dados relacional (Principal e Réplicas) |
| `nookio-redis` | `redis:7-alpine` | Cache L2 distribuído |
| `nookio-kafka` | `cp-kafka:7.5.0` | Barramento de mensagens de eventos |
| `nookio-zookeeper` | `cp-zookeeper:7.5.0` | Coordenação do cluster Kafka |
| `nookio-prometheus` | `prom/prometheus:v2.50.1` | Coletor de métricas (:9090) |
| `nookio-grafana` | `grafana/grafana:10.3.3` | Dashboards de observabilidade (:3000) |

---

## 🔒 Segurança & Resiliência

- **Hashing de Senhas 100% Seguro**: `Argon2PasswordEncoder` no `nookio-auth`.
- **Rate Limit por IP**: O Nginx restringe tráfego a `10 req/s` com controle de burst (proteção contra 502/429).
- **Proteção XSS e iFrame**: Headers `X-Frame-Options: SAMEORIGIN` e `Content-Security-Policy`.
- **Circuit Breakers**: Resilience4j protege chamadas entre microsserviços com fallbacks em banco local em falhas de rede.
- **Auto-Escalonamento**: Kubernetes HPA escala dinamicamente de 4 a 20 pods baseado em CPU e Memória.

---

## 📄 Licença
Este projeto está sob a licença MIT.
