# Task Management API

A RESTful API for managing tasks with full CRUD operations, built with Spring Boot. Features comprehensive documentation, validation, and consistent error handling.

---

## Features

- **Task Management**
    - Create tasks with title, description, and due date
    - Update task status
    - Delete tasks
    - Retrieve tasks by ID or paginated list
- **Validation**
    - Required fields enforcement
    - Date/time format validation
    - Valid status transitions
- **Documentation**
    - Interactive Swagger UI
    - OpenAPI 3.0 specification
- **Response Standardization**
    - Uniform JSON response format
    - Proper HTTP status codes
    - Location headers for created resources

---

## Technologies

- Java 21
- Spring Boot 3
- Spring Data JPA
- Hibernate Validator
- SpringDoc OpenAPI

---

## Installation & Usage

1. **Requirements**
  - Java 21 JDK
  - Maven
  - MySQL (or configure preferred database in `application.properties`)

2. **Run Application**
   ```bash
   ./mvnw spring-boot:run

3. **Run Integration and Unit Test**
    ```bash
   ./mvnw clean test

## Access Endpoints

- **Base URL:** `http://localhost:8080/tasks`
- **Documentation:** `http://localhost:8080/documentation`

---

## API Endpoints

| Method | Endpoint          | Description                          | Status Codes               |
|--------|-------------------|--------------------------------------|----------------------------|
| POST   | `/create`         | Create new task                      | 201 Created, 400 Bad Request|
| GET    | `/{id}`           | Get task by ID                       | 200 OK, 404 Not Found      |
| GET    | `/`               | Get paginated task list              | 200 OK                     |
| PUT    | `/{id}`           | Update task status                   | 200 OK, 404 Not Found      |
| DELETE | `/{id}`           | Delete task                          | 204 No Content, 404 Not Found |

---

## Request Requirements

### Create Task
- **Required Fields:**
    - `title`: Non-blank string
    - `dueDate`: Future datetime in `yyyy-MM-dd'T'HH:mm` format
- **Optional Fields:**
    - `description`: String
- ### Request Body:
  ```json
  {
    "title": "Task Title",
    "description": "Task description",
    "dueDate": "2025-04-03T14:30"
  }
---

### Update Task
- **Required Field:**
    - `status`: Valid enum value (e.g., IN_PROGRESS, PENDING, COMPLETED)

- ### Request Body:
  ```json
  {
    "status": "COMPLETED"
  }
---

## Response Structure

All responses follow this format:
```json
{
  "status": [HTTP_STATUS_CODE],
  "message": "Descriptive message",
  "data": [RESOURCE_OR_NULL]
}