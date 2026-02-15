package net.devstudy.resume.ms.messaging.outbox;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.devstudy.resume.contracts.messaging.api.event.ReadEvent;
import net.devstudy.resume.contracts.messaging.api.model.MessageItem;

@Service
public class MessagingOutboxService {

    private final MessagingOutboxRepository outboxRepository;
    private final MessagingOutboxProperties properties;
    private final ObjectMapper objectMapper;

    public MessagingOutboxService(MessagingOutboxRepository outboxRepository,
            MessagingOutboxProperties properties,
            ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public void enqueueMessageSent(Long conversationId, MessageItem payload) {
        if (!properties.isEnabled()) {
            return;
        }
        if (conversationId == null || payload == null) {
            return;
        }
        saveEvent(conversationId,
                MessagingOutboxEventType.MESSAGE_SENT,
                properties.getTopics().getMessageSent(),
                "conversation:" + conversationId,
                writeJson(payload));
    }

    public void enqueueConversationRead(ReadEvent payload) {
        if (!properties.isEnabled()) {
            return;
        }
        if (payload == null || payload.conversationId() == null) {
            return;
        }
        saveEvent(payload.conversationId(),
                MessagingOutboxEventType.CONVERSATION_READ,
                properties.getTopics().getConversationRead(),
                "conversation:" + payload.conversationId(),
                writeJson(payload));
    }

    private void saveEvent(Long aggregateId,
            MessagingOutboxEventType eventType,
            String topic,
            String key,
            String payload) {
        Instant now = Instant.now();
        MessagingOutboxEvent event = new MessagingOutboxEvent();
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        event.setTopic(topic);
        event.setEventKey(key);
        event.setPayload(payload);
        event.setStatus(MessagingOutboxStatus.NEW);
        event.setAttempts(0);
        event.setCreatedAt(now);
        event.setAvailableAt(now);
        outboxRepository.save(event);
    }

    private String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize messaging outbox payload", ex);
        }
    }
}
