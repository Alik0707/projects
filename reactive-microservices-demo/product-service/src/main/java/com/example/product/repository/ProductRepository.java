package com.example.product.repository;

import com.example.product.model.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends R2dbcRepository<Product, Long> {

    // пример кастомного метода
    Flux<Product> findByPriceBetween(double min, double max);
}