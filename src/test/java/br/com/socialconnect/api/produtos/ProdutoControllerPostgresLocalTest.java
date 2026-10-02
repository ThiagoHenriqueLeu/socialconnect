package br.com.socialconnect.api.produtos;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Banco exclusivo de testes: nunca aponta para o banco usado pela aplicação. */
@EnabledIfEnvironmentVariable(named = "RUN_LOCAL_POSTGRES_TESTS", matches = "true")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/socialconnect_a1_test}",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.datasource.username=${DB_USERNAME:postgres}",
    "spring.datasource.password=${DB_PASSWORD}",
    "spring.flyway.clean-disabled=true"
})
@ActiveProfiles("test")
class ProdutoControllerPostgresLocalTest extends ProdutoHttpContract {}
