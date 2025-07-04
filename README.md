# 🎂 CakeLicious - Premium Artisan Cake Management System

A modern, full-featured cake bakery management system built with Java, MySQL, and modern web technologies.

## 🚀 Features

### 👥 Multi-Role System
- **Admin**: Complete system management, analytics, staff management
- **Staff**: Order processing, inventory management, task tracking
- **Customer**: Browse cakes, place orders, track deliveries

### 🛒 Core Functionality
- **Product Management**: Full CRUD operations for cakes and categories
- **Order Processing**: Complete order lifecycle management
- **Inventory Tracking**: Real-time stock monitoring with alerts
- **User Authentication**: Secure role-based access control
- **Analytics Dashboard**: Sales reports and performance metrics

### 🎨 Modern UI/UX
- Responsive design with mobile-first approach
- Smooth animations and micro-interactions
- Apple-level design aesthetics
- Intuitive user experience across all roles

## 🛠️ Tech Stack

### Backend
- **Java**: Core backend logic with HttpServer
- **MySQL**: Database with JDBC connectivity
- **RESTful API**: Clean API design for frontend communication

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Modern styling with custom properties and animations
- **Vanilla JavaScript**: No framework dependencies, pure JS

### Database
- **MySQL**: Relational database with proper normalization
- **JDBC**: Direct database connectivity

## 📋 Prerequisites

- Java 8 or higher
- MySQL 5.7 or higher
- MySQL JDBC Driver (mysql-connector-j-8.x.x.jar)

## 🚀 Quick Start

### 1. Database Setup
```sql
-- Run the database schema
mysql -u root -p < database/schema.sql
```

### 2. Configure Database Connection
Update `src/com/cakelicious/database/DatabaseConnector.java`:
```java
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Download MySQL JDBC Driver
- Download from [MySQL Official Site](https://dev.mysql.com/downloads/connector/j/)
- Place the JAR file in the `lib/` directory

### 4. Compile and Run

**Windows:**
```bash
# Compile
javac -d build -cp "lib/mysql-connector-j-8.x.x.jar;." src/com/cakelicious/**/*.java

# Run
java -cp "build;lib/mysql-connector-j-8.x.x.jar" com.cakelicious.Server
```

**macOS/Linux:**
```bash
# Compile
javac -d build -cp "lib/mysql-connector-j-8.x.x.jar:." src/com/cakelicious/**/*.java

# Run
java -cp "build:lib/mysql-connector-j-8.x.x.jar" com.cakelicious.Server
```

### 5. Access the Application
Open your browser and navigate to: `http://localhost:8000`

## 👤 Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@example.com | admin123 |
| Customer | customer@example.com | cust123 |
| Staff | staff@example.com | staff123 |

## 📁 Project Structure

```
CakeLicious/
├── database/
│   └── schema.sql              # Database schema and sample data
├── src/com/cakelicious/
│   ├── Server.java             # Main server class
│   ├── database/
│   │   └── DatabaseConnector.java
│   ├── handlers/               # HTTP request handlers
│   │   ├── AuthHandler.java
│   │   ├── ProductHandler.java
│   │   ├── OrderHandler.java
│   │   ├── CategoryHandler.java
│   │   └── FileHandler.java
│   ├── models/                 # Data models
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Order.java
│   │   └── Category.java
│   └── services/               # Business logic
│       ├── UserService.java
│       ├── ProductService.java
│       ├── OrderService.java
│       └── CategoryService.java
├── css/
│   └── style.css               # Modern styling
├── js/
│   ├── auth.js                 # Authentication logic
│   ├── customer.js             # Customer dashboard
│   ├── admin.js                # Admin dashboard
│   └── staff.js                # Staff dashboard
├── index.html                  # Login page
├── customer.html               # Customer dashboard
├── admin.html                  # Admin dashboard
├── staff.html                  # Staff dashboard
└── README.md
```

## 🔧 API Endpoints

### Authentication
- `POST /api/login` - User authentication

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders
- `GET /api/orders` - Get all orders
- `GET /api/orders?userId={id}` - Get orders by user
- `GET /api/orders?status={status}` - Get orders by status
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}` - Update order status

### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create new category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

## 🎯 Key Features Implementation

### 🔐 Security
- Role-based access control
- Session management with localStorage
- Input validation and sanitization
- SQL injection prevention with PreparedStatements

### 📊 Analytics
- Real-time sales tracking
- Inventory monitoring
- Order status analytics
- Revenue calculations

### 🎨 UI/UX Excellence
- Smooth animations and transitions
- Responsive design for all devices
- Intuitive navigation
- Modern color scheme and typography

### 🚀 Performance
- Efficient database queries
- Optimized frontend assets
- Minimal dependencies
- Fast loading times

## 🔮 Future Enhancements

- [ ] Payment gateway integration
- [ ] Email notifications
- [ ] Advanced reporting
- [ ] Mobile app
- [ ] Real-time chat support
- [ ] Inventory forecasting
- [ ] Multi-location support

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Modern UI/UX inspired by leading e-commerce platforms
- Database design following best practices
- RESTful API design principles
- Responsive design patterns

---

**CakeLicious** - Where every celebration becomes sweeter! 🎂✨