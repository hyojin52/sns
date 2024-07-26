package com.fastcampus.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {
  private String resultCode;
  private T result;
  
  public static Response<Void> error(String errorCode) {
    return new Response<>(errorCode, null);
  }
  
  public static Response<Void> success() {
    return new Response<>("SUCCESS", null);
  }
  
  public static <T> Response<T> success(T result) {
    return new Response<>("SUCCESS", result);
  }
  
  public String toStream() {
    if(result == null) {
      return "{ \n" +
              "  \"resultCode\":" + "\"" + resultCode + "\", \n" +
              "  \"result\":" + null +
              "\n }";
    }
    
    return "{ \n" +
            "  \"resultCode\":" + "\"" + resultCode + "\", \n" +
            "  \"result\":" + "\"" + result + "\"" +
            "\n }";
  }
}
