-- ============================================
-- SETUP DE BASE DE DATOS - TechFlow
-- ============================================
-- Ejecutar este archivo para configurar la base de datos
-- correctamente antes de iniciar el backend.
--
-- COMANDOS:
-- 1. Crear la base de datos:
--    mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS techflow_db"
--
-- 2. Ejecutar este script:
--    mysql -u root -p techflow_db < database/setup.sql
-- ============================================

-- 1. Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS techflow_db;
USE techflow_db;

-- 2. Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla de productos (inventario)
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock INT NOT NULL DEFAULT 0,
    min_stock INT NOT NULL DEFAULT 5,
    category VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Tabla de órdenes de servicio
CREATE TABLE IF NOT EXISTS service_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    technician_note TEXT,
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    labor_cost DECIMAL(10,2) DEFAULT 0,
    total_cost DECIMAL(10,2) DEFAULT 0,

    -- Datos del cliente (para órdenes sin cuenta)
    client_email VARCHAR(255),
    client_name VARCHAR(255),
    client_phone VARCHAR(50),

    -- Relaciones
    client_id BIGINT NULL,
    technician_id BIGINT,

    -- Valoración
    rating INT,
    rating_comment TEXT,
    rated_at TIMESTAMP NULL,

    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,

    FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (technician_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Tabla de items de orden (repuestos usados)
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (service_order_id) REFERENCES service_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================
-- MIGRACIONES (ejecutar si la BD ya existe)
-- ============================================

-- Permitir que client_id sea NULL
ALTER TABLE service_orders MODIFY COLUMN client_id BIGINT NULL;

-- Agregar columnas si no existen (MySQL 8.0+)
-- Si tienes MySQL 5.7, comenta estas líneas y ejecuta los ALTER TABLE manualmente

-- Datos del cliente sin cuenta
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS client_email VARCHAR(255);
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS client_name VARCHAR(255);
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS client_phone VARCHAR(50);

-- Valoración
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS rating INT;
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS rating_comment TEXT;
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS rated_at TIMESTAMP NULL;

-- Nota del técnico
ALTER TABLE service_orders ADD COLUMN IF NOT EXISTS technician_note TEXT;

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Usuario admin (contraseña: admin123)
INSERT IGNORE INTO users (email, password, name, role, active) VALUES
('admin@techflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3s4TNwD6gK/xnKPGwMK2', 'Administrador', 'ADMIN', true);

-- Usuario técnico de prueba (contraseña: admin123)
INSERT IGNORE INTO users (email, password, name, role, active) VALUES
('tecnico@techflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3s4TNwD6gK/xnKPGwMK2', 'Técnico Demo', 'TECNICO', true);

-- Productos de ejemplo
INSERT IGNORE INTO products (sku, name, description, price, stock, min_stock, category) VALUES
('RAM-8GB-DDR4', 'Memoria RAM 8GB DDR4', 'Memoria RAM DDR4 2666MHz', 35.00, 15, 5, 'Memorias'),
('SSD-256GB', 'SSD 256GB SATA', 'Disco SSD 256GB SATA 2.5"', 45.00, 10, 3, 'Almacenamiento'),
('SSD-512GB', 'SSD 512GB SATA', 'Disco SSD 512GB SATA 2.5"', 65.00, 8, 3, 'Almacenamiento'),
('HDD-1TB', 'Disco Duro 1TB', 'Disco HDD 1TB 7200RPM', 55.00, 12, 5, 'Almacenamiento'),
('PASTA-TERMICA', 'Pasta Térmica', 'Pasta térmica de alta calidad', 8.00, 25, 10, 'Accesorios'),
('VENTILADOR-CPU', 'Ventilador CPU', 'Cooler para procesador universal', 18.00, 10, 5, 'Refrigeración');

SELECT '✅ Base de datos configurada correctamente!' AS mensaje;
