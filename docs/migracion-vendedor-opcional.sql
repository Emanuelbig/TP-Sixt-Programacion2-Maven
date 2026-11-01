-- MySQL 8.4. Ejecutar sobre una base existente antes de iniciar el backend nuevo.
-- Las reservas online (hechas por el propio cliente) no tienen vendedor.
-- Hibernate con ddl-auto=update no quita un NOT NULL que ya existe, por eso va a mano.
-- En una base nueva no hace falta: la columna ya se crea aceptando NULL.
ALTER TABLE reserva MODIFY COLUMN vendedor_id BIGINT NULL;
