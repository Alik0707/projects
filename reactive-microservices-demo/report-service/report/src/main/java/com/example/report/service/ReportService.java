package com.example.report.service;

import com.example.report.dto.ProductDeliveryReport;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ReportService {

    private final WebClient productClient;

    public ReportService(WebClient productWebClient) {
        this.productClient = productWebClient;
    }

    public Flux<ProductDeliveryReport> getDeliveryReport(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(ids)
                .flatMap(this::getProductWithFakeStatus)
                .sort((a, b) -> Long.compare(a.getId(), b.getId()));
    }

    private Mono<ProductDeliveryReport> getProductWithFakeStatus(Long id) {
        return productClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .map(p -> new ProductDeliveryReport(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        "PENDING"
                ))
                .onErrorReturn(ProductDeliveryReport.notFound(id));
    }

    // Вспомогательный класс для десериализации
    private static class ProductDto {
        private Long id;
        private String name;
        private double price;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
}