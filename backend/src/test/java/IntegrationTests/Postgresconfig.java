package IntegrationTests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
class Postgresconfig {

    @Bean
    @ServiceConnection
    MariaDBContainer mariaDbContainer() {
        return new MariaDBContainer("mariadb:11")
            .withDatabaseName("addresses")
            .withCopyFileToContainer(
                MountableFile.forHostPath("db/addresses.sql"),
                "/docker-entrypoint-initdb.d/01-addresses.sql"
            );
    }
}