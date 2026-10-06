package ar.edu.utn.inspt.sixt;

import ar.edu.utn.inspt.sixt.entity.ModelType;
import ar.edu.utn.inspt.sixt.repository.ModelTypeRepository;
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
	private ModelTypeRepository modelTypeRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void hibernateCreatesDomainTables() {
		Integer tableCount = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
						+ "WHERE TABLE_SCHEMA = 'PUBLIC' "
						+ "AND TABLE_NAME IN ('BOOKING', 'MODEL', 'MODEL_TYPE', 'OFFICE', 'PERSON', 'ROLES', 'VEHICLE')",
				Integer.class);

		assertThat(tableCount).isEqualTo(7);
	}

	@Test
	void modelTypeCanBeStoredAndRead() {
		ModelType modelType = new ModelType();
		modelType.setName("Sedan");
		modelType.setPrice(new BigDecimal("50.00"));

		ModelType saved = modelTypeRepository.save(modelType);

		assertThat(saved.getId()).isNotNull();
		assertThat(modelTypeRepository.findById(saved.getId()))
				.isPresent()
				.get()
				.extracting(ModelType::getName)
				.isEqualTo("Sedan");
	}

}
