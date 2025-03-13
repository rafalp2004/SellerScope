package com.course.leverxproject.service.redis;

public interface RedisService {
    void saveVerificationCode(String email, String code);
    String getVerificationCode(String email);
    void removeVerificationCode(String email);
}
