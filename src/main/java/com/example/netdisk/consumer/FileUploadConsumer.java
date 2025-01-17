package com.example.netdisk.consumer;

import com.example.netdisk.manager.RedisManager;
import com.example.netdisk.service.MinioService;
import com.example.netdisk.service.TaskCoordinatorService;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FileUploadConsumer {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private TaskCoordinatorService taskCoordinatorService;

    @Autowired
    MinioService minioService;
    /**
     * 处理分片上传任务
     */
    @RabbitListener(queues = "${mq.chunk.queue}")
    public void handleChunkUploadTask(Map<String, Object> message) {
        String fileMd5 = (String) message.get("fileMd5");
        String chunkMd5 = (String) message.get("chunkMd5");
        int totalChunks = (Integer) message.get("totalChunks");
        // 锁的 Key 和 Value
        String lockKey = "chunk:lock:" + chunkMd5;
        String lockValue = UUID.randomUUID().toString();

        // 尝试获取锁
        if (!redisManager.tryLock(lockKey, lockValue, 5, TimeUnit.MINUTES)) {
            System.out.println("Chunk is already being processed by another consumer: " + chunkMd5);
            return; // 无法获取锁，跳过
        }

        try {
            // 获取分片数据
            byte[] chunkData = (byte[]) message.get("chunkData");

            // 存储分片内容到 Redis
            redisManager.storeChunk(fileMd5, chunkMd5, chunkData);

            // 标记分片为已上传
            redisManager.markChunkUploaded(fileMd5, chunkMd5);

            // 通知任务协调服务
            taskCoordinatorService.reportChunkUploaded(fileMd5, totalChunks);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放锁
            redisManager.unlock(lockKey, lockValue);
        }
    }

    /**
     * 处理文件合并任务
     */
    @RabbitListener(queues = "${mq.merge.queue}")
    public void handleMergeTask(Map<String, Object> message) {
        String fileMd5 = (String) message.get("fileMd5");
        int totalChunks = (int) message.get("totalChunks");

        try {
            // 合并文件并上传到 MinIO
            mergeAndUploadFile(fileMd5, totalChunks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并文件并上传到 MinIO
     */
    private void mergeAndUploadFile(String fileMd5, int totalChunks) throws Exception {
        // 从 Redis 获取所有分片数据
        Map<String, byte[]> chunks = redisManager.getAllChunks(fileMd5);

        // 合并分片数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] chunk : chunks.values()) {
            outputStream.write(chunk);
        }

        // 上传合并后的文件到 MinIO
        byte[] completeFile = outputStream.toByteArray();
        String fileId = minioService.uploadFile(completeFile);

        // 清理 Redis 数据
        redisManager.clearChunks(fileMd5);

        System.out.println("File merged and uploaded to MinIO with ID: " + fileId);
    }
}
