# 🛠️ TechFlow - Backend Management System

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

**TechFlow** es un sistema robusto de gestión para centros de reparación y ensamble de computadoras. Este Backend proporciona una API RESTful segura y escalable para administrar inventarios, órdenes de servicio, facturación automática y seguimiento de reparaciones en tiempo real.

---

## 🚀 Características Principales

### 🔐 Seguridad & Usuarios
* **Autenticación JWT:** Sistema seguro basado en JSON Web Tokens.
* **Roles:** Gestión diferenciada para Administradores, Técnicos y Clientes.
* **CORS Configurado:** Listo para integración con Frontend (React/Angular).

### 📦 Gestión de Inventario Inteligente
* **Control de Stock:** Descuento automático de inventario al crear órdenes.
* **Borrado Lógico (Soft Delete):** Los productos eliminados no desaparecen de la base de datos, manteniendo la integridad histórica de las órdenes pasadas.
* **Resurrección de Datos:** Lógica inteligente que reactiva productos "borrados" si se intenta registrar un SKU duplicado.
* **Alertas de Stock Bajo:** Identificación automática de productos que requieren reabastecimiento.

### 🧾 Órdenes de Servicio & Facturación
* **Cálculo Automático de Costos:** Suma dinámica de `Mano de Obra` + `(Precio Repuesto × Cantidad)`.
* **Máquina de Estados:** Flujo de trabajo controlado: `PENDIENTE` → `DIAGNOSTICO` → `EN_ESPERA_REPUESTO` → `REPARADO` → `ENTREGADO`.
* **Transacciones Atómicas:** Garantía de integridad de datos (ACID) en la creación de órdenes.

### 🕵️‍♂️ Rastreo Público (Tracking)
* **Portal de Cliente:** Endpoint público accesible mediante `TrackingCode` (UUID) para que los clientes consulten el estado de su equipo sin iniciar sesión.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3.4.1
* **Base de Datos:** MySQL
* **Seguridad:** Spring Security + JJWT
* **Documentación:** SpringDoc OpenAPI (Swagger UI)
* **Utilidades:** Lombok, Maven

---

## 📖 Documentación de la API (Swagger)

El proyecto incluye documentación automática e interactiva. Una vez iniciado el servidor, accede a:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Desde allí podrás probar todos los endpoints (`Login`, `Crear Orden`, `Listar Productos`) directamente.

---

## ⚡ Instalación y Ejecución

### Prerrequisitos
* JDK 21 instalado.
* MySQL Server corriendo.
* Maven.

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/techflow-backend.git](https://github.com/tu-usuario/techflow-backend.git)
    cd techflow-backend
    ```

2.  **Configurar Base de Datos:**
    Abre `src/main/resources/application.properties` y ajusta tus credenciales:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/techflow_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```

3.  **Ejecutar el proyecto:**
    ```bash
    mvn spring-boot:run
    ```

---

## 📡 Endpoints Clave

| Método | Endpoint | Descripción | Auth Requerida |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Iniciar sesión y obtener Token | ❌ No |
| `GET` | `/api/products` | Listar productos activos | ✅ Sí (Bearer Token) |
| `POST` | `/api/orders` | Crear orden (con facturación auto) | ✅ Sí (Bearer Token) |
| `PATCH`| `/api/orders/{id}/status` | Cambiar estado (ej. a REPARADO) | ✅ Sí (Bearer Token) |
| `GET` | `/api/orders/track/{code}` | Rastreo público para clientes | ❌ No |

---

Hecho por drahcirok
