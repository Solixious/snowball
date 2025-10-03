package dev.indian.snowball.model;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
public class KiteAuthentication implements Authentication {

    private final String userId;
    private final String userName;
    private final String apiKey;
    private final String accessToken;
    private final String publicToken;
    private boolean authenticated = true;

    public KiteAuthentication(String userId, String userName, String apiKey, String accessToken, String publicToken) {
        this.userId = userId;
        this.userName = userName;
        this.apiKey = apiKey;
        this.accessToken = accessToken;
        this.publicToken = publicToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return userName;
    }
}