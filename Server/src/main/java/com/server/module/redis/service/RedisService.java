package com.server.module.redis.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	private final StringRedisTemplate stringRedisTemplate;

	public RedisService(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public String getData(String key) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		return valueOperations.get(key);
	}

	public void setExpire(String email, String authCode, long duration) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(duration);
		valueOperations.set(email, authCode, expireDuration);
	}

	public void deleteData(String key) {
		stringRedisTemplate.delete(key);
	}
}
