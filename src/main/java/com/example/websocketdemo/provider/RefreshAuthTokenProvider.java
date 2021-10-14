package com.example.websocketdemo.provider;

import com.example.websocketdemo.authentication.RefreshAuthToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class RefreshAuthTokenProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RefreshAuthToken refreshAuthToken = (RefreshAuthToken) authentication;
        refreshAuthToken.setAuthenticated(true);

        return refreshAuthToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshAuthToken.class.isAssignableFrom(authentication);
    }
}
