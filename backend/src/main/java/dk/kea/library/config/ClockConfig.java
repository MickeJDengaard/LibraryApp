package dk.kea.library.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * All code gets "today" from this Clock (LocalDate.now(clock)),
 * so tests can replace it with Clock.fixed(...).
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
