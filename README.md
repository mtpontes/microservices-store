# Microsservices Store

## 🔎 About the project

E-CommerceApp is a REST API for an e-commerce store, based on microservices architecture, it has both operations performed for the customer and operations performed by employees and administrators.

## ⚙️ System overview
![application-schema](/repo/readme/application.svg)

<details>
  <summary><h2>📋 Details</h2></summary>

### Eureka
- This is the discovery service. It acts as a hub where all microservices connect, allowing them to know each other.

### Gateway
- Main entry point of the application and load balancer.

### Common
- An internal library that all microservices use to implement security features.
- Most services rely on it to implement Spring Security logic without code repetition.
- In addition to having it locally in the project, its package is also distributed via Github Packages, so even if it is not present locally, services will still be able to access the package.

### Accounts
- Manages user accounts and authentication.

### Products
- Manages products.
- Provides reliable product data to other microservices.

### Cart
- Manages customers' shopping carts.
- Allows the creation of carts for unauthenticated users, featuring the merging of the local cart with the authenticated user's cart.

### Orders
- Manages customer orders.
- Requests the generation and cancellation of payments.

### Payments
- Manages order payments.

</details>


<details>
  <summary><h2>🛠️ Tecnologies</h2></summary>

- [Swagger]()
- [Docker](https://www.docker.com/)
- [TestContainers](https://testcontainers.com/)
- [RabbitMQ](https://www.rabbitmq.com/)
- [MySQL](https://dev.mysql.com/downloads/connector/j/)
- [MongoDB](https://www.mongodb.com)
- [H2](https://www.h2database.com/html/main.html)
- [JWT](https://github.com/auth0/java-jwt)
- [Spring Boot]()

</details>


<details>
  <summary><h2>📦 Documentation</h2></summary>

### Swagger UI

To access the OpenAPI documentation with Swagger UI, run the containers and go to http://localhost:9092/swagger-ui/index.html (Gateway). The documentation can be accessed centrally via the Gateway using the URL already mentioned, and also via the API itself (accounts, products, orders, carts, payments).

> **Notes:**
>
> - Endpoints with the prefix "Admin" require you to be logged in as a user with ADMIN or EMPLOYEE permission
> - Endpoints with the prefix "Client" only work with users with CLIENT permission
> - Endpoints with "Internal" prefix do not accept external calls
> - The Accounts service is responsible for always creating a default administrator user, using the ADMIN_USERNAME and ADMIN_PASSWORD environment variables

---

<details>
  <summary><h3> Authentication and Authorization Flow</h3></summary>

#### 1. User authenticates to the Accounts service:
- Logs in.
- API generates a JWT with user identification data: id, username and roles.
- User receives the JWT token.

#### 2. Token Validation:
- When calling any service on an endpoint that requires authorization, a security filter intercepts, captures the JWT token and decodes it.
- With the decoded JWT, the service creates a representation of the user (UserDetailsImpl) in the security context, allowing the system to know who the logged-in user is and what permissions they have.

#### 3. Integration with Spring Security:
- The mapped user is persisted in the Spring Security context.
- Spring Security then manages the user's permissions for the microservice routes.

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

#### Check out the project's Postman collection:
[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://app.getpostman.com/run-collection/31232249-c57739c1-b80d-463e-be53-c848cdbf703e?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D31232249-c57739c1-b80d-463e-be53-c848cdbf703e%26entityType%3Dcollection%26workspaceId%3Deac3d0ef-d921-4389-8597-a53480212132)

</details>

<details>
  <summary><h2>🚀 How to run</h2></summary>

### Deploy with Docker
This docker-compose file is for demonstration purposes, facilitating deployment in any environment.

Clone this repository:

    git clone https://github.com/mtpontes/microservices-store.git

Raise the containers:

    docker-compose up --build

### Known Issues

#### Line endings in "mvnw" file causing error on deploy (CRLF vs LF)

If you are running the application on a Linux environment after cloning the repository on a Windows machine, you might encounter issues with the `mvnw` script due to line endings being converted to CRLF (Windows format) instead of LF (Unix format). This can cause the script to fail, especially when running Maven commands like:

    mvn clean install -DskipTests

This error is typically related to the line endings issue.

To fix this:

1. **Check the line endings**:
   - Open the project folder in a text editor like VSCode.
   - Check the line ending format of the `mvnw` file (it should be `LF`).

2. **Convert to LF if necessary**:
   - In VSCode, you can change the line endings by clicking on the bottom right corner where the current line ending format is displayed and selecting `LF` (Unix).
   - Alternatively, you can run the following command in Git Bash or WSL to convert the line endings:
     ```bash
     sed -i 's/\r$//' mvnw
     ```

Once you've ensured the correct line endings, run the following command to build and start the containers:

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
- [x] Each microservice will have the ability to decode JWT token, killing the need for Auth microservice
- [x] Create detailed API documentation with Swagger
- [x] Group all OpenAPI documentation into Gateway
- [ ] Implement caching with Redis
- [ ] Allow users to have multiple addresses
- [ ] Sending emails regarding orders
- [ ] Create and handle dead letter exchanges
- [ ] Configure messaging rules
- [ ] Configure load balancing rules
- [ ] Integrate the Payments service with a real payment API, making the service fully functional
- [ ] Create evaluation service
- [ ] Create discount coupon system

</details>

# 🤝 Credits

Special thanks to [@MadeiraAlexandre](https://github.com/MadeiraAlexandre) for helping me with several suggestions, such as creating the concept of system services, and with the relationships of some entities.