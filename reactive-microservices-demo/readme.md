# Reactive Microservices Demo

## Описание
Учебный pet-проект с набором микросервисов на Spring Boot.
Цель проекта — отработать взаимодействие сервисов по HTTP, реактивный стек (WebFlux),
реактивную работу с базой данных (R2DBC), а также базовую защиту через Basic Auth.

Состав репозитория:
- product-service: реактивный сервис продуктов (WebFlux + R2DBC H2)
- delivery-service: сервис доставки (Spring MVC, заглушка)
- report-service: реактивный сервис отчетов (WebFlux), дергает product-service через WebClient

## Технологии
- Java
- Spring Boot
- Spring WebFlux (product-service, report-service)
- Spring Web (delivery-service)
- Spring Security (Basic Auth)
- Spring Data R2DBC + H2 (product-service)
- Reactor (Mono/Flux)
- springdoc-openapi (Swagger в delivery-service)
- Maven

## Порты сервисов
- product-service: http://localhost:8080
- delivery-service: http://localhost:8081
- report-service: http://localhost:8082

## Аутентификация
В product-service включен Basic Auth для всех эндпоинтов.

Пользователи (заданы в SecurityConfig):
- admin / admin
- user / user

Пример curl:
```bash
curl -u admin:admin http://localhost:8080/products
