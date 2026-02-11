package com.moderation.service.rules;

import com.moderation.service.model.CustomerRequestEvent;
import com.moderation.service.model.EnrichmentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ModerationRules {
    
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(18, 0);
    private static final List<String> WORK_TIME_RESTRICTED_CATEGORIES = 
        Arrays.asList("TECHNICAL", "BILLING");
    

    public ModerationResult checkActiveRequests(CustomerRequestEvent event, EnrichmentData enrichmentData) {
        if (enrichmentData == null || enrichmentData.getActiveRequests() == null) {
            return ModerationResult.approved();
        }
        
        boolean hasSameCategoryRequest = enrichmentData.getActiveRequests().stream()
                .anyMatch(activeRequest -> 
                    event.getCategory().equals(activeRequest.getCategory()) &&
                    "ACTIVE".equals(activeRequest.getStatus())
                );
        
        if (hasSameCategoryRequest) {
            log.info("Request rejected: active request exists for category: {}, customerId: {}", 
                event.getCategory(), event.getCustomerId());
            return ModerationResult.rejected(
                "Active request already exists for category: " + event.getCategory()
            );
        }
        
        return ModerationResult.approved();
    }
    public ModerationResult checkWorkingHours(CustomerRequestEvent event) {
        if (!WORK_TIME_RESTRICTED_CATEGORIES.contains(event.getCategory())) {
            return ModerationResult.approved();
        }
        
        LocalDateTime timestamp = event.getTimestamp();
        DayOfWeek dayOfWeek = timestamp.getDayOfWeek();
        LocalTime time = timestamp.toLocalTime();
        
        // Проверяем что это рабочий день (не суббота и не воскресенье)
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        
        // Проверяем время
        boolean isWorkingHours = !time.isBefore(WORK_START) && !time.isAfter(WORK_END);
        
        if (isWeekend || !isWorkingHours) {
            log.info("Request rejected: outside working hours. Category: {}, timestamp: {}", 
                event.getCategory(), timestamp);
            return ModerationResult.rejected(
                "Request for category " + event.getCategory() + 
                " received outside working hours (Mon-Fri 09:00-18:00)"
            );
        }
        
        return ModerationResult.approved();
    }
    

    public ModerationResult checkCustomerBlocked(EnrichmentData enrichmentData) {
        if (enrichmentData != null && 
            enrichmentData.getCustomerInfo() != null && 
            enrichmentData.getCustomerInfo().isBlocked()) {
            
            log.info("Request rejected: customer is blocked. CustomerId: {}", 
                enrichmentData.getCustomerId());
            return ModerationResult.rejected("Customer is blocked");
        }
        
        return ModerationResult.approved();
    }
    

    public static class ModerationResult {
        private final boolean approved;
        private final String reason;
        
        private ModerationResult(boolean approved, String reason) {
            this.approved = approved;
            this.reason = reason;
        }
        
        public static ModerationResult approved() {
            return new ModerationResult(true, null);
        }
        
        public static ModerationResult rejected(String reason) {
            return new ModerationResult(false, reason);
        }
        
        public boolean isApproved() {
            return approved;
        }
        
        public String getReason() {
            return reason;
        }
    }
}
