package com.example.netdisk.service;

import com.example.netdisk.manager.RedisManager;
import com.example.netdisk.producer.FileUploadProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TaskCoordinatorService {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private FileUploadProducer fileUploadProducer;

    /**
     * 报告分片已上传
     */
    public void reportChunkUploaded(String fileMd5, int totalChunks) {
        long uploadedChunks = redisManager.getUploadedChunkCount(fileMd5);

        if (uploadedChunks == totalChunks) {
            // 尝试获取 Redis 锁，确保合并任务唯一
            String lockKey = "merge:lock:" + fileMd5;
            String lockValue = UUID.randomUUID().toString();

            if (redisManager.tryLock(lockKey, lockValue, 10, TimeUnit.MINUTES)) {
                try {
                    // 所有分片已上传，发布合并任务
                    fileUploadProducer.publishMergeTask(fileMd5, totalChunks);
                    System.out.println("All chunks uploaded. File merge task published: fileMd5=" + fileMd5);
                } finally {
                    redisManager.unlock(lockKey, lockValue); // 释放锁
                }
            } else {
                System.out.println("Merge task already in progress for fileMd5=" + fileMd5);
            }
        }
    }
}
