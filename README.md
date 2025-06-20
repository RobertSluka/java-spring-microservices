# Java Spring Microservices – Learning & Improvements

This project is a **fork** of [chrisblakely01/java-spring-microservices](https://github.com/chrisblakely01/java-spring-microservices), originally built to demonstrate a microservice architecture using Java, Spring Boot, PostgreSQL, and Kafka. I used it as a **hands-on learning resource** to understand modern microservice design patterns, DevOps principles, and gRPC integration.

---

## 🔍 What I Learned

- **Spring Boot Microservice Structure**: How to separate concerns into distinct services.
- **Service Communication**:
    - REST for external API communication
    - Kafka for asynchronous messaging between services
    - gRPC for efficient internal RPCs (planned improvement)
- **Dockerization**: Containerizing services and databases.
- **Service Discovery (Planned)**: Currently, services communicate via hardcoded URLs—will explore Eureka or Kubernetes-native DNS discovery.
- **Data Consistency**: Event-driven patterns with Kafka for syncing data between services.

---
### 💡 Suggestions for Future Improvements
| Area                        | Original Implementation        | Proposed Improvement |
|-----------------------------|-------------------------------|----------------------|
| gRPC Communication          | Not used                      | Introduce gRPC for faster internal communication |
| `.proto` Storage            | Not applicable                | Move to centralized [Git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules) for maintainability |
| Shared DTOs                 | Duplicated across services    | Externalize shared models to a common module |
| Service Discovery           | Hardcoded URLs                | Introduce Eureka or migrate to Kubernetes with native discovery |
| Observability               | Not present                   | Add Prometheus + Grafana + Zipkin for monitoring and tracing |
| CI/CD                       | Manual or none                | GitHub Actions for build/test/deploy workflows |

---

## 📊 Architecture Diagram (Planned)

A visual representation of the service interactions is being developed. This will include:

- REST API entry points
- Kafka event flows
- gRPC request paths (once implemented)
- Database interactions

> Will be published soon as PNG + Mermaid syntax.

---

## 🧪 Tech Stack

- Java 17
- Spring Boot
- Kafka
- PostgreSQL
- Docker / Docker Compose
- Maven

---

🤝 Acknowledgements

Huge thanks to @chrisblakely01 for the original implementation. This fork exists purely for educational purposes and to serve as a base for continued experimentation and architectural improvements.
---
📌 Goals

Add gRPC between services (e.g., Billing ↔ Patient)

Centralize .proto files

Document full architecture

Add unit + integration tests

CI/CD with GitHub Actions

Deploy on Kubernetes / DigitalOcean (future)
