# Expense Management Server

Backend service for an expense management application built with Kotlin and Spring Boot.

## Description

Expense Management Server provides a REST API for managing shared expenses between users.

The application currently supports:

* User registration and authentication
* JWT-based authentication and authorization
* User account management
* Creating and managing accounts/groups
* Managing account members
* Account invitations
* Creating and managing expenses
* MongoDB persistence

The project follows a **Ports and Adapters (Hexagonal Architecture)** approach and is divided into three main layers:

* `domain` – domain models, business rules and ports
* `application` – application use cases
* `adapter` – REST API, persistence, security and other infrastructure implementations

### Technology stack

* Kotlin 2.3
* Java 25
* Spring Boot 4
* Spring Security
* Spring Data MongoDB
* MongoDB
* JWT authentication
* Gradle
* Docker Compose
* JUnit 5
* Mockito
* Testcontainers

## Build

### Requirements

To build the application you need:

* Java 25
* Docker
* Docker Compose

The project includes the Gradle Wrapper, so installing Gradle separately is not required.

Clone the repository:

```bash
git clone https://github.com/dmaciak96/expense-management-server.git
cd expense-management-server
```

Build the application:

```bash
./gradlew clean build
```

The generated executable JAR can be found in:

```text
build/libs/
```

You can also build the executable Spring Boot JAR directly:

```bash
./gradlew clean bootJar
```

## Run

### Local environment

The repository contains a `compose.yaml` file which provides a local MongoDB instance.

Start MongoDB:

```bash
docker compose up -d
```

The local MongoDB instance is available on:

```text
localhost:27017
```

Run the application using the `local` Spring profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Alternatively, build the application first:

```bash
./gradlew bootJar
```

and run the generated JAR:

```bash
java -jar build/libs/expense-management-server-0.0.1-alpha.jar --spring.profiles.active=local
```

### Production configuration

Without the `local` profile, the application expects the following environment variables:

```text
DB_URI
JWT_SECRET
JWT_TOKEN_EXPIRATION_IN_SECONDS
```

Example:

```bash
export DB_URI="mongodb://username:password@localhost:27017/expense-management?authSource=admin"
export JWT_SECRET="your-base64-encoded-secret"
export JWT_TOKEN_EXPIRATION_IN_SECONDS="3600"

java -jar build/libs/expense-management-server-1.0.0.jar
```
