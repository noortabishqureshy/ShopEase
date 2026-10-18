# ShopEase an E-Commerce Web Application

ShopEase is a web-based e-commerce application built using Java, Spring Boot, Spring Security, Thymeleaf, and MySQL. It provides a shopping interface where users can browse products, search for items, manage a shopping cart, and proceed to checkout. It also includes an admin dashboard for managing products.

## Features

### Customer Features

- View all available products.
- Search for products dynamically.
- Add products to a shopping cart.
- Increase or decrease product quantities.
- Remove products from the cart.
- Proceed to checkout.
- Enter their name, phone number, and delivery address.
- Place an order.
- See an order confirmation.

### Admin panel

- Log in using a username and password.
- View all products.
- Add new products.
- Update existing products.
- Delete products.
- Search for products.
- View customer orders.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Programming language |
| Spring Boot 4.1.1 | Backend application framework |
| Spring MVC | Web controllers and request handling |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database access |
| Hibernate | Object-relational mapping |
| MySQL | Relational database |
| Thymeleaf | Server-side HTML templates |
| HTML5 | Web page structure |
| CSS3 | Styling and responsive design |
| JavaScript | Client-side interactions |
| Maven | Dependency management and build |
| MySQL Workbench | Database management |

## Database Setup

This application uses MySQL to store its data.

### Step 1: Open MySQL Workbench

Open MySQL Workbench and create a new connection.

Open the connection configured, typically `127.0.0.1:3306`.

### Step 2: Create the database

```sql
CREATE DATABASE IF NOT EXISTS shopease_db;
```

### Step 3: Create the application database user

```sql
CREATE USER IF NOT EXISTS 'shopease_admin'@'localhost'
IDENTIFIED BY 'Password@12345';

GRANT ALL PRIVILEGES ON shopease_db.*
TO 'shopease_user'@'localhost';
```

> Note: Replace `Password@12345` with your own strong password.

### Step 4: Verify the database

Run:

```sql
SHOW DATABASES;
```

You should see `shopease_db` in the results.

## Application Configuration

Open:

```text
src/main/resources/application.properties
```

A typical local configuration for this project is:

```properties
spring.application.name=shopease

server.port=8080

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/shopease_db
spring.datasource.username=shopease_admin
spring.datasource.password=Password@12345

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

spring.thymeleaf.cache=false
```

## Admin Login Configuration

shopease\config\SecurityConfig.java

## Start the application

Run:

```powershell
.\mvnw.cmd spring-boot:run
```

## Open ShopEase

Open your browser and visit:

```text
http://localhost:8080
```