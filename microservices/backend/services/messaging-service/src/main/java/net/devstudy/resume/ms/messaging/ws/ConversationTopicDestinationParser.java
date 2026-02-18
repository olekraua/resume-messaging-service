package net.devstudy.resume.ms.messaging.ws;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class ConversationTopicDestinationParser {

    private static final Pattern CONVERSATION_TOPIC_PATTERN =
            Pattern.compile("^/user/queue/conversations/(\\d+)/(messages|read)$");

    public Optional<Long> extractConversationId(String destination) {
        if (destination == null || destination.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = CONVERSATION_TOPIC_PATTERN.matcher(destination.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        try {
            long conversationId = Long.parseLong(matcher.group(1));
            if (conversationId <= 0) {
                return Optional.empty();
            }
            return Optional.of(conversationId);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}
