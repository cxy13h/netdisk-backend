package com.example.netdisk.producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.chunk.queue}")
    private String chunkQueue;

    @Value("${mq.merge.queue}")
    private String mergeQueue;

    /**
     * 发布分片上传任务到队列
     */
    public void publishChunkUploadTask(String fileMd5, int chunkIndex, int totalChunks, MultipartFile file) {
        Map<String, Object> message = new HashMap<>();
        message.put("fileMd5", fileMd5);
        message.put("chunkIndex", chunkIndex);
        message.put("totalChunks", totalChunks);
        try {
            // 将分片数据转为字节数组
            message.put("chunkData", file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read chunk data.", e);
        }
        // 发送到分片上传队列
        rabbitTemplate.convertAndSend(chunkQueue, message);
    }

    /**
     * 发布文件合并任务到队列
     */
    public void publishMergeTask(String fileMd5, int totalChunks) {
        Map<String, Object> message = new HashMap<>();
        message.put("fileMd5", fileMd5);
        message.put("totalChunks", totalChunks);
        // 发送到文件合并队列
        rabbitTemplate.convertAndSend(mergeQueue, message);
    }
}

