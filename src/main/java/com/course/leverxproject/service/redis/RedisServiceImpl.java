package com.course.leverxproject.service.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisServiceImpl implements RedisService{
private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, Duration.ofHours(24));
    }

    @Override
    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    @Override
    public void removeVerificationCode(String email) {
        redisTemplate.delete(email);
    }
}
