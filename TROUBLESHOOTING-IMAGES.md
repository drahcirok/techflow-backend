# Troubleshooting - Sistema de Imágenes de Productos

## Problema: Las imágenes no se muestran en la tienda

### Paso 1: Verificar que la columna existe en la base de datos

Ejecuta este comando SQL en tu base de datos:

```sql
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'products';
```

Debes ver una columna llamada `image_url` de tipo `VARCHAR(500)`.

Si NO aparece, ejecuta:
```sql
ALTER TABLE products ADD COLUMN image_url VARCHAR(500);
```

O usa el script: `database/add-image-url-column.sql`

### Paso 2: Crear el directorio de uploads

En la raíz del proyecto backend, crea el directorio:
```bash
mkdir -p uploads/products
```

### Paso 3: Verificar permisos del directorio

```bash
chmod 755 uploads
chmod 755 uploads/products
```

### Paso 4: Reiniciar el backend

Detén y vuelve a iniciar el backend para que:
- Hibernate detecte la nueva columna
- Se cargue la configuración de archivos estáticos

### Paso 5: Verificar en consola del navegador

1. Abre las Herramientas de Desarrollador (F12)
2. Ve a la pestaña "Console"
3. Deberías ver logs como:
   - `📦 Enviando producto: {...}`
   - `✅ Producto creado/actualizado: {...}`
   - `📋 Productos recibidos del backend: [...]`
   - `🖼️ Construyendo URL de imagen: {...}`

### Paso 6: Verificar que la imagen se guardó

1. Sube una imagen en Admin > Inventario
2. Verifica que se creó el archivo en: `backend/uploads/products/`
3. En consola debería aparecer algo como:
   ```
   📦 Enviando producto: {
     imageUrl: "/uploads/products/abc123.jpg",
     ...
   }
   ```

### Paso 7: Verificar que la URL es correcta

En consola deberías ver:
```
🖼️ Construyendo URL de imagen: {
  imageUrl: "/uploads/products/abc123.jpg",
  baseUrl: "http://localhost:8080",
  fullUrl: "http://localhost:8080/uploads/products/abc123.jpg"
}
```

### Paso 8: Probar la URL directamente

Copia la `fullUrl` de la consola y ábrela en una nueva pestaña.
Deberías ver la imagen. Si no la ves:
- Verifica que el archivo existe en `uploads/products/`
- Verifica que el backend está corriendo
- Verifica la configuración de CORS en `WebConfig.java`

## Comandos útiles

### Ver productos en la base de datos:
```sql
SELECT id, sku, name, image_url FROM products;
```

### Actualizar manualmente una imagen:
```sql
UPDATE products
SET image_url = '/uploads/products/test.jpg'
WHERE sku = 'RAM-CORE-8GB';
```

### Ver archivos subidos:
```bash
ls -la uploads/products/
```

## URLs de ejemplo correctas

- Relativa: `/uploads/products/abc123.jpg`
- Completa: `http://localhost:8080/uploads/products/abc123.jpg`
