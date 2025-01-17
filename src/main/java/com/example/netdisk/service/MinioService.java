package com.example.netdisk.service;

import com.example.netdisk.manager.MinioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class MinioService {

    @Autowired
    private MinioManager minioManager; // 使用更新后的 MinioManager

    /**
     * 上传完整文件到 MinIO
     *
     * @param completeFile 合并后的完整文件字节数组
     * @return 生成的文件存储 ID
     * @throws Exception 如果上传失败则抛出异常
     */
    public String uploadFile(byte[] completeFile) throws Exception {
        // 随机生成文件存储 ID
        String fileId = UUID.randomUUID().toString();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(completeFile)) {
            // 使用 MinioManager 上传文件
            minioManager.uploadFile(
                    fileId,                    // 文件存储名称
                    inputStream,               // 文件流
                    completeFile.length,       // 文件大小
                    "application/octet-stream" // 默认文件类型
            );

            System.out.println("File successfully uploaded to MinIO with ID: " + fileId);
        } catch (Exception e) {
            System.err.println("Failed to upload file to MinIO: " + e.getMessage());
            throw e; // 抛出异常给调用方处理
        }

        return fileId; // 返回文件存储 ID
    }
}
