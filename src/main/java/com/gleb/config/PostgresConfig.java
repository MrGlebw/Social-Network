package com.gleb.config;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class PostgresConfig  {

    ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:postgresql://localhost:5432/userDb");
    Publisher<? extends Connection> connectionPublisher = connectionFactory.create();

}
