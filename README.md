# 🚀 Feature Flag Engine

A **production-grade Feature Flag Engine** built with **Java 17, Spring Boot 3, PostgreSQL, Redis, Kafka, and Server-Sent Events (SSE)**.

The system allows teams to **create, manage, evaluate, and dynamically roll out features** without redeploying their applications.

It supports **targeting rules, percentage-based rollouts, consistent user bucketing, distributed caching, event-driven updates, real-time SSE notifications, monitoring, and a Java SDK**.

---

## ✨ Why This Project?

Modern applications frequently need to release features gradually:

* Enable a feature for internal users first
* Roll out a feature to 10% → 25% → 50% → 100% of users
* Target users based on attributes
* Quickly disable a problematic feature
* Update feature configuration without redeploying
* Monitor flag usage and system health

Instead of hard-coding feature checks inside applications, this project provides a **centralized Feature Flag Platform**.

---

# 🏗️ Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT APPLICATIONS                      │
│                                                             │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐             │
│   │ Web App  │    │ Mobile   │    │ Java SDK │             │
│   │          │    │ App      │    │          │             │
│   └────┬─────┘    └────┬─────┘    └────┬─────┘             │
│        │               │               │                    │
└────────┼───────────────┼───────────────┼────────────────────┘
         │               │               │
         └───────────────┼───────────────┘
                         │
                    HTTP / REST
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  FEATURE FLAG ENGINE                        │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                    CONTROL PLANE                      │  │
│  │                                                       │  │
│  │ FlagController → RuleController → FlagRepository      │  │
│  └───────────────────────────────┬───────────────────────┘  │
│                                  │                          │
│                                  ▼                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                     DATA PLANE                        │  │
│  │                                                       │  │
│  │ EvaluationController → EvaluationService             │  │
│  │                         ↓                             │  │
│  │                    FlagCacheService                   │  │
│  │                                                       │  │
│  │ SseController → Real-Time Updates                     │  │
│  └───────────────────────────────┬───────────────────────┘  │
└──────────────────────────────────┼──────────────────────────┘
                                   │
          ┌────────────────────────┼─────────────────────┐
          │                        │                     │
          ▼                        ▼                     ▼
    ┌───────────┐             ┌───────────┐        ┌───────────┐
    │ PostgreSQL│             │   Redis   │        │   Kafka   │
    │           │             │           │        │           │
    │ Flags     │             │ Cache     │        │ Events    │
    │ Rules     │             │ Pub/Sub   │        │ Audit     │
    │ Audit     │             │           │        │           │
    └───────────┘             └───────────┘        └─────┬─────┘
                                                         │
                                                         ▼
                                                   SSE / Consumers

                         ┌──────────────────────┐
                         │    OBSERVABILITY     │
                         │                      │
                         │ Prometheus → Grafana │
                         └──────────────────────┘
```

---

# 🎯 Core Features

## 1. Flag Management

Complete CRUD operations for feature flags.

* Create flag
* Get flag
* List flags
* Update flag
* Delete flag
* Enable / disable features
* Environment-specific configuration

---

## 2. Targeting Rules

Flags can be evaluated using user attributes.

Example:

```text
IF country == "IN"
AND plan == "PREMIUM"
THEN feature = ENABLED
```

Possible targeting attributes:

```text
userId
country
region
plan
device
platform
appVersion
custom attributes
```

---

## 3. Percentage Rollout

Gradually release a feature to a percentage of users.

Example:

```text
feature: new-checkout

0%   → Disabled
10%  → Internal rollout
25%  → Beta users
50%  → Large rollout
100% → Full release
```

This enables controlled feature releases without deploying new application code.

---

# 🎲 Consistent Bucketing

The same user should consistently receive the same feature decision.

Example:

```text
User ID
   │
   ▼
Hash Function
   │
   ▼
Bucket: 0 - 99
   │
   ▼
Compare with rollout %
```

For example:

```text
Rollout = 20%

user-101 → bucket 7  → ENABLED
user-102 → bucket 83 → DISABLED
user-103 → bucket 14 → ENABLED
```

This prevents users from randomly switching between enabled and disabled states.

---

# ⚡ Redis Caching

Redis is used to reduce database load and improve flag evaluation latency.

```text
Client
   │
   ▼
Evaluation API
   │
   ▼
Redis Cache
   │
   ├── Cache Hit ──────► Return Flag
   │
   └── Cache Miss
          │
          ▼
      PostgreSQL
          │
          ▼
      Update Redis
```

Benefits:

* Lower database load
* Faster evaluations
* Better scalability
* Distributed cache support

---

# 📨 Kafka Event Streaming

Flag changes generate events through Kafka.

Example:

```text
Admin updates flag
       │
       ▼
Feature Flag Engine
       │
       ▼
Kafka Event
       │
       ├────────► Audit Consumer
       │
       ├────────► Cache Consumer
       │
       └────────► SSE Update
