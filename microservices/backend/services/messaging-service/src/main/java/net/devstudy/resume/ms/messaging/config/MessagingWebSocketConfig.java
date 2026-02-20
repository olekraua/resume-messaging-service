package net.devstudy.resume.ms.messaging.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import net.devstudy.resume.web.config.CorsProperties;
import net.devstudy.resume.ms.messaging.ws.WebSocketAuthChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnProperty(name = "app.messaging.realtime.stomp.enabled", havingValue = "true")
public class MessagingWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String USER_DESTINATION_BROADCAST = "/topic/unresolved-user-destination";
    private static final String USER_REGISTRY_BROADCAST = "/topic/simp-user-registry";

    private final CorsProperties corsProperties;
    private final WebSocketAuthChannelInterceptor authChannelInterceptor;
    private final MessagingBrokerRelayProperties brokerRelayProperties;

    public MessagingWebSocketConfig(CorsProperties corsProperties,
            WebSocketAuthChannelInterceptor authChannelInterceptor,
            MessagingBrokerRelayProperties brokerRelayProperties) {
        this.corsProperties = corsProperties;
        this.authChannelInterceptor = authChannelInterceptor;
        this.brokerRelayProperties = brokerRelayProperties;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        var registration = registry.addEndpoint("/ws");
        if (corsProperties != null && !corsProperties.getAllowedOrigins().isEmpty()) {
            registration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins().toArray(String[]::new));
        }
        registration.withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(brokerRelayProperties.getHost())
                .setRelayPort(brokerRelayProperties.getPort())
                .setClientLogin(brokerRelayProperties.getClientLogin())
                .setClientPasscode(brokerRelayProperties.getClientPasscode())
                .setSystemLogin(brokerRelayProperties.getSystemLogin())
                .setSystemPasscode(brokerRelayProperties.getSystemPasscode())
                .setVirtualHost(brokerRelayProperties.getVirtualHost())
                .setSystemHeartbeatSendInterval(brokerRelayProperties.getSystemHeartbeatSendIntervalMs())
                .setSystemHeartbeatReceiveInterval(brokerRelayProperties.getSystemHeartbeatReceiveIntervalMs())
                .setUserDestinationBroadcast(USER_DESTINATION_BROADCAST)
                .setUserRegistryBroadcast(USER_REGISTRY_BROADCAST);
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }
}
