# Microsservices Store

## 🔎 About the project

E-CommerceApp is a REST API for an e-commerce store, based on microservices architecture, it has both operations performed for the customer and operations performed by employees and administrators.

## 🔧 Adjustments and improvements

The project is still under development, is currently using development settings. The next updates will focus on the following tasks:

- [x] Add standard price and promotional price
- [x] Implement a better separation of Users and Clients
- [x] Add more details to poor entities
- [x] Add more behaviors to entities, reducing dependence on external services for basic domain rules
- [x] Add a promotional price scheduler, so that when you set a promotional price, you also set a deadline for the promotional
- [x] Implement Spring Security
- [x] Create a Cart service
- [ ] Create evaluation service
- [ ] Create discount coupon system
- [ ] Allow users to have multiple addresses
- [ ] Each microservice will have the ability to decode JWT token, killing the need for Auth microservice
- [ ] Improvements in authentication, such as sending tokens by Email and authentication via third parties
- [ ] Sending emails regarding orders
- [ ] Create fallbacks for failures between services
- [ ] Create and handle dead letter exchanges
- [ ] Configure messaging rules
- [ ] Configure load balancing rules
- [ ] Create docker-compose
- [ ] Create detailed documentation with Postman (currently there are only requests in the Postman collection)

## 📋 Prerequisites

Before you begin, make sure you've met the following requirements:

- Java 17
- Server RabbitMQ 3.7
- Database MySQL 8.0
- MongoDB 8

## 🖥️ System

### 1. Eureka
- This is the discovery service. It acts as a hub where all microservices connect, allowing them to know each other.

### 2. Gateway
- Main entry point of the application and load balancer.

### 3. Auth
- Responsible for validating tokens and retrieving user authentication data.
- Serves directly to the Gateway.

### 4. Common
- An internal library that all microservices use to implement security features.

### 5. Accounts
- Manages user accounts and authentication.

### 6. Products
- Manages products.
- Provides reliable product data to other microservices.

### 7. Cart
- Manages customers' shopping carts.
- Allows the creation of carts for unauthenticated users, featuring the merging of the local cart with the authenticated user's cart.

### 8. Orders
- Manages customer orders.
- Requests the generation and cancellation of payments.

### 9. Payments
- Manages order payments.


## 🛠️ Tecnologies

- [Docker](https://www.docker.com/)
- [TestContainers](https://testcontainers.com/)
- [RabbitMQ](https://www.rabbitmq.com/)
- [MySQL](https://dev.mysql.com/downloads/connector/j/)
- [MongoDB](https://www.mongodb.com)
- [H2](https://www.h2database.com/html/main.html)
- [JWT](https://github.com/auth0/java-jwt)
- [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Spring Web]()
- [Java Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)

## ⚙️ System overview
![application-schema](readme/application.svg)

## 🔑 Authentication and Authorization Flow

### 1. Token Interception by the API Gateway:
- The API Gateway checks the "Authorization" header in each request.
- If the header is absent, the request goes directly to the destination microservice.

### 2. Token Validation:
- The Gateway collects the token and sends a request to the Auth microservice.
- The Auth microservice validates the token and returns the user's data (ID, username, roles).

### 3. Passing User Data:
- The Gateway adds the user's data in internal headers (e.g., "X-auth-user-id").
- It then forwards the request to the destination microservice with these headers.

### 4. Interception by the Destination Microservice:
- A security filter in the microservice captures the headers created by the Gateway.
- The filter maps the data from the headers into a user representation (UserDetails).

### 5. Integration with Spring Security:
- The mapped user is persisted in the Spring Security context.
- Spring Security then manages the user's permissions for the microservice routes.

### Future Adjustments:
- New Flow:
    - The internal headers and the Auth microservice will be removed.
    - The JWT decoding will be done directly in each microservice, eliminating the need for centralized validation in Auth.

## 📦 Endpoints 

#### To log in as an ADMIN use username `root` and password `root@123` or create the environment variables `ADMIN_USERNAME` and `ADMIN_PASSWORD`.

[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://app.getpostman.com/run-collection/31232249-c57739c1-b80d-463e-be53-c848cdbf703e?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D31232249-c57739c1-b80d-463e-be53-c848cdbf703e%26entityType%3Dcollection%26workspaceId%3Deac3d0ef-d921-4389-8597-a53480212132)


# 🤝 Credits

- Special thanks to [@AlexandreMadeira](https://github.com/MadeiraAlexandre) for helping me with several suggestions, such as creating the concept of system services, and with the relationships of some entities.