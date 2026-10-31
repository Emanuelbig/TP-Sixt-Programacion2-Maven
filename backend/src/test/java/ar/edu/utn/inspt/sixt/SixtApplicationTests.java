package ar.edu.utn.inspt.sixt;

import ar.edu.utn.inspt.sixt.modelo.TipoModelo;
import ar.edu.utn.inspt.sixt.repositorio.TipoModeloRepositorio;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:sixt-test;DB_CLOSE_DELAY=-1",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class SixtApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TipoModeloRepositorio tipoModeloRepositorio;

	@Test
	void contextLoads() {
	}

	@Test
	void hibernateCreaLasTablas() {
		Integer cantidadTablas = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
						+ "WHERE TABLE_SCHEMA = 'PUBLIC' "
						+ "AND TABLE_NAME IN ('RESERVA', 'MODELO', 'TIPO_MODELO', 'OFICINA', 'PERSONA', 'ROL', 'VEHICULO')",
				Integer.class);

		assertThat(cantidadTablas).isEqualTo(7);
	}

	@Test
	void tipoModeloSePuedeGuardarYLeer() {
		TipoModelo tipoModelo = new TipoModelo();
		tipoModelo.setNombre("Sedan");
		tipoModelo.setPrecio(new BigDecimal("50.00"));

		TipoModelo guardado = tipoModeloRepositorio.save(tipoModelo);

		assertThat(guardado.getId()).isNotNull();
		assertThat(tipoModeloRepositorio.findById(guardado.getId()))
				.isPresent()
				.get()
				.extracting(TipoModelo::getNombre)
				.isEqualTo("Sedan");
	}

}
