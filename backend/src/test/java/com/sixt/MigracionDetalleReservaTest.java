package com.sixt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MigracionDetalleReservaTest {
    @Test
    void conservaLaReservaAnteriorYPermiteNuevasReservasSinColumnasLegadas() throws Exception {
        try (var conexion = DriverManager.getConnection("jdbc:h2:mem:migracion;MODE=MySQL", "sa", "");
             var sql = conexion.createStatement()) {
            sql.execute("CREATE TABLE vehiculo (id BIGINT PRIMARY KEY)");
            sql.execute("INSERT INTO vehiculo VALUES (1)");
            sql.execute("""
                    CREATE TABLE reserva (id BIGINT PRIMARY KEY, vehiculo_id BIGINT NOT NULL,
                    patente VARCHAR(255), color VARCHAR(255), anio INT NOT NULL,
                    litros_nafta INT NOT NULL, precio_total DECIMAL(12,2))
                    """);
            sql.execute("INSERT INTO reserva VALUES (1, 1, 'APG023', 'violeta', 2010, 20, 30000)");
            Path archivo = Path.of("../docs/migracion-detalle-reserva.sql");
            if (!Files.exists(archivo)) archivo = Path.of("docs/migracion-detalle-reserva.sql");
            String script = Files.readString(archivo).replaceAll("(?m)--.*$", "");
            for (int repeticion = 0; repeticion < 2; repeticion++) {
                for (String sentencia : script.split(";")) {
                    if (!sentencia.isBlank()) sql.execute(sentencia);
                }
            }
            try (var resultado = sql.executeQuery("SELECT * FROM detalle_reserva")) {
                assertThat(resultado.next()).isTrue();
                assertThat(resultado.getLong("reserva_id")).isEqualTo(1);
                assertThat(resultado.getString("patente")).isEqualTo("APG023");
                assertThat(resultado.getInt("litros_nafta")).isEqualTo(20);
                assertThat(resultado.getBigDecimal("precio_alquiler")).isEqualByComparingTo("30000");
                assertThat(resultado.next()).isFalse();
            }
            sql.execute("INSERT INTO reserva (id, precio_total) VALUES (2, 54000)");
        }
    }
}
