/*
  # CakeLicious Database Schema
  
  1. Database Structure
    - Users table with authentication
    - Products and categories
    - Orders and order items
    - Inventory management
    - Reviews and ratings
  
  2. Sample Data
    - Demo users for all roles
    - Sample products with categories
    - Test orders and reviews
*/

CREATE DATABASE IF NOT EXISTS cakelicious_db;
USE cakelicious_db;

-- Users table with enhanced fields
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('customer', 'staff', 'admin') NOT NULL,
    address TEXT,
    date_of_birth DATE,
    profile_image VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table with enhanced fields
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    category_id INT,
    stock_quantity INT DEFAULT 0,
    min_stock_level INT DEFAULT 5,
    weight VARCHAR(50),
    serves INT,
    preparation_time INT DEFAULT 24, -- hours
    ingredients TEXT,
    allergens TEXT,
    is_customizable BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders table with enhanced tracking
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('pending', 'confirmed', 'preparing', 'baking', 'ready', 'out_for_delivery', 'delivered', 'cancelled') DEFAULT 'pending',
    payment_status ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    delivery_date DATE,
    delivery_time TIME,
    special_instructions TEXT,
    assigned_staff_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (assigned_staff_id) REFERENCES users(id)
);

-- Order items table
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    customizations JSON,
    special_requests TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Reviews table
CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    order_id INT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    is_approved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Cart table for persistent shopping cart
CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    customizations JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY unique_user_product (user_id, product_id)
);

-- Inventory alerts table
CREATE TABLE inventory_alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    alert_type ENUM('low_stock', 'out_of_stock') NOT NULL,
    message TEXT,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Coupons table
CREATE TABLE coupons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed') NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    min_order_amount DECIMAL(10, 2) DEFAULT 0.00,
    max_discount_amount DECIMAL(10, 2),
    usage_limit INT DEFAULT 1,
    used_count INT DEFAULT 0,
    valid_from DATE,
    valid_until DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert demo users
INSERT INTO users (email, password, full_name, phone, role, address) VALUES
('admin@example.com', 'admin123', 'Admin User', '+1234567890', 'admin', '123 Admin Street, City'),
('customer@example.com', 'cust123', 'John Doe', '+1234567891', 'customer', '456 Customer Ave, City'),
('staff@example.com', 'staff123', 'Jane Smith', '+1234567892', 'staff', '789 Staff Road, City'),
('baker@example.com', 'baker123', 'Mike Baker', '+1234567893', 'staff', '321 Baker Lane, City');

-- Insert categories
INSERT INTO categories (name, description, image_url) VALUES
('Birthday Cakes', 'Delicious cakes perfect for birthday celebrations', 'https://images.pexels.com/photos/1721932/pexels-photo-1721932.jpeg'),
('Wedding Cakes', 'Elegant and beautiful cakes for your special day', 'https://images.pexels.com/photos/1126359/pexels-photo-1126359.jpeg'),
('Custom Cakes', 'Personalized cakes designed just for you', 'https://images.pexels.com/photos/1702373/pexels-photo-1702373.jpeg'),
('Cupcakes', 'Individual treats perfect for any occasion', 'https://images.pexels.com/photos/1055272/pexels-photo-1055272.jpeg'),
('Seasonal Specials', 'Limited time seasonal cake offerings', 'https://images.pexels.com/photos/291528/pexels-photo-291528.jpeg');

-- Insert sample products
INSERT INTO products (name, description, price, image_url, category_id, stock_quantity, weight, serves, ingredients, allergens, is_customizable, is_featured) VALUES
('Chocolate Truffle Delight', 'Rich and decadent chocolate cake with truffle filling and chocolate ganache', 45.00, 'https://images.pexels.com/photos/1721932/pexels-photo-1721932.jpeg', 1, 15, '2 lbs', 8, 'Chocolate, Eggs, Flour, Butter, Sugar, Cream', 'Contains: Eggs, Dairy, Gluten', TRUE, TRUE),
('Classic Vanilla Dream', 'Light and fluffy vanilla sponge with buttercream frosting', 35.00, 'https://images.pexels.com/photos/1126359/pexels-photo-1126359.jpeg', 1, 20, '1.5 lbs', 6, 'Vanilla, Eggs, Flour, Butter, Sugar', 'Contains: Eggs, Dairy, Gluten', TRUE, FALSE),
('Red Velvet Romance', 'Moist red velvet cake with cream cheese frosting', 40.00, 'https://images.pexels.com/photos/1702373/pexels-photo-1702373.jpeg', 1, 12, '2 lbs', 8, 'Cocoa, Red Food Coloring, Cream Cheese, Eggs, Flour', 'Contains: Eggs, Dairy, Gluten', TRUE, TRUE),
('Three-Tier Wedding Elegance', 'Stunning three-tier wedding cake with elegant decorations', 250.00, 'https://images.pexels.com/photos/1055272/pexels-photo-1055272.jpeg', 2, 5, '8 lbs', 50, 'Multiple flavors available, Fondant, Buttercream', 'Contains: Eggs, Dairy, Gluten', TRUE, TRUE),
('Strawberry Shortcake', 'Fresh strawberries with vanilla sponge and whipped cream', 38.00, 'https://images.pexels.com/photos/291528/pexels-photo-291528.jpeg', 1, 18, '1.8 lbs', 7, 'Fresh Strawberries, Vanilla, Whipped Cream, Sponge', 'Contains: Dairy, Gluten', TRUE, FALSE),
('Chocolate Cupcake Box (12)', 'Dozen of rich chocolate cupcakes with various toppings', 24.00, 'https://images.pexels.com/photos/1998635/pexels-photo-1998635.jpeg', 4, 25, '1 lb', 12, 'Chocolate, Buttercream, Various Toppings', 'Contains: Eggs, Dairy, Gluten', FALSE, FALSE);

-- Insert sample coupons
INSERT INTO coupons (code, description, discount_type, discount_value, min_order_amount, valid_from, valid_until) VALUES
('WELCOME10', 'Welcome discount for new customers', 'percentage', 10.00, 30.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
('BIRTHDAY20', 'Special birthday discount', 'percentage', 20.00, 50.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY)),
('SAVE5', 'Save $5 on your order', 'fixed', 5.00, 25.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY));