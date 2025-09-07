# 🏢 Shop API

**Shop API** — это RESTful сервис для управления клиентами, поставщиками, продуктами и изображениями товаров.  
Реализован на **Java 17** с использованием **Spring Boot**, **Spring Data JPA** и **PostgreSQL**.  

API обеспечивает быстрые и безопасные CRUD операции, управление запасами и обработку изображений товаров.  

---

## 🚀 Технологии
- **Java 17**  
- **Spring Boot**  
- **Spring Data JPA / Hibernate**  
- **PostgreSQL**  
- **Spring Validation**  
- **Swagger/OpenAPI**  
- **Maven**

---

## 📦 Основные сущности
| Сущность | Описание |
|----------|----------|
| **Client** | Клиенты компании |
| **Supplier** | Поставщики |
| **Product** | Продукты на складе |
| **Images** | Изображения товаров |

---

## 🔑 API Эндпоинты

### 👤 Клиенты (`/api/v1/client`)
| Метод | URL | Описание |
|-------|-----|----------|
| `POST` | `/add` | Добавление нового клиента |
| `DELETE` | `/{id}` | Удаление клиента по ID |
| `GET` | `/search?name=&surname=` | Поиск клиентов по имени и фамилии |
| `GET` | `/all?limit=&offset=` | Получение всех клиентов с пагинацией |
| `PATCH` | `/update-address/{id}` | Обновление адреса клиента |
| `GET` | `/{id}` | Получение клиента по ID |

---

### 🏭 Поставщики (`/api/v1/supplier`)
| Метод | URL | Описание |
|-------|-----|----------|
| `POST` | `/add` | Добавление нового поставщика |
| `PATCH` | `/updateAddress/{id}` | Обновление адреса поставщика |
| `DELETE` | `/{id}` | Удаление поставщика по ID |
| `GET` | `/all?limit=&offset=` | Получение всех поставщиков с пагинацией |
| `GET` | `/{id}` | Получение поставщика по ID |

---

### 🛒 Продукты (`/api/v1/product`)
| Метод | URL | Описание |
|-------|-----|----------|
| `POST` | `/add` | Добавление нового продукта |
| `POST` | `/decrease/{id}?count=` | Уменьшение количества продукта на складе |
| `GET` | `/{id}` | Получение продукта по ID |
| `GET` | `/all` | Получение всех продуктов |
| `DELETE` | `/{id}` | Удаление продукта по ID |

---

### 🖼 Изображения (`/api/v1/image`)
| Метод | URL | Описание |
|-------|-----|----------|
| `POST` | `/{productId}/add` | Добавление изображения для продукта |
| `PATCH` | `/{id}` | Обновление изображения по ID |
| `DELETE` | `/{id}` | Удаление изображения по ID |
| `GET` | `/by-product/{productId}` | Получение изображения товара по ID продукта |
| `GET` | `/{id}` | Получение изображения по его ID |

---

## ✨ Особенности проекта
- ✅ Валидация входных данных через `@Valid` и `@Validated`  
- ✅ Пагинация для клиентов и поставщиков  
- ✅ Поддержка работы с изображениями в формате `byte[]`  
- ✅ UUID для идентификации всех сущностей  
- ✅ Swagger/OpenAPI документация для тестирования API  
- ✅ Обработка ошибок с информативными сообщениями  

---

## ⚡ Запуск проекта
1. Клонировать репозиторий:  
```bash
git clone https://github.com/aeternitas008/ShopAPi.git
```

2. Настройте подключение к базе данных PostgreSQL в файле `application.properties`, например:
```
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db  
   spring.datasource.username=your_username  
   spring.datasource.password=your_password  
   spring.jpa.hibernate.ddl-auto=update
```

4. Сборка и запуск проекта через Maven:  
```bash
   mvn clean install  
   mvn spring-boot:run
```
5. Доступ к Swagger UI для тестирования API:  
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
