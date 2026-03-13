# Lab 06: SOA Integration Project

A microservices-based project demonstrating Service-Oriented Architecture (SOA). It integrates a **SOAP Identity Provider** with a **REST Profile Service** and a **Web Frontend**.

## Architecture Overview
This project uses **Token Delegation**:
1. **Frontend** gets a Token from the SOAP Service.
2. **Frontend** sends the Token to the REST Service.
3. **REST Service** validates the Token by calling the SOAP Service internally.


---

## Getting Started

### 1. Prerequisites
* Java 17 or higher
* Eclipse IDE (with Spring Tools 4)
* Maven

### 2. Running the Services
1. **SOAP Auth Service:** * Go to `user-soap-service`
   * Run as `Spring Boot App` (Port: `8081`)
2. **REST Profile Service:** * Go to `user-json-service`
   * Run as `Spring Boot App` (Port: `8082`)
3. **Frontend:**
   * Open `index.html` in your browser.

---

## API Documentation

### SOAP Service (Identity Provider)
**Endpoint:** `http://localhost:8081/ws`

| Operation | Input | Description |
| :--- | :--- | :--- |
| `RegisterRequest` | `username`, `password` | Creates a new user in memory. |
| `LoginRequest` | `username`, `password` | Returns a UUID session token. |
| `ValidateTokenRequest` | `token` | Returns `true/false` if token exists. |

### REST Service (User Profiles)
**Endpoint:** `http://localhost:8082/users`

| Method | Endpoint | Headers | Description |
| :--- | :--- | :--- | :--- |
| **POST** | `/users` | `Authorization: <token>` | Saves user bio and phone. |
| **GET** | `/users/{id}` | `Authorization: <token>` | Retrieves user data. |

---

##  Built With
* **Spring Boot** - Backend framework
* **Spring Web Services** - For SOAP implementation
* **Jakarta XML Binding** - For XML/Java mapping
* **Vanilla JS & CSS3** - For the interactive frontend

---
