package com.example.product.service;

import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final WebClient deliveryWebClient;

    public ProductService(ProductRepository repository, WebClient deliveryWebClient) {
        this.repository = repository;
        this.deliveryWebClient = deliveryWebClient;
    }

    public Flux<Product> findAll() {
        return repository.findAll();
    }

    public Mono<Product> create(Product product) {
        return repository.save(product)
                .flatMap(saved ->
                        deliveryWebClient.post()
                                .uri("/delivery")
                                .bodyValue(Map.of(
                                        "productId", saved.getId(),
                                        "address", saved.getAddress()
                                ))
                                .retrieve()
                                .bodyToMono(Void.class)
                                .thenReturn(saved)
                )
                // если delivery упадёт — можно .onErrorResume(...) или .onErrorContinue(...)
                ;
    }

    // Пример другого метода
    public Flux<Product> findByPriceRange(double min, double max) {
        return repository.findByPriceBetween(min, max);
    }
}