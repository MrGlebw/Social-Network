package com.gleb.config;

import com.gleb.data.Username;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

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


    @Bean
    ReactiveAuditorAware<Username> reactiveAuditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername)
                .map(Username::new)
                .switchIfEmpty(Mono.empty());
    }


}
