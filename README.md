# 🚀 Система управления банковскими картами

Backend-приложение на Java с использованием Spring Boot для управления банковскими картами. Реализована авторизация через JWT, CRUD-операции, переводы между картами, фильтрация, шифрование данных и документация через Swagger.

---

## ехнологии

- Java 17+
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Liquibase (миграции)
- Swagger (Springdoc OpenAPI)

---

## Возможности

### 👤 Пользователь:
- Просмотр своих карт (поиск, фильтрация, пагинация)
- Создание карты
- Запрос на блокировку / активацию карты
- Переводы между своими картами
- Просмотр баланса

### Администратор:
- CRUD карт
- Видит все карты в системе
- Управление пользователями (регистрация)

---

## Настройка и запуск

###  Предварительные требования:
- Java 17
- Maven
- PostgreSQL (порт: `7777`, база: `card`, пользователь: `postgres`, пароль: `drakon0409`)

---

### 🛠 Шаги:

1. Клонируй проект:

```bash
git clone <REPO_URL>
cd Bank_REST
```

2. Убедись, что в PostgreSQL есть база:
```sql
CREATE DATABASE card;
```

3. Проверь файл application.yml:


spring:
  datasource:
    url: jdbc:postgresql://localhost:7777/card
    username: postgres
    password: drakon0409
  jpa:
    hibernate:
      ddl-auto: none
  liquibase:
    enabled: true

4. Запусти приложение:
```bash
mvn spring-boot:run
```

## 📌 Тестовые пользователи (создаются автоматически)

| Роль   | Логин   | Пароль     |
|--------|---------|------------|
| ADMIN  | admin   | adminpass  |
| USER   | user    | userpass   |

---

## 📘 Документация API

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI YAML: [`docs/openapi.yaml`](./docs/api-docs.yaml)

---

## 🗄 Миграции Liquibase

- Файлы миграций: `src/main/resources/db/migration/*.yaml`
- Основной файл: `db.changelog-master.yaml`
- При первом запуске создаются таблицы: `users`, `cards`

---

## 📂 Структура проекта

- `src/main/java/...` — основной код
- `src/main/resources/application.yml` — конфигурация
- `docs/openapi.yaml` — OpenAPI спецификация

---

## ✅ Выполненные требования

- [x] JWT + Spring Security
- [x] Роли ADMIN / USER
- [x] CRUD для карт
- [x] Переводы между картами
- [x] Фильтрация, пагинация
- [x] Маскирование и шифрование номеров карт
- [x] Liquibase миграции
- [x] Swagger UI + OpenAPI
- [x] Валидация + глобальная обработка ошибок

---

## 📝 Автор

Тестовое задание для компании Effective Mobile  
Август 2025