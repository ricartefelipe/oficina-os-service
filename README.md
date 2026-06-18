# oficina-os-service

**Responsabilidade:** Gestão de Ordens de Serviço — abertura, controle de status e histórico.

Tech Challenge SOAT — Fase 4 | Microsserviço 1 de 3

---

## Stack

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Banco | PostgreSQL (relacional) |
| Migrations | Liquibase |
| Mensageria | RabbitMQ (Spring AMQP) |
| Segurança | OAuth2 / JWT (Keycloak) |
| Observabilidade | Micrometer + Prometheus |
| Testes | JUnit 5 + Cucumber (BDD) + Testcontainers |
| Qualidade | JaCoCo ≥ 80% + SonarCloud |

---

## Eventos publicados / consumidos

| Direção | Routing Key | Descrição |
|---|---|---|
| Publica | `os.aberta` | Quando uma nova OS é criada |
| Consome | `orcamento.aprovado` | OS avança para PAGAMENTO_PENDENTE |
| Consome | `orcamento.recusado` | OS avança para CANCELADA |
| Consome | `pagamento.confirmado` | OS avança para EM_EXECUCAO |
| Consome | `pagamento.falhou` | OS avança para CANCELADA |
| Consome | `execucao.finalizada` | OS avança para FINALIZADA |

---

## Como rodar localmente

### 1. Subir a infraestrutura

```bash
docker compose -f docker-compose.infra.yml up -d
```

Aguarda RabbitMQ, PostgreSQL, Keycloak, Prometheus e Grafana subirem.

### 2. Subir o serviço

```bash
./mvnw spring-boot:run
```

O serviço sobe na porta **8081** com context-path `/api`.

### Swagger UI

`http://localhost:8081/api/swagger-ui.html`

---

## Testes

```bash
# Unitários (rápido)
./mvnw -Pci clean verify

# Integração (requer Docker)
./mvnw clean verify
```

**Cobertura JaCoCo:** `target/site/jacoco/index.html`

---

## CI/CD

- Pipeline independente em `.github/workflows/ci.yml`
- Build + testes + SonarCloud + push Docker em cada push para `main`/`develop`
- Deploy manual em `.github/workflows/deploy.yml` (requer secret `KUBE_CONFIG_B64`)

---

## Saga Pattern — Estratégia Coreografada

Este serviço participa da Saga como produtor do evento inicial (`os.aberta`) e consumidor de eventos dos demais serviços. Não há orquestrador central — cada serviço reage de forma autônoma aos eventos do RabbitMQ.

**Justificativa:** A coreografia elimina o Single Point of Failure de um orquestrador centralizado. Cada microsserviço permanece autônomo e o acoplamento existe apenas com a infraestrutura de mensageria.

---

## Kubernetes

Manifestos em `k8s/`:
- `deployment.yaml` — Deployment + Service + HPA
- `configmap.yaml` — Variáveis de ambiente não sensíveis
- `secret.example.yaml` — Exemplo de Secret (não versionar com valores reais)
- `namespace.yaml` — Namespace `oficina`
