-- MySQL 8.4. Ejecutar sobre la base existente ANTES de iniciar el backend nuevo.
-- Detener el backend durante la migracion. Se conservan las columnas antiguas
-- como respaldo; las nuevas reservas dejan esos campos en NULL.
CREATE TABLE IF NOT EXISTS detalle_reserva (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    vehiculo_id BIGINT NOT NULL,
    patente VARCHAR(255),
    color VARCHAR(255),
    anio INT NOT NULL,
    litros_nafta INT NOT NULL,
    precio_alquiler DECIMAL(12,2) NOT NULL,
    CONSTRAINT uk_detalle_reserva_vehiculo UNIQUE (reserva_id, vehiculo_id),
    CONSTRAINT fk_detalle_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id),
    CONSTRAINT fk_detalle_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(id)
) ENGINE=InnoDB;

-- Cada reserva anterior se convierte en una reserva con un detalle.
-- Conserva el precio efectivamente cobrado, sin recalcular tarifas historicas.
INSERT INTO detalle_reserva (reserva_id, vehiculo_id, patente, color, anio, litros_nafta, precio_alquiler)
SELECT r.id, r.vehiculo_id, r.patente, r.color, r.anio, r.litros_nafta, r.precio_total
FROM reserva r
WHERE r.vehiculo_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM detalle_reserva d WHERE d.reserva_id = r.id);

ALTER TABLE reserva MODIFY COLUMN vehiculo_id BIGINT NULL;
ALTER TABLE reserva MODIFY COLUMN anio INT NULL;
ALTER TABLE reserva MODIFY COLUMN litros_nafta INT NULL;
