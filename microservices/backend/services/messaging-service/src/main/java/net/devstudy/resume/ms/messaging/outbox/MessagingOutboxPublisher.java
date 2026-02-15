package net.devstudy.resume.ms.messaging.outbox;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "app.messaging.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class MessagingOutboxPublisher {

    private static final int MAX_BACKOFF_MULTIPLIER = 10;

    private final MessagingOutboxRepository outboxRepository;
    private final MessagingOutboxProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessagingOutboxPublisher(MessagingOutboxRepository outboxRepository,
            MessagingOutboxProperties properties,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.properties = properties;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.messaging.outbox.poll-interval-ms:1000}")
    @Transactional
    public void relay() {
        Instant now = Instant.now();
        int batchSize = Math.max(1, properties.getBatchSize());
        int maxAttempts = Math.max(1, properties.getMaxAttempts());
        List<MessagingOutboxEvent> batch = outboxRepository.lockNextBatch(now, batchSize, maxAttempts);
        if (batch.isEmpty()) {
            return;
        }
        for (MessagingOutboxEvent event : batch) {
            if (event == null) {
                continue;
            }
            try {
                publish(event);
                markSent(event, now);
            } catch (Exception ex) {
                markFailed(event, ex, now);
            }
            outboxRepository.save(event);
        }
    }

    private void publish(MessagingOutboxEvent event) throws Exception {
        if (event.getTopic() == null || event.getTopic().isBlank()) {
            throw new IllegalArgumentException("Outbox topic is empty");
        }
        if (event.getPayload() == null || event.getPayload().isBlank()) {
            throw new IllegalArgumentException("Outbox payload is empty");
        }
        ProducerRecord<String, String> record = new ProducerRecord<>(
                event.getTopic(),
                event.getEventKey(),
                event.getPayload());
        addHeader(record, KafkaEventHeaders.EVENT_ID, "messaging-outbox:" + event.getId());
        addHeader(record, KafkaEventHeaders.EVENT_TYPE, event.getEventType().name());
        addHeader(record, KafkaEventHeaders.SOURCE_SERVICE, "resume-messaging-service");
        addHeader(record, KafkaEventHeaders.OCCURRED_AT, resolveOccurredAt(event));
        kafkaTemplate.send(record).get();
    }

    private String resolveOccurredAt(MessagingOutboxEvent event) {
        if (event == null || event.getCreatedAt() == null) {
            return Instant.now().toString();
        }
        return event.getCreatedAt().toString();
    }

    private void markSent(MessagingOutboxEvent event, Instant now) {
        event.setStatus(MessagingOutboxStatus.SENT);
        event.setSentAt(now);
        event.setAvailableAt(now);
        event.setLastError(null);
    }

    private void markFailed(MessagingOutboxEvent event, Exception ex, Instant now) {
        int attempts = Math.max(0, event.getAttempts()) + 1;
        event.setAttempts(attempts);
        event.setStatus(MessagingOutboxStatus.ERROR);
        event.setLastError(truncate(ex.getMessage(), 1000));
        long baseDelay = Math.max(100L, properties.getRetryDelayMs());
        long multiplier = Math.min(attempts, MAX_BACKOFF_MULTIPLIER);
        event.setAvailableAt(now.plusMillis(baseDelay * multiplier));
    }

    private void addHeader(ProducerRecord<String, String> record, String key, String value) {
        if (record == null || key == null || value == null || value.isBlank()) {
            return;
        }
        record.headers().add(new RecordHeader(key, value.getBytes(StandardCharsets.UTF_8)));
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
