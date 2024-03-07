package ru.develop.bank.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private String name;

    //    private String username;
//    private String firstName;
//    private Set<Role> roles;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;

        //    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() { return roles; }
//
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return name;
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
        return name;
    }
}




