package net.devstudy.resume.ms.messaging.ws;

import java.io.Serial;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import net.devstudy.resume.auth.api.model.CurrentProfile;

public final class WebSocketProfileAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = -4736853079421144948L;

    private final CurrentProfile principal;
    private final Object credentials;
    private final String name;

    public WebSocketProfileAuthenticationToken(CurrentProfile principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.name = principal.getId().toString();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public CurrentProfile getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return name;
    }
}
