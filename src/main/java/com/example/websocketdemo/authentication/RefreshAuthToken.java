package com.example.websocketdemo.authentication;


import org.springframework.security.authentication.AbstractAuthenticationToken;

public class RefreshAuthToken extends AbstractAuthenticationToken {

    private String principal;
    private Object credentials;

    public RefreshAuthToken(){
        super(null);
        this.principal = null;
        this.credentials = null;

    }



    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
