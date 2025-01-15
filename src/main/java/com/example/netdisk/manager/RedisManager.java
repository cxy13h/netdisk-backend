package com.example.netdisk.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisManager {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 设置值并带过期时间
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    // 获取值
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除键
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 尝试获取锁
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit timeUnit) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    // 释放锁
    public void unlock(String lockKey, String lockValue) {
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }

    // 添加分片到已上传列表
    public void addUploadedChunk(String fileMd5, int chunkIndex) {
        String key = "upload:" + fileMd5 + ":uploadedChunks";
        redisTemplate.opsForSet().add(key, chunkIndex);
    }

    // 检查分片是否已上传
    public boolean isChunkUploaded(String fileMd5, int chunkIndex) {
        String key = "upload:" + fileMd5 + ":uploadedChunks";
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, chunkIndex));
    }

    // 获取已上传分片列表的大小
    public long getUploadedChunkCount(String fileMd5) {
        String key = "upload:" + fileMd5 + ":uploadedChunks";
        return redisTemplate.opsForSet().size(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
