package com.example.product.controller;

import com.example.product.model.Product;
import com.example.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<String> test() {
        return Mono.just("Reactive Product Service (WebFlux + R2DBC) is running");
    }

    @GetMapping("/all")
    public Flux<Product> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Mono<Product> create(@RequestBody Product product) {
        return service.create(product);
    }

    // Пример
    @GetMapping("/price-range")
    public Flux<Product> byPrice(@RequestParam double min, @RequestParam double max) {
        return service.findByPriceRange(min, max);
    }
}