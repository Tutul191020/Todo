# Todo Application - Project Structure

## 📁 Project Organization

```
Todo/basic/
├── 📂 src/main/java/com/example/todo/          # BACKEND
│   ├── 📂 configuration/                        # Spring Configuration
│   │   └── SecurityConfig.java                 # Security & JWT config
│   │
│   ├── 📂 controller/                           # REST API Controllers
│   │   ├── AuthController.java                 # Authentication endpoints
│   │   ├── TodoController.java                 # Todo CRUD endpoints
│   │   └── HealthCheckController.java          # Health check endpoint
│   │
│   ├── 📂 services/                             # Business Logic Layer
│   │   ├── AuthService.java                    # Authentication logic
│   │   ├── TodoService.java                    # Todo business logic
│   │   ├── CustomUserDetailsService.java       # User details for auth
│   │   ├── JwtUtil.java                        # JWT token utilities
│   │   ├── JwtAuthFilter.java                  # JWT filter
│   │   └── UserPrincipal.java                  # User principal wrapper
│   │
│   ├── 📂 repositories/                         # Data Access Layer
│   │   ├── TodoRepository.java                 # Todo JPA repository
│   │   ├── UserAccountRepository.java          # User JPA repository
│   │   └── AdminRepository.java                # Admin JPA repository
│   │
│   ├── 📂 entity/                               # Database Entities
│   │   ├── TodoItem.java                       # Todo entity
│   │   ├── UserAccount.java                    # User entity (base)
│   │   ├── AdminAccount.java                   # Admin entity (extends User)
│   │   ├── TodoStatus.java                     # Todo status enum
│   │   └── AccountStatus.java                  # Account status enum
│   │
│   ├── 📂 dto/                                  # Data Transfer Objects
│   │   ├── TodoRequest.java                    # Todo create/update DTO
│   │   ├── TodoResponse.java                   # Todo response DTO
│   │   ├── LoginRequest.java                   # Login request DTO
│   │   ├── RegisterRequest.java                # User registration DTO
│   │   ├── AdminRegisterRequest.java           # Admin registration DTO
│   │   └── JwtResponse.java                    # JWT response DTO
│   │
│   ├── 📂 common/                               # Common Utilities
│   │   ├── ApiResponse.java                    # Standard API response wrapper
│   │   └── GlobalExceptionHandler.java         # Global exception handling
│   │
│   └── TodoApplication.java                    # Spring Boot main class
│
├── 📂 src/main/resources/                       # FRONTEND & CONFIG
│   ├── 📂 static/                               # Static Web Resources
│   │   ├── 📂 css/
│   │   │   └── styles.css                      # Main stylesheet
│   │   ├── 📂 js/
│   │   │   └── app.js                          # Main JavaScript
│   │   ├── index.html                          # Main application page
│   │   └── admin-signup.html                   # Admin registration page
│   │
│   └── application.properties                  # Spring Boot configuration
│
├── pom.xml                                      # Maven dependencies
└── README.md                                    # This file
```

---

## 🏗️ Architecture Overview

### Backend (Spring Boot)

**Layered Architecture:**

```
┌─────────────────────────────────────────┐
│         Controllers (REST API)          │  ← HTTP Endpoints
├─────────────────────────────────────────┤
│         Services (Business Logic)       │  ← Core Logic
├─────────────────────────────────────────┤
│      Repositories (Data Access)         │  ← Database Queries
├─────────────────────────────────────────┤
│         Entities (Domain Models)        │  ← JPA Entities
└─────────────────────────────────────────┘
```

**Package Structure:**

| Package | Purpose | Key Classes |
|---------|---------|-------------|
| `configuration` | Spring configuration beans | SecurityConfig |
| `controller` | REST API endpoints | AuthController, TodoController |
| `services` | Business logic & validation | AuthService, TodoService |
| `repositories` | Database operations | TodoRepository, UserAccountRepository |
| `entity` | JPA entities & enums | TodoItem, UserAccount, TodoStatus |
| `dto` | Request/Response objects | TodoRequest, TodoResponse |
| `common` | Shared utilities | ApiResponse, GlobalExceptionHandler |

---

### Frontend (Vanilla JavaScript)

**Structure:**

```
static/
├── index.html              # Main SPA
├── admin-signup.html       # Admin registration
├── css/
│   └── styles.css         # All styles (dark theme, animations)
└── js/
    └── app.js             # All JavaScript (auth, todos, API calls)
```

**Frontend Architecture:**

```
┌─────────────────────────────────────────┐
│           HTML (Views)                  │  ← User Interface
├─────────────────────────────────────────┤
│        JavaScript (Logic)               │  ← Event Handlers, State
├─────────────────────────────────────────┤
│         API Client (Fetch)              │  ← Backend Communication
└─────────────────────────────────────────┘
```

