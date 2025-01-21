package com.example.netdisk.service;

import com.example.netdisk.common.constant.RedisKeyConstants;
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

    @Autowired
    private INetdiskFileService iNetdiskFileService;

    /**
     * 报告分片已上传
     */
    public void reportChunkUploaded(String fileMd5, int totalChunks) {
        long uploadedChunks = redisManager.getUploadedChunkCount(fileMd5);

        if (uploadedChunks == totalChunks) {
            String lockKey = RedisKeyConstants.buildKey(RedisKeyConstants.UPLOAD, RedisKeyConstants.MERGE, fileMd5);
            String lockValue = UUID.randomUUID().toString();

            // 尝试获取锁，确保只有一个合并任务执行
            if (redisManager.tryLock(lockKey, lockValue, 10, TimeUnit.MINUTES)) {
                try {
                    fileUploadProducer.publishMergeTask(fileMd5, totalChunks);
                } finally {
                    redisManager.unlock(lockKey, lockValue);
                }
            }
        }
    }

    public void reportSaveFileRecord(String fileId, String fileMd5, int size) {
        iNetdiskFileService.saveFileRecord(fileId, fileMd5, size);
    }
}
