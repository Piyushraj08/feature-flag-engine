package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class FlagEventConsumer {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "flag-events",
            groupId = "flag-audit-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(String message) {
        try {
            FlagEvent event = objectMapper.readValue(message, FlagEvent.class);

            // 1. Audit log save
            AuditLog log = new AuditLog();
            log.setFlagId(event.getFlagId());
            log.setFlagName(event.getFlagName());
            log.setEventType(event.getEventType());
            log.setActor(event.getActor());
            log.setTimestamp(event.getTimestamp());
            auditLogRepository.save(log);

            // Important line
            SseController.broadcast("flag-update", event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}