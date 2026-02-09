-- Script para agregar la columna image_url a la tabla products si no existe

-- Verifica si la columna existe antes de agregarla
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'products'
        AND column_name = 'image_url'
    ) THEN
        ALTER TABLE products ADD COLUMN image_url VARCHAR(500);
        RAISE NOTICE 'Columna image_url agregada exitosamente';
    ELSE
        RAISE NOTICE 'La columna image_url ya existe';
    END IF;
END $$;
