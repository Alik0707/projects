package com.moderation.enrichment.controller;

import com.moderation.enrichment.dto.EnrichmentResponse;
import com.moderation.enrichment.service.EnrichmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/enrichment")
@RequiredArgsConstructor
public class EnrichmentController {
    
    private final EnrichmentService enrichmentService;
    
    @GetMapping("/{customerId}")
    public ResponseEntity<EnrichmentResponse> getEnrichmentData(
            @PathVariable String customerId,
            @RequestParam String requestId) {
        
        log.info("Received enrichment request for customerId: {}, requestId: {}", customerId, requestId);
        
        EnrichmentResponse response = enrichmentService.getEnrichmentData(customerId, requestId);
        
        return ResponseEntity.ok(response);
    }
    
    // Вспомогательные эндпоинты для управления данными
    @PostMapping("/customer-info")
    public ResponseEntity<Void> saveCustomerInfo(
            @RequestParam String customerId,
            @RequestBody EnrichmentResponse.CustomerInfo customerInfo) {
        enrichmentService.saveCustomerInfo(customerId, customerInfo);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/active-request")
    public ResponseEntity<Void> saveActiveRequest(
            @RequestParam String customerId,
            @RequestBody EnrichmentResponse.ActiveRequest activeRequest) {
        enrichmentService.saveActiveRequest(customerId, activeRequest);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/active-request/{customerId}/{requestId}")
    public ResponseEntity<Void> removeActiveRequest(
            @PathVariable String customerId,
            @PathVariable String requestId) {
        enrichmentService.removeActiveRequest(customerId, requestId);
        return ResponseEntity.ok().build();
    }
}
