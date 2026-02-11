package com.example.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @PostMapping
    public ResponseEntity<String> createDelivery(
            @RequestBody DeliveryRequest request
    ) {
        return ResponseEntity.ok(
                "Delivery created for product " + request.getProductId()
                        + " to address " + request.getAddress()
        );
    }

}
