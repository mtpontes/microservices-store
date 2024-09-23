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
  <summary><h2>🔑 Authentication and Authorization Flow</h2></summary>

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


Before you begin, make sure you've met the following requirements:

- Java 17
- Server RabbitMQ 3.7
- Database MySQL 8.0
- MongoDB 8


<details>
  <summary><h2>📦 Documentation</h2></summary>

#### * _Atente-se ao detalhe de que os controllers com prefixo "Admin" exigem que esteja logado como um usuário com permissão ADMIN ou EMPLOYEE_
#### * _Os controllers som sufixo "Client" só funcionam com usuários com permissão CLIENT_
#### * _Endpoints com prefixo "internal" não aceitam chamadas externas_
#### * _Set the **ADMIN_USERNAME** and **ADMIN_PASSWORD** environment variables to log in as an administrator_

---

### Users
- Você pode criar três tipos de usuário: ADMIN, EMPLOYEE e CLIENT
- Cada usuário terá diferentes permissões de acesso

---

### Products
- Permite criar departments, categories, manufacturers e products
- Para criar uma category é necessário criar um department
- Para criar um product é necessário fornecer uma categoria e um manufacturer
  - Products são criados sem preço, sendo necessário precifica-los posteriormente

---

### Cart
- Você pode criar um carrinho anônimo, que não é vinculado a um usuário de fato. Neste caso, você passa um body com os dados do produto desejado, a API irá gerar um car, um ID para esse cart e irá te retornar os dados dele:

- É possível mesclar carrinhos anônimos com o carrinho de um usuário autenticado. Para isso, é necessário estar autenticado.
  - A mesclagem reune os produtos mas não soma suas quantidades
  - O carrinho anônimo é excluído no fim do processo

- No caso de usuários autenticados (CLIENT), não é necessário enviar um body
  - Primeiramente você cria o seu carrinho, depois adiciona os produtos

- O ID do seu carrinho é o mesmo ID do seu usuário

- Os orders são criados à partir deste serviço.
  - Informe o ID dos produtos no seu carrinho que você deseja gerar um pedido
  - Nesta etapa não é possível ajustar a quantidade dos produtos, você deve ajustar as quantidades no carrinho

<details>
  <summary><span>Exemplos</span></summary>

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

- Na criação de pedidos, não aceita chamadas externas. A criação de um order deve ser feita via conexão síncrona entre Cart e Orders
- Serve dados dos pedidos para os CLIENT e ADMIN
- Um usuário CLIENT pode cancelar seu próprio pedido
- Um usuário ADMIN pode cancelar qualquer pedido

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

</details>

# 🤝 Credits

- Special thanks to [@AlexandreMadeira](https://github.com/MadeiraAlexandre) for helping me with several suggestions, such as creating the concept of system services, and with the relationships of some entities.