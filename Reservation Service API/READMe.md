# Reservation Service API

## Description
Reservation Service API is a Spring Boot pet project that provides a backend service for managing reservations.
The project was created to practice REST API development, database integration, and layered architecture.

## Features
- Create a reservation
- View all reservations
- Get reservation by id
- Update reservation information
- Delete reservation

## Technologies
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- Relational database (depends on configuration)

## Project Structure
The project follows a standard Spring Boot layered architecture:
- Controller layer (REST API endpoints)
- Service layer (business logic)
- Repository layer (database access)
- Entity/Model layer (data structure)

## How to Run
Run the project using Maven:

```bash
mvn spring-boot:run
