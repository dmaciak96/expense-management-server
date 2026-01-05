# Expense Management Server
Managing shared expenses with friends has never been easier. This app helps you keep track of who paid for what, split costs fairly, and avoid awkward money conversations.
Create balance groups, invite your friends, and add any expenses you want to share - the app will automatically divide the costs equally among all members. No more manual calculations, no more confusion, no more “who owes whom.”
You’ll never have to wonder who owes what ever again!

This project serves as a backend application providing a REST API for full and convenient use, it requires a dedicated client application.

# Application purpose
This application was created for educational purposes. It is built in Kotlin using the Spring Boot framework and incorporates the following technologies:
* **Spring Data JPA** – a Spring module used for database communication, 
* **Spring Security** – a Spring module responsible for securing the application, 
* **REST API** – the communication method used by client applications, 
* **PostgreSQL** – the primary database used by the system, 
* **JUnit & Mockito** – libraries for writing tests, 
* **ArchUnit** – used in this project to verify architectural correctness, 
* **Docker** – used to create and run the database container.

Project follows the **Hexagonal Architecture (Ports & Adapters)** approach, promoting clean separation between business logic and external systems.

# Testing
To run all types of tests with a single command, follow the steps below:
1. Start the Docker environment - this is required because the project uses Testcontainers for integration tests,
2. Run `./gradlew test` to execute all tests in the project.

## Running Unit Tests
To run only unit tests, execute:
`./gradlew test --tests com.example.expense_management_server.unit.*`

## Running Integration Tests
1. Start the Docker environment - this is required because the project uses Testcontainers,
2. Add JWT_SECRET as env variable (set some long custom value)
3. Run `./gradlew test --tests com.example.expense_management_server.integration.*`

## Running Architecture Tests
To run architecture tests, execute: `./gradlew test --tests com.example.expense_management_server.architecture.*`
