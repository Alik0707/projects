package com.example.report;

import lombok.Data;

@Data
public class DeliveryRequest {
    private Long productId;
    private String address;
}
