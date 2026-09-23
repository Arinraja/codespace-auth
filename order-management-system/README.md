# Order Management System (Spring Boot)

Clean, layered REST API for managing customers, products, and orders.

## Features

| Feature | API |
|---------|-----|
| Create Customer | `POST /api/customers` |
| Create Product | `POST /api/products` |
| Create Order | `POST /api/orders` |
| Add Products to Order | `POST /api/orders/{id}/products` |
| Get Order Details | `GET /api/orders/{id}` |
| Cancel Order | `PUT /api/orders/{id}/cancel` |

Also included: global exception handling, SLF4J logging, Bean Validation, Swagger UI, and JUnit 5 + MockMvc tests.

---

## Project structure

```
order-management-system/
├── pom.xml
├── sql/schema.sql
└── src/
    ├── main/java/com/oms/
    │   ├── OrderManagementApplication.java
    │   ├── config/          # OpenAPI / Swagger
    │   ├── controller/      # REST endpoints
    │   ├── dto/             # Request / response objects
    │   ├── entity/          # JPA entities
    │   ├── exception/       # Custom exceptions + handler
    │   ├── repository/      # Spring Data JPA
    │   └── service/         # Business logic
    ├── main/resources/application.properties
    └── test/java/com/oms/   # JUnit + MockMvc tests
```

---

## From scratch: configure on your office laptop

You already have **Spring Tool Suite 4 (STS4)**, **MySQL**, **Eclipse**, and **Maven**. Follow these steps.

### Step 1 — Verify Java 17+

Open Command Prompt / PowerShell:

```bat
java -version
```

You need **17 or higher**. If missing, install [Eclipse Temurin 17](https://adoptium.net/) and set `JAVA_HOME`.

### Step 2 — Start MySQL and create the database

1. Start **MySQL** (Windows Service, or MySQL Workbench → Server → Startup).
2. Open MySQL Workbench or CLI and run:

```sql
CREATE DATABASE IF NOT EXISTS order_db;
```

Default credentials used by this project:

| Setting  | Value      |
|----------|------------|
| Host     | localhost  |
| Port     | 3306       |
| Database | order_db   |
| Username | root       |
| Password | root       |

If your MySQL password is different, edit:

`src/main/resources/application.properties`

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Hibernate will create tables automatically (`ddl-auto=update`).

### Step 3 — Import the project into STS4 / Eclipse

**Option A — Import existing Maven project (recommended if you cloned this repo)**

1. Open **Spring Tool Suite 4**.
2. **File → Import → Maven → Existing Maven Projects → Next**.
3. **Browse** to the `order-management-system` folder (the one that contains `pom.xml`).
4. Ensure the project is checked → **Finish**.
5. Wait for Maven to download dependencies (bottom-right progress bar).

**Option B — Create a new Spring Boot project and copy the code**

1. In STS4: **File → New → Spring Starter Project**.
2. Name: `order-management-system`, Java **17**, Packaging **Jar**, Type **Maven**.
3. Dependencies: **Spring Web**, **Spring Data JPA**, **MySQL Driver**, **Validation**.
4. Finish, then copy the `com.oms` packages from this repo into `src/main/java`.
5. Add the OpenAPI dependency to `pom.xml` (already present in this project's `pom.xml`):

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### Step 4 — Configure application.properties

Confirm `src/main/resources/application.properties` matches your MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### Step 5 — Run the application

In STS4:

1. Right-click project → **Run As → Spring Boot App**.
2. Console should show: `Started OrderManagementApplication`.
3. App runs at: **http://localhost:8080**

Or from terminal (inside `order-management-system`):

```bat
mvn spring-boot:run
```

### Step 6 — Open Swagger UI

Browser:

- Swagger UI: http://localhost:8080/swagger-ui.html  
- OpenAPI JSON: http://localhost:8080/api-docs  

Use Swagger to try Create Customer → Create Product → Create Order → Add Product → Get Details → Cancel.

### Step 7 — Run JUnit tests (MockMvc / “Jtest”)

Tests use **H2 in-memory** — MySQL is **not** required for tests.

In STS4:

1. Right-click `src/test/java` → **Run As → JUnit Test**.

Or Maven:

```bat
mvn test
```

Integration test class: `OrderApiIntegrationTest`  
Unit test class: `CustomerServiceTest`

---

## API quick reference (sample JSON)

### 1. Create Customer

`POST http://localhost:8080/api/customers`

```json
{
  "name": "Asha Sharma",
  "email": "asha@example.com",
  "phone": "9876543210"
}
```

### 2. Create Product

`POST http://localhost:8080/api/products`

```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic mouse",
  "price": 799.00,
  "stockQuantity": 50
}
```

### 3. Create Order

`POST http://localhost:8080/api/orders`

```json
{
  "customerId": 1
}
```

### 4. Add Product to Order

`POST http://localhost:8080/api/orders/1/products`

```json
{
  "productId": 1,
  "quantity": 2
}
```

### 5. Get Order Details

`GET http://localhost:8080/api/orders/1`

### 6. Cancel Order

`PUT http://localhost:8080/api/orders/1/cancel`

---

## Tech stack

- Java 17+
- Spring Boot 3.2
- Spring Data JPA / Hibernate
- MySQL (runtime) + H2 (tests)
- springdoc-openapi (Swagger UI)
- JUnit 5 + MockMvc + Mockito

## Common issues

| Problem | Fix |
|---------|-----|
| `Communications link failure` | Start MySQL; check port 3306 |
| `Access denied for user 'root'` | Fix password in `application.properties` |
| Port 8080 already in use | Change `server.port` or stop the other process |
| Maven dependencies stuck | Right-click project → **Maven → Update Project** (Force Update) |