```

Example event:

```json
{
  "eventType": "UPDATE",
  "flagId": 14,
  "flagName": "new-checkout",
  "actor": "system",
  "timestamp": "2026-09-29T10:30:00Z"
}
```

---

# 📡 Real-Time Updates with SSE

The engine supports **Server-Sent Events (SSE)** for real-time configuration updates.

Clients can subscribe to:

```http
GET /api/flags/stream
```

When a flag changes:

```text
Admin
  │
  ▼
Update Flag
  │
  ▼
Kafka
  │
  ▼
SSE Publisher
  │
  ▼
Connected Clients
```

This allows applications to receive configuration changes without continuously polling the server.

---

# ☕ Java SDK

A Java SDK is provided so client applications can evaluate flags easily.

Example usage:

```java
FlagClient client = new FlagClient(
        "http://localhost:8081"
);

boolean enabled = client.isEnabled(
        "new-checkout",
        "user-123"
);

if (enabled) {
    // New checkout flow
} else {
    // Existing checkout flow
}
```

The SDK abstracts communication with the Feature Flag Engine from application developers.

---

# 📊 Observability

The application exposes metrics through **Spring Boot Actuator + Prometheus**.

Important metrics can include:

```text
process_cpu_usage
lettuce_seconds_count
flag_evaluation_latency
cache_hit_total
cache_miss_total
flag_update_total
kafka_event_total
sse_connection_total
```

Monitoring flow:

```text
Spring Boot
     │
     ▼
Actuator
     │
     ▼
Prometheus
     │
     ▼
Grafana
```

---

# 🛠️ Tech Stack

| Layer            | Technology         |
| ---------------- | ------------------ |
| Language         | Java 17            |
| Framework        | Spring Boot 3      |
| API              | REST               |
| Database         | PostgreSQL 16      |
| Cache            | Redis 7            |
| Messaging        | Apache Kafka       |
| Real-Time        | Server-Sent Events |
| Monitoring       | Prometheus         |
| Dashboard        | Grafana            |
| SDK              | Java               |
| Containerization | Docker             |
| Deployment       | Docker Compose     |

---

# 📋 API Documentation

## Feature Flags

| Method   | Endpoint          | Description       |
| -------- | ----------------- | ----------------- |
| `GET`    | `/api/flags`      | Get all flags     |
| `GET`    | `/api/flags/{id}` | Get flag by ID    |
| `POST`   | `/api/flags`      | Create a new flag |
| `PUT`    | `/api/flags/{id}` | Update a flag     |
| `DELETE` | `/api/flags/{id}` | Delete a flag     |

---

## Targeting Rules

| Method   | Endpoint                             | Description        |
| -------- | ------------------------------------ | ------------------ |
| `GET`    | `/api/flags/{flagId}/rules`          | Get all rules      |
| `POST`   | `/api/flags/{flagId}/rules`          | Add targeting rule |
| `DELETE` | `/api/flags/{flagId}/rules/{ruleId}` | Delete rule        |

---

## Flag Evaluation

```http
POST /api/flags/{flagId}/evaluate
```

Example request:

```json
{
  "userId": "user-123",
  "attributes": {
    "country": "IN",
    "plan": "PREMIUM",
    "platform": "WEB"
  }
}
```

Example response:

```json
{
  "flagName": "new-checkout",
  "enabled": true,
  "reason": "All rules matched"
}
```

Possible evaluation reasons:

```text
Flag is disabled
All rules matched
Rule failed: {attribute} {operator} {value}
User not in rollout percentage ({bucket} >= {percentage})
```

---

# 📡 SSE Endpoint

```http
GET /api/flags/stream
```

Example event:

```text
event:flag-update
data:{
  "flagId": 14,
  "flagName": "new-checkout",
  "eventType": "UPDATE",
  "actor": "system",
  "timestamp": "..."
}
```

---

# ❤️ Health & Metrics

### Health

```http
GET /actuator/health
```

### Prometheus

```http
GET /actuator/prometheus
```

---

# 🚀 Getting Started

## Prerequisites

Install:

* Docker Desktop
* WSL 2 on Windows
* Java 17 for local development
* Git

Verify Java:

```bash
java -version
```

Expected:

```text
Java 17
```

---

# 🐳 Run with Docker Compose

Clone the repository:

```bash
git clone https://github.com/<your-username>/feature-flag-engine.git
```

Navigate into the project:

```bash
cd feature-flag-engine
```

Start all services:

```bash
docker-compose up -d
```

Check running containers:

```bash
docker-compose ps
```

---

# 🔗 Service URLs

After startup:

| Service          | URL                                     |
|------------------| --------------------------------------- |
| Feature Flag API | `http://localhost:8081`               |
| Health Check     | `http://localhost:8081/actuator/health` | 
| Prometheus       | `http://localhost:9090`                 |
| Grafana          | `http://localhost:3000`                 |
| PostgreSQL       | `localhost:5432`                        |
| Redis            | `localhost:6379`                        |
| Kafka            | `localhost:9092`                        |

