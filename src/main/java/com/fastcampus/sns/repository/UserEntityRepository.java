package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entiry.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
  
  Optional<UserEntity> findByUserName(String userName);
}
