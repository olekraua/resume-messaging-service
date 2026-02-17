package net.devstudy.resume.ms.messaging.ws;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import net.devstudy.resume.auth.api.model.CurrentProfile;
import net.devstudy.resume.messaging.api.model.ConversationParticipant;
import net.devstudy.resume.messaging.internal.repository.storage.ConversationParticipantRepository;
import net.devstudy.resume.web.security.CurrentProfileJwtConverter;

class WebSocketAuthChannelInterceptorTest {

    private final JwtDecoder jwtDecoder = mock(JwtDecoder.class);
    private final CurrentProfileJwtConverter jwtConverter = mock(CurrentProfileJwtConverter.class);
    private final ConversationParticipantRepository participantRepository =
            mock(ConversationParticipantRepository.class);

    private WebSocketAuthChannelInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new WebSocketAuthChannelInterceptor(jwtDecoder, jwtConverter, participantRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowSubscribeToConversationTopicForParticipant() {
        Authentication authentication = authentication(7L);
        Message<byte[]> message = subscribeMessage(authentication, "/topic/conversations/42/messages");
        when(participantRepository.findByConversationIdAndProfileId(eq(42L), eq(7L)))
                .thenReturn(Optional.of(mock(ConversationParticipant.class)));

        assertDoesNotThrow(() -> interceptor.preSend(message, null));

        verify(participantRepository).findByConversationIdAndProfileId(42L, 7L);
    }

    @Test
    void shouldDenySubscribeWhenNotParticipant() {
        Authentication authentication = authentication(7L);
        Message<byte[]> message = subscribeMessage(authentication, "/topic/conversations/42/messages");
        when(participantRepository.findByConversationIdAndProfileId(eq(42L), eq(7L)))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, null));
    }

    @Test
    void shouldDenySubscribeForInvalidDestination() {
        Authentication authentication = authentication(7L);
        Message<byte[]> message = subscribeMessage(authentication, "/topic/public/news");

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, null));

        verifyNoInteractions(participantRepository);
    }

    @Test
    void shouldDenySubscribeWithoutAuthentication() {
        Message<byte[]> message = subscribeMessage(null, "/topic/conversations/42/messages");

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, null));

        verifyNoInteractions(participantRepository);
    }

    private Authentication authentication(Long profileId) {
        CurrentProfile principal = new CurrentProfile(profileId, "uid-" + profileId, "Test User");
        return new UsernamePasswordAuthenticationToken(principal, "n/a", List.of());
    }

    private Message<byte[]> subscribeMessage(Authentication authentication, String destination) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setSessionId("session-1");
        accessor.setSubscriptionId("sub-1");
        accessor.setDestination(destination);
        accessor.setUser(authentication);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
