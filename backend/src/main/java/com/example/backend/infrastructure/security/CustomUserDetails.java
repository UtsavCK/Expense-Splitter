package com.example.backend.infrastructure.security;

import com.example.backend.domain.model.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Return roles/authorities here. For now, empty list or simple ROLE_USER
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return user.getPasswordHash(); // must match your User domain property
  }

  @Override
  public String getUsername() {
    return user.getEmail(); // or user.getName(), depending on login
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
