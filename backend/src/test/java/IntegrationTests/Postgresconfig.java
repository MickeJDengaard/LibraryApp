package IntegrationTests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
class Postgresconfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return (PostgreSQLContainer) new PostgreSQLContainer("postgres:16")
                .withDatabaseName("library")
                .withCopyFileToContainer(
                        MountableFile.forHostPath("../db/schema.sql"),
                        "/docker-entrypoint-initdb.d/01-schema.sql")
                .withCopyFileToContainer(
                        MountableFile.forHostPath("../db/seed.sql"),
                        "/docker-entrypoint-initdb.d/02-seed.sql");
    }
}