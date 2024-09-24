# Microsservices Store

## 🔎 About the project

E-CommerceApp is a REST API for an e-commerce store, based on microservices architecture, it has both operations performed for the customer and operations performed by employees and administrators.

## ⚙️ System overview
![application-schema](readme/application.svg)

<details>
  <summary><h2>📋 Details</h2></summary>

### 1. Eureka
- This is the discovery service. It acts as a hub where all microservices connect, allowing them to know each other.

### 2. Gateway
- Main entry point of the application and load balancer.

### 3. Auth
- Responsible for validating tokens and retrieving user authentication data.
- Serves directly to the Gateway.

### 4. Common
- An internal library that all microservices use to implement security features.
- Most services rely on it to implement Spring Security logic without code repetition.
- In addition to having it locally in the project, its package is also distributed via Github Packages, so even if it is not present locally, services will still be able to access the package.

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

</details>


<details>
  <summary><h2>🛠️ Tecnologies</h2></summary>

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

</details>


<details>
  <summary><h2>📦 Documentation</h2></summary>

#### * _Please note that controllers with the prefix "Admin" require you to be logged in as a user with ADMIN or EMPLOYEE permission_
#### * _Controllers with the prefix "Client" only work with users with CLIENT permission_
#### * _Endpoints with "Internal" prefix do not accept external calls_
#### * _The Accounts service is responsible for always creating a default administrator user, using the ADMIN_USERNAME and ADMIN_PASSWORD environment variables._

---

<details>
  <summary><h3> Authentication and Authorization Flow</h3></summary>

#### 1. Token Interception by the API Gateway:
- The API Gateway checks the "Authorization" header in each request.
- If the header is absent, the request goes directly to the destination microservice.

#### 2. Token Validation:
- The Gateway collects the token and sends a request to the Auth microservice.
- The Auth microservice validates the token and returns the user's data (ID, username, roles).

#### 3. Passing User Data:
- The Gateway adds the user's data in internal headers (e.g., "X-auth-user-id").
- It then forwards the request to the destination microservice with these headers.

#### 4. Interception by the Destination Microservice:
- A security filter in the microservice captures the headers created by the Gateway.
- The filter maps the data from the headers into a user representation (UserDetails).

#### 5. Integration with Spring Security:
- The mapped user is persisted in the Spring Security context.
- Spring Security then manages the user's permissions for the microservice routes.

#### Future Adjustments:
  - The internal headers and the Auth microservice will be removed.
  - The JWT decoding will be done directly in each microservice, eliminating the need for centralized validation in Auth.

</details>


---

### Users
- You can create three types of users: ADMIN, EMPLOYEE and CLIENT
- Each user will have different access permissions

---

### Products
- Allows you to create departments, categories, manufacturers and products
- To create a category, you must create a department
- To create a product, you must provide a category and a manufacturer
  - Products are created without a price, and you must price them later
- Allows you to create product promotions
  - When the application starts, it checks all products whose promotions have already expired and restores them to their default state.
  - Promotions use a scheduler to schedule the end of promotions.
  - Every time the application starts, it checks all products whose promotions expire within 1 hour, defining a scheduler that triggers the change of the promotional price to the original price.
  - At every zero hour, it also checks all products whose promotions expire within 1 hour and defines a scheduler for each one.

---

### Cart
- You can create an anonymous cart, which is not linked to a real user. In this case, you pass a body with the desired product data, the API will generate a cart, an ID for that cart and will return its data to you.

- In the case of authenticated users (CLIENT), it is not necessary to send a body when creating the cart
  - First you create your cart, then add the products

- It is possible to merge anonymous carts with the cart of an authenticated user. To do this, you must be authenticated.
  - The merge brings together the products but does not add their quantities
  - The anonymous cart is deleted at the end of the process

- Your cart ID is the same as your user ID

- Orders are created from this service.
  - Enter the ID of the products in your cart that you want to generate an order for
  - At this stage, it is not possible to adjust the quantity of the products, you must adjust the quantities in the cart

<details>
  <summary><span>Examples</span></summary>

#### **CREATE ANONYMOUS CART**
POST: /anonymous/carts

Content-Type: application/json

    {
        "id": "1",   // product id
        "unit": 3    // desired units
    }

**RESPONSE:**

    {
        "id": "6ab3b395-7d42-45c6-9a89-313786b0f751",
        "products": [
            {
                "id": "1",
                "name": "Intel Core i9-11900K",
                "unit": 3,
                "price": 100.00
            }
        ],
        "totalPrice": 300.00,
        "createdAt": "2024-09-23T18:23:40.2128144",
        "modifiedAt": "2024-09-23T18:23:40.2128144",
        "anon": true
    }

---



#### **CREATE CART**
POST: /carts

Content-Type: application/json

**RESPONSE:**

    {
        "id": "2",
        "products": [],
        "totalPrice": 0,
        "createdAt": "2024-09-23T18:23:40.2128144",
        "modifiedAt": "2024-09-23T18:23:40.2128144",
        "anon": true
    }

</details>

---

### Orders

- When creating orders, it does not accept external calls. The creation of an order must be done via a synchronous connection between Cart and Orders
- Serves order data to CLIENT and ADMIN
- A CLIENT user can cancel his own order
- An ADMIN user can cancel any order

---

### Payments

- Serves only other services, communicating mainly through messages.
- Allows some GET queries for system administrators.
- Receives feedback from the payment API, causing the order status to change.

---

#### Confira a coleção Postman do projeto:
[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://app.getpostman.com/run-collection/31232249-c57739c1-b80d-463e-be53-c848cdbf703e?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D31232249-c57739c1-b80d-463e-be53-c848cdbf703e%26entityType%3Dcollection%26workspaceId%3Deac3d0ef-d921-4389-8597-a53480212132)

</details>

<details>
  <summary><h2>🚀 How to run</h2></summary>

### Deploy with Docker
Clone this repository:

    git clone https://github.com/mtpontes/blog-san-api.git

Raise the containers:

    docker-compose up --build

</details>


<details>
  <summary><h2>🔧 Adjustments and improvements</h2></summary>
The project is still under development, is currently using development settings. The next updates will focus on the following tasks:

- [x] Add standard price and promotional price
- [x] Implement a better separation of Users and Clients
- [x] Add more details to poor entities
- [x] Add more behaviors to entities, reducing dependence on external services for basic domain rules
- [x] Add a promotional price scheduler, so that when you set a promotional price, you also set a deadline for the promotional
- [x] Implement Spring Security
- [x] Create a Cart service
- [x] Create docker-compose
- [x] Create fallbacks for failures between services
- [ ] Allow users to have multiple addresses
- [ ] Each microservice will have the ability to decode JWT token, killing the need for Auth microservice
- [ ] Improvements in authentication, such as sending tokens by Email and authentication via third parties
- [ ] Sending emails regarding orders
- [ ] Create and handle dead letter exchanges
- [ ] Configure messaging rules
- [ ] Configure load balancing rules
- [ ] Create evaluation service
- [ ] Create discount coupon system
- [ ] Create detailed documentation with Postman (currently there are only requests in the Postman collection)

</details>

# 🤝 Credits

Special thanks to [@AlexandreMadeira](https://github.com/MadeiraAlexandre) for helping me with several suggestions, such as creating the concept of system services, and with the relationships of some entities.