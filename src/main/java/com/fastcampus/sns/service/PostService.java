package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
  
  private final PostEntityRepository postEntityRepository;
  
  private final UserEntityRepository userEntityRepository;
  
  
  @Transactional
  public void create(String title, String body, String userName) {
    // user find
    UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
            new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("user name is %s", userName))
    );
    
    // post save
    PostEntity saved = postEntityRepository.save(PostEntity.of(title, body, userEntity));
  }
  
}
