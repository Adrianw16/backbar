package io.github.adrianw16.backbar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers // starts/stops @Container fields around the test class
@SpringBootTest // full context. Makes Flyway + Hibernate run for real
public class SchemaMigrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    JdbcTemplate jdbcTemplate; // autoconfigured - spring-boot-starter-data-jpa pulls in spring-jdbc

    @Test
    void flywayAppliedExactlyOneMigration(){
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from flyway_schema_history", Integer.class);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void productHasFiveUnitColumns(){
        List<String> columns = jdbcTemplate.queryForList(
            "SELECT column_name FROM information_schema.columns WHERE table_name = 'product'",
            String.class);

        assertThat(columns).contains(
                "purchase_unit_name",
                "each_per_purchase",
                "each_unit_name",
                "base_per_each",
                "base_unit_name"
        );

    }
}
