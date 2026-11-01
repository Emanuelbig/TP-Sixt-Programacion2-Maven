-- MySQL: ampliar el almacenamiento de permisos conservando sus valores.
-- Ejecutar antes de actualizar una base que conserva el ENUM original.
-- Puede repetirse: no elimina filas ni modifica los permisos asignados.
ALTER TABLE rol_permiso MODIFY COLUMN permiso VARCHAR(64) NULL;
