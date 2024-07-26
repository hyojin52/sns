package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entiry.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class User {
  
  private Integer id;
  
  private String userName;
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
}
