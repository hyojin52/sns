package com.fastcampus.sns.fixture;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.entiry.UserEntity;

public class UserEntityFixture {

  public static UserEntity get(String userName, String password) {
    UserEntity result = new UserEntity();
    result.setId(1);
    result.setUserName(userName);
    result.setPassword(password);
    return result;
  }
}
