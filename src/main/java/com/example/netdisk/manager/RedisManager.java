package com.example.netdisk.manager;

import com.example.netdisk.common.constant.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储分片内容到 Redis
     * @param fileMd5 文件的 MD5
     * @param chunkIndex 分片索引
     * @param chunkData 分片数据
     */
    public void storeChunk(String fileMd5, int chunkIndex, byte[] chunkData) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.CHUNK, String.valueOf(chunkIndex));
        redisTemplate.opsForValue().set(key, chunkData);
    }

    /**
     * 从 Redis 获取分片数据
     * @param fileMd5 文件的 MD5
     * @param chunkIndex 分片索引
     * @return 分片数据
     */
    public byte[] getChunkData(String fileMd5, int chunkIndex) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.CHUNK, String.valueOf(chunkIndex));
        return (byte[]) redisTemplate.opsForValue().get(key);
    }

    /**
     * 标记分片为已上传
     * @param fileMd5 文件的 MD5
     * @param chunkIndex 分片索引
     */
    public void markChunkUploaded(String fileMd5, int chunkIndex) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.UPLOADED_CHUNKS);
        redisTemplate.opsForSet().add(key, chunkIndex);
    }

    /**
     * 检查分片是否已上传
     * @param fileMd5 文件的 MD5
     * @param chunkIndex 分片索引
     * @return 是否已上传
     */
    public boolean isChunkUploaded(String fileMd5, int chunkIndex) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.UPLOADED_CHUNKS);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, chunkIndex));
    }

    /**
     * 获取已上传分片的数量
     * @param fileMd5 文件的 MD5
     * @return 已上传分片的数量
     */
    public long getUploadedChunkCount(String fileMd5) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.UPLOADED_CHUNKS);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 设置 Redis 锁
     * @param lockKey 锁的 Key
     * @param lockValue 锁的 Value
     * @param timeout 锁的有效期
     * @param timeUnit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit timeUnit) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, timeUnit);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放 Redis 锁
     * @param lockKey 锁的 Key
     * @param lockValue 锁的 Value
     */
    public void unlock(String lockKey, String lockValue) {
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 清理 Redis 中存储的分片数据
     * @param fileMd5 文件的 MD5
     * @param totalChunks 文件的总分片数
     */
    public void clearChunks(String fileMd5, int totalChunks) {
        // 删除分片数据
        for (int i = 1; i <= totalChunks; i++) {
            String chunkKey = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.CHUNK, String.valueOf(i));
            redisTemplate.delete(chunkKey);
        }

        // 删除已上传分片集合
        String uploadedChunksKey = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, fileMd5, RedisKeyConstants.UPLOADED_CHUNKS);
        redisTemplate.delete(uploadedChunksKey);
    }
}

