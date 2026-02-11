package com.example.product.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
@Component
public class DatabaseInitializer {

    private final DatabaseClient client;

    public DatabaseInitializer(ConnectionFactory connectionFactory) {
        this.client = DatabaseClient.create(connectionFactory);
    }

    @PostConstruct
    public void init() {
        client.sql("""
                CREATE TABLE IF NOT EXISTS products (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255),
                    price DOUBLE,
                    address VARCHAR(255)
                )
                """)
                .then()
                .subscribe();
    }
}