---

# 🧪 Example Workflow

### Step 1 — Create a Feature Flag

```http
POST /api/flags
```

```json
{
  "name": "new-checkout",
  "enabled": true,
  "rolloutPercentage": 20
}
```

---

### Step 2 — Add Targeting Rule

```http
POST /api/flags/{flagId}/rules
```

```json
{
  "attribute": "country",
  "operator": "equals",        
  "value": "India"
}
```

---

### Step 3 — Evaluate Flag

```http
POST /api/flags/{flagId}/evaluate
```

```json
{
  "userId": "user-101",
  "attributes": {
    "country": "IN"
  }
}
```

---

### Step 4 — Receive Real-Time Updates

Connect to:

```http
GET /api/flags/stream
```

When the flag configuration changes, connected clients receive the update through SSE.

---

# 🔄 Feature Evaluation Flow

```text
                ┌──────────────┐
                │    Client    │
                └──────┬───────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Evaluation API  │
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │   Redis Cache   │
              └────────┬────────┘
                       │
                 Cache Miss
                       │
                       ▼
              ┌─────────────────┐
              │   PostgreSQL    │
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Targeting Rules │
              └────────┬────────┘
                       │
                       ▼
              ┌─────────────────┐
              │ Percentage Roll │
              └────────┬────────┘
                       │
                       ▼
                 TRUE / FALSE
```

---

# 🔐 Production Considerations

The architecture is designed with production-oriented concepts such as:

* Stateless REST APIs
* Distributed caching
* Event-driven architecture
* Asynchronous processing
* Real-time configuration propagation
* Consistent percentage bucketing
* Health checks
* Application metrics
* Containerized deployment
* Separation of control plane and data plane

---

# 📁 Suggested Project Structure

```text
feature-flag-engine/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.teraapp.model.flag/
│   │   │
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── rule/
│   │   │   ├── evaluation/
│   │   │   ├── cache/
│   │   │   ├── kafka/
│   │   │   ├── sse/
│   │   │   └── config/
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
├── sdk/
│   └── java-sdk/
│
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

# 📈 Scalability

The architecture can be extended horizontally:

```text
                 Load Balancer
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
       API-1         API-2        API-3
          │            │            │
          └────────────┼────────────┘
                       │
              ┌────────┴────────┐
              ▼                 ▼
           Redis              Kafka
              │                 │
              └────────┬────────┘
                       ▼
                  PostgreSQL
```

Because application instances can remain stateless, additional API instances can be added as traffic increases.

---

# 🔮 Future Enhancements

Potential future improvements:

* [ ] Multi-environment support
* [ ] API authentication and authorization
* [ ] RBAC for admin users
* [ ] Feature flag versioning
* [ ] Audit log UI
* [ ] Scheduled flag activation
* [ ] Flag dependency management
* [ ] Dark launch support
* [ ] Multi-region deployment
* [ ] Kubernetes deployment
* [ ] OpenTelemetry tracing
* [ ] Web-based Admin Dashboard
* [ ] Client SDKs for JavaScript, Python, and Go
* [ ] Automated rollout progression
* [ ] Experimentation / A-B testing support

---

# 🧠 Backend Concepts Demonstrated

This project demonstrates practical backend engineering concepts:

### Java

* Java 17
* Collections
* Streams
* Concurrency
* Exception handling
* Clean code
* SOLID principles

### Spring Boot

* REST APIs
* Dependency Injection
* Spring Data JPA
* Validation
* Actuator
* SSE
* Configuration management

### Distributed Systems

* Caching
* Event-driven architecture
* Kafka
* Pub/Sub
* Consistent hashing / bucketing
* Horizontal scalability
* Event propagation

### Databases

* PostgreSQL
* Relational modeling
* Transactions
* Indexing
* Query optimization

### DevOps

* Docker
* Docker Compose
* Prometheus
* Grafana
* Health checks
* Containerized services

---

# 📊 System Design Highlights

The project focuses on two important architectural concepts:

### Control Plane

Responsible for configuration management.

```text
Create Flag
    ↓
Update Flag
    ↓
Store in PostgreSQL
    ↓
Publish Kafka Event
```

### Data Plane

Responsible for high-frequency flag evaluation.

```text
Application
    ↓
Evaluation API
    ↓
Redis
    ↓
Evaluation Engine
    ↓
Boolean Result
```

Separating these responsibilities allows configuration operations and high-volume evaluations to scale independently.

---

# 🎓 What This Project Demonstrates

```text
REST APIs
   +
PostgreSQL
   +
Redis
   +
Kafka
   +
SSE
   +
Distributed Evaluation
   +
Observability
   +
Docker
```

It is designed to demonstrate real-world Java backend and distributed-system.

---

# 👨‍💻 Author

**Piyush**

Java Backend Developer | Spring Boot | Distributed Systems | AI/Backend Engineering

---

## ⭐ If you find this project useful

Give the repository a ⭐ and feel free to explore, fork, or contribute.

---
