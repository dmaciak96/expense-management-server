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

# Running application
