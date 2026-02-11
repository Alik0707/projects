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
public class ModeratedRequestEvent {
    private String eventId;
    private String requestId;
    private String customerId;
    private String category;
    private String priority;
    private String description;
    private LocalDateTime timestamp;
    private String source;
    
    // Enrichment data
    private String customerTier;
    private String preferredLanguage;
    
    // Moderation metadata
    private String moderationStatus; // APPROVED
    private LocalDateTime moderatedAt;
    private String moderationReason;
}
