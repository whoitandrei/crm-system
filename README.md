# CRM System - Customer Relationship Management

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.0-blue.svg)](https://gradle.org/)
[![H2](https://img.shields.io/badge/H2-Database-blue.svg)](https://www.h2database.com/)

## Описание проекта

CRM система для управления продавцами и их транзакциями. Система предоставляет REST API для выполнения CRUD операций, получения аналитических данных и управления бизнес-процессами.

### Основные возможности:
- Управление продавцами (создание, просмотр, редактирование, удаление)
- Учет транзакций продавцов
- Аналитика продаж и производительности
- Фильтрация по периодам и суммам
- Валидация данных
- Обработка ошибок

## Технологический стек

- **Java 21** - Основной язык программирования
- **Spring Boot 3.5.5** - Основной фреймворк
- **Spring Data JPA** - ORM и работа с базой данных
- **Spring Validation** - Валидация данных
- **H2 Database** - Встроенная база данных 
- **PostgreSQL** - База данных 
- **Lombok** - Уменьшение базы кода
- **Gradle** - Система сборки
- **JUnit 5** - Тестирование
- **Mockito** - Мокирование в тестах

## Архитектура проекта

```
src/
├── main/
│   ├── java/ru/shift/zverev/crm_system/
│   │   ├── config/          # Конфигурация и инициализация данных
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Обработка исключений
│   │   ├── model/           # JPA сущности
│   │   ├── repository/      # Репозитории данных
│   │   └── service/         # Бизнес-логика
│   └── resources/
│       ├── application.properties      # Основная конфигурация
│       └── application-prod.properties # Продакшн конфигурация
└── test/                    # Тесты (юнит и интеграционные)
```

## Быстрый старт

### Предварительные требования

- Java 21
- Git

### Клонирование и запуск

```bash
# Клонировать репозиторий
git clone https://github.com/whoitandrei/crm-system
cd crm-system

# Сборка проекта
.\gradlew build

# Запуск приложения
.\gradlew bootRun
```

Приложение будет доступно по адресу: `http://localhost:8080`

## API Документация

### Базовый URL
```
http://localhost:8080/api
```

### Sellers API

#### Основные операции
- `GET /api/sellers` - Получить всех продавцов
- `GET /api/sellers/{id}` - Получить продавца по ID
- `POST /api/sellers` - Создать нового продавца
- `PUT /api/sellers/{id}` - Обновить продавца
- `DELETE /api/sellers/{id}` - Удалить продавца

#### Аналитические endpoints
- `GET /api/sellers/analytics/most-productive` - Самый продуктивный продавец за все время
- `GET /api/sellers/analytics/most-productive/{days}` - Самый продуктивный продавец за период
- `GET /api/sellers/analytics/low-performance?limit={amount}` - Продавцы с продажами менее указанного лимита

#### Дополнительное задание:
- `GET /api/sellers/analytics/most-productive-time/{id}/{days}` - Самое продуктивное время продавца (с заранее заданным диапазоном days)

### Transactions API

#### Основные операции
- `GET /api/transactions` - Получить все транзакции
- `GET /api/transactions/{id}` - Получить транзакцию по ID
- `POST /api/transactions` - Создать новую транзакцию
- `PUT /api/transactions/{id}` - Обновить транзакцию
- `DELETE /api/transactions/{id}` - Удалить транзакцию

#### Фильтрация и аналитика
- `GET /api/transactions/seller/{sellerId}` - Транзакции по продавцу
- `GET /api/transactions/analytics/total/{sellerId}` - Общая сумма по продавцу
- `GET /api/transactions/analytics/statistics` - Статистика продаж за период


### Примеры запросов

#### Создание продавца
```bash
curl -X POST http://localhost:8080/api/sellers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Анна Смирнова",
    "contactInfo": "anna@example.com"
  }'
```

#### Создание транзакции
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "amount": 250.50,
    "paymentType": "CARD"
  }'
```

#### Получение аналитики
```bash
# Самый продуктивный продавец
curl http://localhost:8080/api/sellers/analytics/most-productive

# Статистика продаж за период
curl "http://localhost:8080/api/transactions/analytics/statistics?startDate=2023-12-01T00:00:00&endDate=2023-12-31T23:59:59"

# Дополнительное задание - самый продуктивный диапазон
curl "http://localhost:8080/api/transactions/analytics/most-productive-time/{id}/{days}"
```


## Тестирование

Проект содержит комплексное покрытие тестами:

### Запуск всех тестов
```bash
.\gradlew test
```

### Покрытие
- Сервисы: SellerService, TransactionService
- Контроллеры: SellerController, TransactionController
- Репозитории: SellerRepository, TransactionRepository
- Обработка исключений: GlobalExceptionHandler

## Модель данных

### Seller (Продавец)
```sql
CREATE TABLE sellers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_info VARCHAR(255),
    registration_date TIMESTAMP NOT NULL
);
```

### Transaction (Транзакция)
```sql
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_type VARCHAR(20) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES sellers(id)
);
```

## Валидация сущностей

### Seller
- `name`: обязательно, максимум 100 символов
- `contactInfo`: опционально, максимум 255 символов
- `registrationDate`: автоматически устанавливается

### Transaction
- `sellerId`: обязательно, должен существовать
- `amount`: обязательно, больше 0
- `paymentType`: CARD или CASH
- `transactionDate`: автоматически устанавливается, не может быть в будущем

## Обработка ошибок

Система возвращает структурированные ошибки:

### Примеры ответов

#### 404 Not Found
```json
{
  "status": 404,
  "error": "Resource not found",
  "message": "Seller not found with id: 999",
  "timestamp": "2023-12-01T10:30:00"
}
```

#### 400 Validation Error
```json
{
  "status": 400,
  "error": "Validation failed",
  "fieldErrors": {
    "name": "seller name is mandatory",
    "amount": "amount must be greater than 0"
  },
  "timestamp": "2023-12-01T10:30:00"
}
```

## Gradle Tasks
```bash
./gradlew clean          # Очистка
./gradlew compileJava    # Компиляция
./gradlew test           # Тесты
./gradlew build          # Полная сборка
./gradlew bootRun        # Запуск приложения
```

---

**Автор**: Зверев Андрей (whoitandrei)
