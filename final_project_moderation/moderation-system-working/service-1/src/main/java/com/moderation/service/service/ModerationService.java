package com.moderation.service.service;

import com.moderation.service.client.EnrichmentServiceClient;
import com.moderation.service.model.CustomerRequestEvent;
import com.moderation.service.model.EnrichmentData;
import com.moderation.service.model.ModeratedRequestEvent;
import com.moderation.service.rules.ModerationRules;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationService {
    
    private final IdempotencyService idempotencyService;
    private final EnrichmentServiceClient enrichmentServiceClient;
    private final ModerationRules moderationRules;
    private final KafkaTemplate<String, ModeratedRequestEvent> kafkaTemplate;
    
    private static final String OUTPUT_TOPIC = "moderated-requests";
    
    public void processRequest(CustomerRequestEvent event) {
        log.info("Processing request: eventId={}, requestId={}, customerId={}",
                event.getEventId(), event.getRequestId(), event.getCustomerId());

        // 1) Если уже финально обработано — пропускаем
        if (idempotencyService.isEventProcessed(event.getEventId())) {
            log.warn("Event already processed, skipping: eventId={}", event.getEventId());
            return;
        }

        // 2) Берём lock "в работе" (защита от параллельной обработки)
        if (!idempotencyService.tryStartProcessing(event.getEventId())) {
            log.warn("Event is already being processed, skipping: eventId={}", event.getEventId());
            return;
        }

        try {
            // Получение обогащенных данных от Service-2
            EnrichmentData enrichmentData = enrichmentServiceClient.getEnrichmentData(
                    event.getCustomerId(),
                    event.getRequestId()
            );

            // Применение правил модерации
            ModerationRules.ModerationResult result = applyModerationRules(event, enrichmentData);

            if (result.isApproved()) {
                // Публикация в Topic-2
                publishModeratedEvent(event, enrichmentData);
                log.info("Request approved and published: eventId={}", event.getEventId());
            } else {
                log.info("Request rejected: eventId={}, reason={}",
                        event.getEventId(), result.getReason());
            }

            // Финально помечаем как обработанное (и approve, и reject)
            idempotencyService.markEventAsProcessed(event.getEventId());

        } catch (Exception e) {
            // Снимаем lock и пробрасываем исключение наверх,
            // чтобы consumer НЕ ack-нул сообщение и Kafka сделала retry
            idempotencyService.clearProcessingLock(event.getEventId());
            log.error("Error processing request: eventId={}", event.getEventId(), e);
            throw e;
        }
    }
    
    private ModerationRules.ModerationResult applyModerationRules(
            CustomerRequestEvent event, 
            EnrichmentData enrichmentData) {
        
        // Правило 1: Проверка заблокированного клиента
        ModerationRules.ModerationResult result = moderationRules.checkCustomerBlocked(enrichmentData);
        if (!result.isApproved()) {
            return result;
        }
        
        // Правило 2: Проверка активных обращений той же категории
        result = moderationRules.checkActiveRequests(event, enrichmentData);
        if (!result.isApproved()) {
            return result;
        }
        
        // Правило 3: Проверка рабочего времени
        result = moderationRules.checkWorkingHours(event);
        if (!result.isApproved()) {
            return result;
        }
        
        return ModerationRules.ModerationResult.approved();
    }
    
    private void publishModeratedEvent(CustomerRequestEvent event, EnrichmentData enrichmentData) {
        ModeratedRequestEvent moderatedEvent = ModeratedRequestEvent.builder()
                .eventId(event.getEventId())
                .requestId(event.getRequestId())
                .customerId(event.getCustomerId())
                .category(event.getCategory())
                .priority(event.getPriority())
                .description(event.getDescription())
                .timestamp(event.getTimestamp())
                .source(event.getSource())
                .customerTier(enrichmentData != null && enrichmentData.getCustomerInfo() != null 
                    ? enrichmentData.getCustomerInfo().getTier() : null)
                .preferredLanguage(enrichmentData != null && enrichmentData.getCustomerInfo() != null 
                    ? enrichmentData.getCustomerInfo().getPreferredLanguage() : null)
                .moderationStatus("APPROVED")
                .moderatedAt(LocalDateTime.now())
                .moderationReason("Passed all moderation rules")
                .build();
        
        kafkaTemplate.send(OUTPUT_TOPIC, event.getRequestId(), moderatedEvent);
        log.info("Published moderated event to topic: {}, requestId: {}", 
            OUTPUT_TOPIC, event.getRequestId());
    }
}
