package com.moderation.service.kafka;

import com.moderation.service.model.CustomerRequestEvent;
import com.moderation.service.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestEventConsumer {
    
    private final ModerationService moderationService;
    
    @KafkaListener(
        topics = "${kafka.topics.input}",
        groupId = "${spring.kafka.consumer.group-id}",
        concurrency = "3"
    )
    public void consumeRequestEvent(
            @Payload CustomerRequestEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Received event from Kafka: eventId={}, partition={}, offset={}", 
            event.getEventId(), partition, offset);
        
        try {
            moderationService.processRequest(event);
            

            acknowledgment.acknowledge();
            log.debug("Message acknowledged: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Error processing event: eventId={}", event.getEventId(), e);
        }
    }
}