---

## 🔧 Backend Package Details

### 1. **Configuration Layer**

**`configuration/SecurityConfig.java`**
- Spring Security configuration
- JWT authentication setup
- CORS configuration
- Static resource permissions
- Method-level security (`@EnableMethodSecurity`)

### 2. **Controller Layer** (REST API)

**`controller/AuthController.java`**
- `POST /api/v1/public/auth/login` - User login
- `POST /api/v1/public/auth/register` - User registration
- `POST /api/v1/public/auth/admin/register` - Admin registration

**`controller/TodoController.java`**
- `POST /api/v1/todos` - Create todo
- `GET /api/v1/todos` - Get user's todos
- `GET /api/v1/todos/{id}` - Get specific todo
- `PUT /api/v1/todos/{id}` - Update todo
- `DELETE /api/v1/todos/{id}` - Delete todo
- `PATCH /api/v1/todos/{id}/toggle` - Toggle completion
- `GET /api/v1/todos/admin/all` - Admin: view all todos

### 3. **Service Layer** (Business Logic)

**`services/AuthService.java`**
- User registration & validation
- Admin registration with email
- Login authentication
- JWT token generation

**`services/TodoService.java`**
- Todo CRUD operations
- User ownership validation
- Status toggling
- Admin view all todos

**`services/JwtUtil.java`**
- JWT token creation
- Token validation
- Token parsing
- Expiration handling

**`services/CustomUserDetailsService.java`**
- Load user by username
- Spring Security integration

**`services/JwtAuthFilter.java`**
- JWT token extraction from requests
- Token validation on each request
- Security context setup

### 4. **Repository Layer** (Data Access)

**`repositories/TodoRepository.java`**
```java
- findByUserId(Long userId)
- findByUserIdAndStatus(Long userId, TodoStatus status)
- findByIdAndUserId(Long id, Long userId)
- countByUserId(Long userId)
```

**`repositories/UserAccountRepository.java`**
```java
- findByUsername(String username)
```

**`repositories/AdminRepository.java`**
```java
- findByEmail(String email)
```

### 5. **Entity Layer** (Domain Models)

**`entity/TodoItem.java`**
- Database table: `todo_items`
- Fields: id, title, description, status, createdAt, updatedAt, user
- Relationships: ManyToOne with UserAccount

**`entity/UserAccount.java`**
- Database table: `user_accounts`
- Base class for users
- Inheritance: JOINED strategy
- Fields: id, username, firstName, lastName, password, createdAt, status

**`entity/AdminAccount.java`**
- Database table: `admins`
- Extends UserAccount
- Additional fields: email, isAdmin

**`entity/TodoStatus.java`**
- Enum: TODO, IN_PROGRESS, COMPLETED, DONE

**`entity/AccountStatus.java`**
- Enum: ENABLED, DISABLED, LOCKED

### 6. **DTO Layer** (Data Transfer)

**Request DTOs:**
- `LoginRequest` - username, password
- `RegisterRequest` - username, firstName, lastName, password
- `AdminRegisterRequest` - username, firstName, lastName, email, password
- `TodoRequest` - title, description, status

**Response DTOs:**
- `JwtResponse` - token, type, expiresAt, username, role
- `TodoResponse` - id, title, description, status, createdAt, updatedAt, userId, username

### 7. **Common Layer** (Utilities)

**`common/ApiResponse.java`**
```java
{
  "success": boolean,
  "message": string,
  "data": T
}
```

**`common/GlobalExceptionHandler.java`**
- Centralized exception handling
- Consistent error responses

---

## 🎨 Frontend Structure

### HTML Files

**`index.html`** - Main Application
- Login form
- Registration form
- Todo list interface
- Add todo form
- Filter buttons
- Admin panel
- Edit modal

**`admin-signup.html`** - Admin Registration
- Standalone admin registration page
- Email field (required for admins)
- Redirects to main page after success

### CSS (`styles.css`)

**Design System:**
- CSS Variables for theming
- Dark color palette
- Gradient accents
- Card-based layout
- Smooth animations
- Responsive breakpoints

**Components:**
- Auth forms
- Todo cards
- Buttons (primary, secondary, success, danger)
- Modals
- Badges
- Filter buttons
- Messages (success, error, info)

### JavaScript (`app.js`)

**Modules:**

1. **API Client**
   - `apiCall(endpoint, method, body)` - Fetch wrapper
   - JWT token management
   - Error handling

2. **Authentication**
   - `login()` - User login
   - `register()` - User registration
   - `logout()` - Clear session
   - Token storage in localStorage

3. **Todo Operations**
   - `createTodo()` - Add new todo
   - `loadTodos()` - Fetch user's todos
   - `toggleTodo(id)` - Toggle completion
   - `deleteTodo(id)` - Remove todo
   - `openEditModal(id)` - Open edit dialog
   - `saveEdit()` - Update todo

