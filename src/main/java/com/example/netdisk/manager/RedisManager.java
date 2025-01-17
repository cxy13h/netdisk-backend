package com.example.netdisk.manager;

import com.example.netdisk.common.constant.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储分片内容
     * @param fileMd5 文件的 MD5
     * @param chunkMd5 分片的 MD5
     * @param chunkData 分片数据
     */
    public void storeChunk(String fileMd5, String chunkMd5, byte[] chunkData) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, chunkMd5);
        redisTemplate.opsForValue().set(key, chunkData);
    }

    /**
     * 标记分片为已上传
     * @param fileMd5 文件的 MD5
     * @param chunkMd5 分片的 MD5
     */
    public void markChunkUploaded(String fileMd5, String chunkMd5) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, RedisKeyConstants.SUFFIX_UPLOADED_CHUNKS);
        redisTemplate.opsForSet().add(key, chunkMd5);
    }

    /**
     * 检查分片是否已上传
     * @param fileMd5 文件的 MD5
     * @param chunkMd5 分片的 MD5
     * @return 是否已上传
     */
    public boolean isChunkUploaded(String fileMd5, String chunkMd5) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, RedisKeyConstants.SUFFIX_UPLOADED_CHUNKS);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, chunkMd5));
    }

    /**
     * 获取已上传分片的数量
     * @param fileMd5 文件的 MD5
     * @return 已上传分片的数量
     */
    public long getUploadedChunkCount(String fileMd5) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, RedisKeyConstants.SUFFIX_UPLOADED_CHUNKS);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 获取所有分片内容
     * @param fileMd5 文件的 MD5
     * @return 所有分片的内容
     */
    public Map<String, byte[]> getAllChunks(String fileMd5) {
        String uploadedChunksKey = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, RedisKeyConstants.SUFFIX_UPLOADED_CHUNKS);
        Set<Object> chunkMd5Set = redisTemplate.opsForSet().members(uploadedChunksKey);

        Map<String, byte[]> chunks = new LinkedHashMap<>();
        if (chunkMd5Set != null) {
            for (Object chunkMd5 : chunkMd5Set) {
                String chunkKey = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, (String) chunkMd5);
                byte[] chunkData = (byte[]) redisTemplate.opsForValue().get(chunkKey);
                chunks.put((String) chunkMd5, chunkData);
            }
        }
        return chunks;
    }

    /**
     * 清理分片数据
     * @param fileMd5 文件的 MD5
     */
    public void clearChunks(String fileMd5) {
        String uploadedChunksKey = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, RedisKeyConstants.SUFFIX_UPLOADED_CHUNKS);
        Set<Object> chunkMd5Set = redisTemplate.opsForSet().members(uploadedChunksKey);

        if (chunkMd5Set != null) {
            for (Object chunkMd5 : chunkMd5Set) {
                String chunkKey = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, fileMd5, (String) chunkMd5);
                redisTemplate.delete(chunkKey);
            }
        }

        redisTemplate.delete(uploadedChunksKey);
    }

    /**
     * 尝试获取 Redis 锁
     * @param lockKey 锁的 Key
     * @param lockValue 锁的 Value
     * @param timeout 锁的过期时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, String.format(RedisKeyConstants.SUFFIX_CHUNK_LOCK, lockKey));
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, lockValue, timeout, unit);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放 Redis 锁
     * @param lockKey 锁的 Key
     * @param lockValue 锁的 Value
     */
    public void unlock(String lockKey, String lockValue) {
        String key = RedisKeyConstants.buildKey(RedisKeyConstants.PREFIX_UPLOAD, String.format(RedisKeyConstants.SUFFIX_CHUNK_LOCK, lockKey));
        String currentValue = (String) redisTemplate.opsForValue().get(key);
        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
