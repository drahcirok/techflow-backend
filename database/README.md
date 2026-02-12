# Base de Datos - TechFlow

## Configuración Inicial

### 1. Requisitos
- MySQL 8.0 o superior
- Tener MySQL en el PATH o usar MySQL Workbench

### 2. Crear la base de datos

```bash
# Opción A: Desde terminal
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS techflow_db"

# Opción B: Desde MySQL Workbench
# Ejecutar: CREATE DATABASE IF NOT EXISTS techflow_db;
```

### 3. Ejecutar el script de setup

```bash
# Desde la carpeta raíz del backend
mysql -u root -p techflow_db < database/setup.sql
```

O copiar el contenido de `setup.sql` y ejecutarlo en MySQL Workbench.

### 4. Configurar credenciales

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.password=TU_CONTRASEÑA_DE_MYSQL
```

### 5. Iniciar el backend

```bash
./mvnw spring-boot:run
```

## Usuarios de Prueba

| Email | Contraseña | Rol |
|-------|------------|-----|
| admin@techflow.com | admin123 | ADMIN |
| tecnico@techflow.com | admin123 | TECNICO |

## Solución de Problemas

### Error: Column 'client_id' cannot be null

Ejecutar en MySQL:
```sql
ALTER TABLE service_orders MODIFY COLUMN client_id BIGINT NULL;
```

### Error: Unknown column 'client_email'

Ejecutar en MySQL:
```sql
ALTER TABLE service_orders ADD COLUMN client_email VARCHAR(255);
ALTER TABLE service_orders ADD COLUMN client_name VARCHAR(255);
ALTER TABLE service_orders ADD COLUMN client_phone VARCHAR(50);
```

### Reiniciar la base de datos completamente

⚠️ **CUIDADO: Esto borra todos los datos**

```sql
DROP DATABASE techflow_db;
CREATE DATABASE techflow_db;
```

Luego ejecutar `setup.sql` nuevamente.
