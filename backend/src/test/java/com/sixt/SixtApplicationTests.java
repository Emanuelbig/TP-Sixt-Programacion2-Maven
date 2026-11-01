package com.sixt;

import com.sixt.modelo.TipoModelo;
import com.sixt.modelo.Permiso;
import com.sixt.dto.NuevaPersona;
import com.sixt.dto.DatosPersona;
import com.sixt.dto.UsuarioLogueado;
import com.sixt.servicio.PersonaServicio;
import com.sixt.repositorio.PersonaRepositorio;
import com.sixt.repositorio.RolRepositorio;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import org.springframework.transaction.annotation.Transactional;
import com.sixt.repositorio.TipoModeloRepositorio;
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
						+ "AND TABLE_NAME IN ('DETALLE_RESERVA', 'RESERVA', 'MODELO', 'TIPO_MODELO', 'OFICINA', 'PERSONA', 'ROL', 'VEHICULO')",
				Integer.class);

		assertThat(cantidadTablas).isEqualTo(8);
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

    @Autowired private PersonaServicio personaServicio;
    @Autowired private PersonaRepositorio personaRepositorio;
    @Autowired private RolRepositorio rolRepositorio;
    @Autowired private EntityManager em;
    @Autowired private Validator validator;

    @Test
    @Transactional
    void guardaYActualizaLaDireccionDelCliente() {
        Long rolId = rolRepositorio.findAll().stream().filter(r -> r.tiene(Permiso.ALQUILAR))
                .findFirst().orElseThrow().getId();
        var creada = personaServicio.crear(new NuevaPersona(45678901, "Cliente prueba",
                "  Av. Corrientes 123, piso 2  ", "direccion@example.com", "1122334455", rolId, "prueba123"));
        Long id = creada.getId();
        em.flush();
        em.clear();
        assertThat(personaRepositorio.findById(id).orElseThrow().getDireccion())
                .isEqualTo("Av. Corrientes 123, piso 2");
        personaServicio.actualizar(id, new DatosPersona(45678901, "Cliente prueba",
                "  Av. Rivadavia 456  ", "direccion@example.com", "1122334455", rolId));
        em.flush();
        em.clear();
        var actualizada = personaRepositorio.findById(id).orElseThrow();
        assertThat(actualizada.getDireccion()).isEqualTo("Av. Rivadavia 456");
        assertThat(UsuarioLogueado.de(actualizada).direccion()).isEqualTo("Av. Rivadavia 456");
    }

    @Test
    void validaDireccionEnAltaYEdicion() {
        for (String direccion : new String[] {null, "", "   ", "x".repeat(501)}) {
            var nueva = new NuevaPersona(45678901, "Cliente", direccion,
                    "direccion@example.com", "1122334455", 1L, "prueba123");
            assertThat(validator.validate(nueva)).anyMatch(v -> v.getPropertyPath().toString().equals("direccion"));
            assertThat(validator.validate(nueva.datos())).anyMatch(v -> v.getPropertyPath().toString().equals("direccion"));
        }
        var valida = new NuevaPersona(45678901, "Cliente", "x".repeat(500),
                "direccion@example.com", "1122334455", 1L, "prueba123");
        assertThat(validator.validate(valida)).isEmpty();
        assertThat(validator.validate(valida.datos())).isEmpty();
    }

}
