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

	public boolean isExist(String key) {
		return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
	}

	public void setExpire(String key, String value, long duration) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(duration);
		valueOperations.set(key, value, expireDuration);
	}

	public long getExpire(String key) {
		return stringRedisTemplate.getExpire(key);
	}

	public void deleteData(String key) {
		stringRedisTemplate.delete(key);
	}
}
