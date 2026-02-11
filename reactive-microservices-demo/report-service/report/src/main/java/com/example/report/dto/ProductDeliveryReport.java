package com.example.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeliveryReport {

    private Long id;
    private String name;
    private double price;
    private String deliveryStatus;

    public static ProductDeliveryReport notFound(Long id) {
        return new ProductDeliveryReport(id, "—", 0.0, "PRODUCT_NOT_FOUND");
    }
}