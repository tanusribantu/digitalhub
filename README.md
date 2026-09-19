# DigitalHub – Digital Product Marketplace

DigitalHub is an end-to-end full-stack digital product marketplace where creators publish and sell digital goods, and customers discover, purchase, and download assets such as source code boilerplates, UI/UX design kits, e-books, presentation templates, 3D graphics, and study guides.

The platform provides dedicated, role-protected workflows for three user roles: **Customer**, **Seller**, and **Admin**.

---

## 🛠️ Tech Stack

- **Backend**:
  - **Language**: Java 21 / 25
  - **Framework**: Spring Boot 3.3.4
  - **Security**: Spring Security 6 with stateless JWT (JSON Web Tokens)
  - **Data Access**: Spring Data JPA & Hibernate ORM
  - **Database**: Dual-profile support:
    - Zero-setup file-backed embedded database (H2 in MySQL compatibility mode)
    - Production MySQL 8.0 support with standalone DDL (`schema.sql`)
  - **Build Tool**: Apache Maven 3.8+
- **Frontend**:
  - **Core**: HTML5, CSS3, Modern JavaScript (ES6+)
  - **Styling**: Custom modern dark-slate design system with glassmorphic cards and responsive grids
  - **Icons**: FontAwesome 6 CDN
  - **Package Management**: npm (Node.js)
- **File Storage**: Local disk-backed asset repository (`uploads/products/`) with authenticated streaming

---

## 📁 Project Structure

```
digitalhub/
├── frontend/                      # Client-side web application
│   ├── package.json               # Frontend package manager configuration
│   ├── index.html                 # Marketplace landing page (Hero, Trending, Categories)
│   ├── login.html                 # Authentication login with 1-click demo buttons
│   ├── register.html              # User registration with Customer/Seller roles
│   ├── products.html              # Product catalog with live search, filters, pagination
│   ├── product-details.html       # Product specifications, file info, reviews CRUD
│   ├── cart.html                  # Shopping cart with coupon validator
│   ├── checkout.html              # Simulated multi-gateway checkout (Card, PayPal, UPI)
│   ├── orders.html                # Orders history and customer invoices
│   ├── downloads.html             # Digital asset library with direct download triggers
│   ├── wishlist.html              # Saved products wishlist
│   ├── seller-dashboard.html      # Creator Studio (Product CRUD, file uploads, sales)
│   ├── admin-dashboard.html       # Admin Control Room (User moderation, coupons, metrics)
│   ├── profile.html               # Account settings and notification center
│   ├── css/                       # main.css, dashboard.css
│   └── js/                        # api.js, auth.js, navbar.js, products.js, cart.js, etc.
│
├── backend/                       # Spring Boot REST API
│   ├── pom.xml                    # Maven project object model
│   ├── src/main/java/com/digitalhub/
│   │   ├── DigitalHubApplication.java
│   │   ├── config/                # SecurityConfig, WebMvcConfig, DataInitializer
│   │   ├── controller/            # REST Controllers (Auth, Product, Cart, Order, etc.)
│   │   ├── dto/                   # Data Transfer Objects
│   │   ├── exception/             # GlobalExceptionHandler and custom exceptions
│   │   ├── model/                 # 16 JPA Entities
│   │   ├── repository/            # 15 Spring Data JPA Repositories
│   │   ├── security/              # JWT Token Provider, Auth Filter, UserDetailsService
│   │   └── service/               # Core business services
│   └── src/main/resources/
│       ├── application.properties # Default zero-setup profile with auto-seeding
│       ├── application-mysql.properties # MySQL production profile
│       └── schema-mysql.sql       # MySQL database DDL
│
├── .env.example                   # Template for environment variables
├── .gitignore                     # Git ignore configuration
├── README.md                      # Project documentation
└── schema.sql                     # Full MySQL 8.0 DDL script
```

---

## 📋 Prerequisites

To run this project, make sure you have installed:
- **Java JDK 17, 21, or 25** (`java -version`)
- **Apache Maven 3.8+** (`mvn -v`)
- **Node.js 18+** (`node -v`) for frontend tooling (optional, frontend is also served directly by Spring Boot)
- *(Optional)* **MySQL 8.0+** (if running against a live MySQL server instead of the default embedded database)

---

## 🚀 Running the Backend

### Option 1: Direct Maven Execution
From the root project directory:
```bash
cd backend
mvn spring-boot:run
```

### Option 2: Run Packaged JAR
```bash
cd backend
mvn clean package -DskipTests=true
java -jar target/digitalhub-backend-1.0.0.jar
```

### Option 3: Run with MySQL
1. Import `schema.sql` into MySQL:
   ```bash
   mysql -u root -p < schema.sql
   ```
2. Run Spring Boot with the MySQL profile:
   ```bash
   cd backend
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 🌐 Running the Frontend

The Spring Boot backend automatically serves the frontend on:
👉 **`http://localhost:8080/`**

Alternatively, to serve the frontend standalone during development:
```bash
cd frontend
npm install
npm start
```
The frontend will be served at `http://localhost:3000` and automatically connects to the backend REST API on port `8080`.

---

## 🔐 Environment Configuration

Copy `.env.example` to `.env` or set environment variables in your deployment environment:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `PORT` | Server HTTP port | `8080` |
| `DB_URL` | JDBC Connection URL | Embedded H2 / MySQL URL |
| `DB_USERNAME` | Database username | `sa` (H2) or `root` (MySQL) |
| `DB_PASSWORD` | Database password | Empty by default |
| `JWT_SECRET` | HMAC-SHA256 signing secret | Configured default |
| `JWT_EXPIRATION` | Token validity in milliseconds | `86400000` (24 hours) |

---

## ✨ Implemented Features

1. **Authentication & Authorization**:
   - Secure registration for Customers and Creators
   - JWT stateless Bearer token authentication
   - Role-based access control (`ROLE_CUSTOMER`, `ROLE_SELLER`, `ROLE_ADMIN`)
   - 1-click evaluation buttons on the login page
2. **Product Catalog & Browsing**:
   - Search by keyword, category, price range, and rating
   - Dynamic sorting (Newest, Most Downloaded, Highest Rated, Price Low-to-High, Price High-to-Low)
   - Pagination controls
3. **Product Details & Community Reviews**:
   - Full product specifications, file size, version, and preview images
   - Interactive Reviews CRUD (verified buyers only)
   - Instant "Buy Now" and "Add to Cart" actions
4. **Shopping Cart & Checkout**:
   - Duplicate digital product prevention
   - Real-time coupon validator (`DIGITAL20`, `WELCOME10`, `MEGA50`)
   - Multi-method simulated payment gateway (Credit Card, PayPal, UPI QR)
5. **Digital Delivery & Downloads**:
   - Secure customer digital library
   - Direct file streaming from disk (`uploads/products/`)
   - Download auditing with IP and timestamp tracking
6. **Creator / Seller Studio**:
   - Real-time analytics (Total earnings, sales count, active products)
   - Product catalog management (Add, Edit, Deactivate)
   - Asset package file uploader
   - Sales ledger
7. **Admin Control Room**:
   - Platform metrics and gross merchandise volume
   - User account status toggling (Enable / Disable) and role elevation
   - Product moderation (Approve, Reject, Feature)
   - Discount coupon generation and management
   - Content moderation reports ledger

---

## 🔑 Pre-Configured Demo Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **Customer** | `customer@digitalhub.com` | `customer123` |
| **Seller** | `seller@digitalhub.com` | `seller123` |
| **Admin** | `admin@digitalhub.com` | `admin123` |