4. **Admin Features**
   - `loadAllTodos()` - Fetch all users' todos
   - `displayAdminTodos()` - Render admin view

5. **UI Management**
   - `displayTodos()` - Render todo list
   - `setFilter()` - Filter by status
   - `showMessage()` - Display notifications
   - `showApp()` / `showLogin()` - View switching

---

## 🔐 Security Architecture

### Authentication Flow

```
1. User submits credentials
   ↓
2. AuthController receives request
   ↓
3. AuthService validates credentials
   ↓
4. JwtUtil generates token
   ↓
5. Token returned to client
   ↓
6. Client stores token in localStorage
   ↓
7. Client includes token in Authorization header
   ↓
8. JwtAuthFilter validates token
   ↓
9. SecurityContext populated
   ↓
10. Request proceeds to controller
```

### Authorization

**Role-Based Access:**
- `USER` role: Can manage own todos
- `ADMIN` role: Can view all todos + manage own todos

**Endpoint Protection:**
- Public: `/api/v1/public/auth/**`
- Authenticated: All other `/api/v1/**` endpoints
- Admin-only: `/api/v1/todos/admin/all` (via `@PreAuthorize`)

**Ownership Validation:**
- All todo operations validate user ownership
- Users cannot access other users' todos
- Admins can only view (not modify) others' todos

---

## 📊 Database Schema

```sql
-- User Accounts (Base Table)
user_accounts
├── id (PK)
├── username (UNIQUE)
├── first_name
├── last_name
├── password (BCrypt)
├── created_at
└── status (ENUM)

-- Admin Accounts (Joined Table)
admins
├── user_id (PK, FK → user_accounts.id)
├── email (UNIQUE)
└── is_admin (BOOLEAN)

-- Todo Items
todo_items
├── id (PK)
├── title
├── description
├── status (ENUM: TODO, IN_PROGRESS, COMPLETED, DONE)
├── created_at
├── updated_at
└── user_id (FK → user_accounts.id)
```

---

## 🚀 Running the Application

### Prerequisites
- Java 21
- Maven 3.x
- PostgreSQL 17.x

### Setup

1. **Database Configuration**
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/basic_todo
   spring.datasource.username=tutul
   spring.datasource.password=postgres100@
   ```

2. **Build & Run**
   ```bash
   cd /home/tutul/Downloads/Todo/basic
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

3. **Access Application**
   - Main App: http://localhost:8080
   - Admin Signup: http://localhost:8080/admin-signup.html

---

## 📝 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/public/auth/login` | User login | No |
| POST | `/api/v1/public/auth/register` | User registration | No |
| POST | `/api/v1/public/auth/admin/register` | Admin registration | No |

### Todo Endpoints

| Method | Endpoint | Description | Auth Required | Admin Only |
|--------|----------|-------------|---------------|------------|
| POST | `/api/v1/todos` | Create todo | Yes | No |
| GET | `/api/v1/todos` | Get user's todos | Yes | No |
| GET | `/api/v1/todos/{id}` | Get specific todo | Yes | No |
| PUT | `/api/v1/todos/{id}` | Update todo | Yes | No |
| DELETE | `/api/v1/todos/{id}` | Delete todo | Yes | No |
| PATCH | `/api/v1/todos/{id}/toggle` | Toggle completion | Yes | No |
| GET | `/api/v1/todos/admin/all` | View all todos | Yes | **Yes** |

---

## 🎯 Key Features

### Backend
✅ JWT-based stateless authentication  
✅ Role-based access control (USER/ADMIN)  
✅ RESTful API design  
✅ Layered architecture  
✅ JPA/Hibernate ORM  
✅ BCrypt password encryption  
✅ Global exception handling  
✅ Method-level security  

### Frontend
✅ Single Page Application (SPA)  
✅ Modern dark theme UI  
✅ Smooth animations & transitions  
✅ Responsive design  
✅ Real-time updates  
✅ Modal dialogs  
✅ Filter functionality  
✅ Role-based UI rendering  

---

## 📦 Dependencies

### Backend (Maven)
- Spring Boot 3.5.7
- Spring Security 2.3.3
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- JWT (jjwt) 0.12.6

### Frontend
- Vanilla JavaScript (ES6+)
- CSS3 with variables
- HTML5

---

## 🔄 Development Workflow

1. **Backend Development**
   - Create entity → Repository → Service → Controller
   - Add DTOs for request/response
   - Configure security if needed
   - Test with Postman/curl

2. **Frontend Development**
   - Design HTML structure
   - Style with CSS
   - Implement JavaScript logic
   - Connect to backend API
   - Test in browser

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Project Status**: ✅ Production Ready

**Last Updated**: 2025-11-24
