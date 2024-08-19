package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {
  
  private Integer id;
  private String username;
  private String password;
  private UserRole userRole;
  private Timestamp registeredAt;
  private Timestamp updatedAt;
  private Timestamp deletedAt;
  
  public static User fromEntity(UserEntity userEntity) {
    return new User(
            userEntity.getId(),
            userEntity.getUserName(),
            userEntity.getPassword(),
            userEntity.getRole(),
            userEntity.getRegisteredAt(),
            userEntity.getUpdatedAt(),
            userEntity.getDeletedAt()
    );
  }
  
  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
  }
  
  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return this.deletedAt == null;
  }
  
  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return this.deletedAt == null;
  }
  
  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return this.deletedAt == null;
  }
  
  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return this.deletedAt == null;
  }
}
