package net.devstudy.resume.ms.messaging.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging.outbox")
public class MessagingOutboxProperties {

    private boolean enabled = true;
    private int pollIntervalMs = 1000;
    private int batchSize = 200;
    private int maxAttempts = 20;
    private long retryDelayMs = 5000L;
    private final Topics topics = new Topics();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPollIntervalMs() {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(int pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }

    public void setRetryDelayMs(long retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }

    public Topics getTopics() {
        return topics;
    }

    public static class Topics {
        private String messageSent = "resume.messaging.message-sent";
        private String conversationRead = "resume.messaging.conversation-read";

        public String getMessageSent() {
            return messageSent;
        }

        public void setMessageSent(String messageSent) {
            this.messageSent = messageSent;
        }

        public String getConversationRead() {
            return conversationRead;
        }

        public void setConversationRead(String conversationRead) {
            this.conversationRead = conversationRead;
        }
    }
}
