package com.example.netdisk.manager;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class MinioManager {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * 上传文件到 MinIO
     *
     * @param objectName 对象名称（文件路径）
     * @param stream     文件流
     * @param size       文件大小
     * @param contentType 文件类型
     */
    public  void uploadFile(String objectName, InputStream stream, long size, String contentType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(stream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    /**
     * 从 MinIO 下载文件
     *
     * @param objectName 对象名称（文件路径）
     * @return 文件流
     */
    public InputStream downloadFile(String objectName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 从 MinIO 删除文件
     *
     * @param objectName 对象名称（文件路径）
     */
    public void deleteFile(String objectName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}
