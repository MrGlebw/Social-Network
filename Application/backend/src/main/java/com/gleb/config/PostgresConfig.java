package com.gleb.config;


import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;


@Configuration
@EnableR2dbcAuditing
public class PostgresConfig extends AbstractR2dbcConfiguration {


        @Bean
        @Override
        public @NotNull ConnectionFactory connectionFactory() {
            final PostgresqlConnectionConfiguration connectionConfig = PostgresqlConnectionConfiguration.builder()
                    .database("userDb")
                    .host("localhost")
                    .password("gleb123")
                    .port(5432)
                    .username("gleb")
                    .build();

            return new PostgresqlConnectionFactory(connectionConfig);
        }



}
