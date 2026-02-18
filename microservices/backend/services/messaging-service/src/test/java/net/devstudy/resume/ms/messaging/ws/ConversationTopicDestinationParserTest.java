package net.devstudy.resume.ms.messaging.ws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ConversationTopicDestinationParserTest {

    private final ConversationTopicDestinationParser parser = new ConversationTopicDestinationParser();

    @Test
    void shouldExtractConversationIdFromMessagesTopic() {
        Optional<Long> conversationId = parser.extractConversationId("/user/queue/conversations/42/messages");

        assertTrue(conversationId.isPresent());
        assertEquals(42L, conversationId.get());
    }

    @Test
    void shouldExtractConversationIdFromReadTopic() {
        Optional<Long> conversationId = parser.extractConversationId("/user/queue/conversations/777/read");

        assertTrue(conversationId.isPresent());
        assertEquals(777L, conversationId.get());
    }

    @Test
    void shouldRejectUnsupportedTopicSuffix() {
        assertFalse(parser.extractConversationId("/user/queue/conversations/42/typing").isPresent());
    }

    @Test
    void shouldRejectInvalidConversationId() {
        assertFalse(parser.extractConversationId("/user/queue/conversations/0/messages").isPresent());
        assertFalse(parser.extractConversationId("/user/queue/conversations/not-number/read").isPresent());
    }

    @Test
    void shouldRejectInvalidDestinationFormat() {
        assertFalse(parser.extractConversationId("/topic/public/news").isPresent());
        assertFalse(parser.extractConversationId("/topic/conversations/42/messages").isPresent());
        assertFalse(parser.extractConversationId("/user/queue/conversations/42/messages/extra").isPresent());
        assertFalse(parser.extractConversationId(null).isPresent());
        assertFalse(parser.extractConversationId(" ").isPresent());
    }
}
