package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.Alarm;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.AlarmEntityRepository;
import com.fastcampus.sns.repository.UserCacheRepository;
import com.fastcampus.sns.repository.UserEntityRepository;
import com.fastcampus.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserEntityRepository userEntityRepository;
  private final BCryptPasswordEncoder encoder;
  private final AlarmEntityRepository alarmEntityRepository;
  private final UserCacheRepository userCacheRepository;
  
  @Value("${jwt.secret-key}")
  private String secretKey;
  
  @Value("${jwt.token.expired-time-ms}")
  private Long expiredTimeMs;
  
  public User loadUserByUserName(String userName) {
    return userCacheRepository.getUser(userName).orElseGet(() -> {
      return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(() ->
              new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName))
      );
    });
  }
  
  @Transactional
  public User join(String username, String password) {
    // 회원가입하려는 userName 으로 회원가입된 user 가 있는지
    userEntityRepository.findByUserName(username)
            .ifPresent( it -> {
              throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", username));
            });
    // 회원 가입 진행 =user 를 등록
    UserEntity userEntity = userEntityRepository.save(UserEntity.of(username, encoder.encode(password)));
    return User.fromEntity(userEntity);
  }
  
  public String login(String username, String password) {
    // 회원가입 여부 체크
    User user = loadUserByUserName(username);
    userCacheRepository.setUser(user);
    
    // 비밀번호 체크
    if(!encoder.matches(password, user.getPassword())) {
      throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD, "");
    }
    
    // 토큰 생성
    String token = JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);
    return token;
  }
  
  public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
    return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    
  }
}
