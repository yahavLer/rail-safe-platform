# Rail Safe Platform – Backend

Backend repository for **Rail Safe Platform**, a multi-service risk and safety management system for operational organizations.

The platform is designed to support structured risk reporting, prioritization, mitigation tracking, and follow-up workflows. This repository contains the backend microservices, infrastructure configuration, and AI-support components used by the system.

## Overview
The backend is built as a **Gradle multi-module project** with several Spring Boot services and supporting infrastructure.

It supports workflows such as:
- organization setup and configuration
- user management
- risk creation and classification
- mitigation / task management
- dashboard and reporting data
- AI-assisted image analysis for hazard reporting

## Architecture
This repository includes the following modules:
- `user_service`
- `risk_service`
- `organization_service`
- `task_service`
- `imageAnalysis-AI_service`
- `base44_bridge`

Infrastructure and orchestration files are also included at the root level.

## Tech Stack
- Java 17
- Spring Boot
- Gradle multi-module build
- PostgreSQL
- RabbitMQ
- Docker / Docker Compose
- Base44 bridge (Node.js)
- REST APIs

## Services
### `organization_service`
Responsible for organization-level configuration and setup, including organization metadata, categories, and risk matrix-related data.

### `user_service`
Handles user-related logic and organization-scoped user operations.

### `risk_service`
Core service for creating, updating, classifying, and retrieving risks.

### `task_service`
Handles mitigation and follow-up tasks linked to risks.

### `imageAnalysis-AI_service`
Supports AI-assisted hazard analysis from uploaded images and integrates with backend services to generate structured draft risk data.

### `base44_bridge`
Node-based bridge service used for AI function integration.

## Repository Structure
```text
rail-safe-platform/
├── user_service/
├── risk_service/
├── organization_service/
├── task_service/
├── imageAnalysis-AI_service/
├── base44_bridge/
├── gradle/
├── build.gradle
├── settings.gradle
├── compose.yaml
└── docker-compose.prod.yml
```

## Local Development
### Prerequisites
- Java 17
- Docker + Docker Compose
- Gradle wrapper (`./gradlew`)

### Clone the Repository
```bash
git clone https://github.com/yahavLer/rail-safe-platform.git
cd rail-safe-platform
```

### Build the Project
```bash
./gradlew build
```

On Windows:
```bash
gradlew.bat build
```

### Run with Docker Compose
```bash
docker compose -f compose.yaml up --build
```

This starts the core services and infrastructure including:
- PostgreSQL
- RabbitMQ
- user service
- risk service
- organization service
- task service
- image AI service
- base44 bridge

## Ports
Default ports in the compose setup:
- `8081` – user service
- `8082` – risk service
- `8083` – organization service
- `8084` – task service
- `8090` – image AI service
- `3001` – base44 bridge
- `5432` – PostgreSQL
- `5672` – RabbitMQ
- `15672` – RabbitMQ management UI

## Production Deployment
A separate production-oriented compose file is included:
- `docker-compose.prod.yml`

It uses environment variables for:
- database credentials
- RabbitMQ credentials
- Base44 integration values
- production Spring profile configuration

Example run command:
```bash
docker compose --env-file .env.production -f docker-compose.prod.yml up -d --build
```

## Example Responsibilities in the System
This backend supports business flows such as:
- creating and managing risks by organization
- assigning mitigation tasks and updating their status
- retrieving dashboard statistics
- enriching incident/risk creation with AI-based image analysis
- enabling service-to-service communication in a microservice environment

## Why This Project Matters
This project demonstrates:
- microservices architecture with clear service boundaries
- backend ownership across multiple domains
- production-style local environments with Docker Compose
- structured domain modeling for operational risk workflows
- integration of AI-assisted functionality into a real business system

## Suggested Environment Notes
For local development, make sure each service can access:
- PostgreSQL
- RabbitMQ
- internal service base URLs
- Base44 credentials if AI flow is enabled

For production, use a dedicated `.env.production` file and avoid committing secrets.

## Related Repository
Frontend repository:
- https://github.com/yahavLer/rail-safe-platform-front

## Status
This repository is an active project and reflects an evolving backend architecture for a full risk and safety management platform.
