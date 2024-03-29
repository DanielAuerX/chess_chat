package com.chrispbacon.chesschat.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Collection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student implements UserDetails {
  @Id private UUID id;
  private String email;
  private String userName;
  private String password;
  private String firstName;
  private String lastName;
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return role.getAuthorities();
  }

  @Override
  public String getUsername() {
    return userName;
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
