package com.gleb.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    public void repairFlyway() {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/userDB", "gleb", "gleb123")
                .load();
        flyway.repair();
    }
}

