package com.sixt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MigracionPermisosTest {
    @Test
    void convierteEnumConservandoPermisosYAdmiteNuevosValores() throws Exception {
        try (var conexion = DriverManager.getConnection("jdbc:h2:mem:migracion-permisos;MODE=MySQL", "sa", "");
             var sql = conexion.createStatement()) {
            sql.execute("CREATE TABLE rol_permiso (rol_id BIGINT NOT NULL, permiso ENUM('ADMINISTRAR','ALQUILAR','VENDER'))");
            sql.execute("INSERT INTO rol_permiso VALUES (1, 'ADMINISTRAR'), (2, 'VENDER'), (3, 'ALQUILAR')");
            Path archivo = Path.of("../docs/migracion-permisos-varchar.sql");
            if (!Files.exists(archivo)) archivo = Path.of("docs/migracion-permisos-varchar.sql");
            String script = Files.readString(archivo).replaceAll("(?m)--.*$", "");
            sql.execute(script);
            sql.execute("INSERT INTO rol_permiso VALUES (2, 'ADMINISTRAR_CLIENTES')");
            sql.execute(script);
            try (var resultado = sql.executeQuery("SELECT COUNT(*) FROM rol_permiso")) {
                assertThat(resultado.next()).isTrue();
                assertThat(resultado.getInt(1)).isEqualTo(4);
            }
            try (var resultado = sql.executeQuery("SELECT permiso FROM rol_permiso WHERE rol_id=1")) {
                assertThat(resultado.next()).isTrue();
                assertThat(resultado.getString(1)).isEqualTo("ADMINISTRAR");
            }
        }
    }
}
