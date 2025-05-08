Card Management API

Spring Boot REST API для управления банковскими картами с поддержкой ролей ADMIN и USER.

Стек технологий

Java 17

Spring Boot 3

Spring Security + JWT

PostgreSQL

MapStruct

Liquibase

Swagger / OpenAPI

Docker + Docker Compose

Локальный запуск

1. Клонируй репозиторий

2. Настрой .env (или application.yml)

Создай файл .env или используй application.yml/application.properties. Пример:

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cardsdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

ENCRYPTION_KEY=your-encryption-key
JWT_SECRET=your-jwt-secret

Не забудь добавить encryption.key и jwt.secret — они обязательны.

3. Запуск с помощью Docker

docker-compose up --build

Приложение: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui/index.html

4. Альтернатива: ручной запуск

Убедись, что PostgreSQL работает и создана БД cardsdb

Укажи данные для подключения

Запусти приложение:

./mvnw spring-boot:run

Документация API

Swagger UI: http://localhost:8080/swagger-ui/index.html

Роли и доступ

ADMIN:

Управление картами

Управление пользователями

USER:

Поиск своих карт

Тесты

Для запуска юнит- и интеграционных тестов:

./mvnw test

Liquibase

Миграции запускаются автоматически при старте. Файлы:

src/main/resources/db/changelog

API endpoints

Примеры:

POST /auth/register — регистрация

POST /auth/login — авторизация

GET /cards — поиск карт (USER)

POST /cards — создание (ADMIN)

PUT /cards/{id}/block — блокировка (ADMIN)
