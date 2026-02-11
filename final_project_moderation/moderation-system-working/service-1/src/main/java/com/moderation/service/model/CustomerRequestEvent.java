package com.moderation.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestEvent {
    private String eventId;
    private String requestId;
    private String customerId;
    private String category; // TECHNICAL, BILLING, COMPLAINT, GENERAL
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private String description;
    private LocalDateTime timestamp;
    private String source; // WEB, MOBILE, EMAIL, PHONE
}
