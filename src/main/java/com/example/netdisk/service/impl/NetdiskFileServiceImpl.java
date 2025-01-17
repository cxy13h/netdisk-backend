package com.example.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.example.netdisk.entity.NetdiskFile;
import com.example.netdisk.dao.NetdiskFileMapper;
import com.example.netdisk.manager.RedisManager;
import com.example.netdisk.producer.FileUploadProducer;
import com.example.netdisk.service.INetdiskFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.netdisk.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
@Service
public class NetdiskFileServiceImpl extends ServiceImpl<NetdiskFileMapper, NetdiskFile> implements INetdiskFileService {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private FileUploadProducer fileUploadProducer;

    /**
     * 处理分片上传逻辑
     */
    public void processChunkUpload(String fileMd5, int chunkIndex, int totalChunks, MultipartFile file) {
        // 检查分片是否已经上传
        if (redisManager.isChunkUploaded(fileMd5, chunkIndex)) {
            throw new IllegalArgumentException("Chunk already uploaded.");
        }

        // 发布分片上传任务到消息队列
        fileUploadProducer.publishChunkUploadTask(fileMd5, chunkIndex, totalChunks, file);

    }

    @Override
    public NetdiskFile getNetdiskFileByMd5(String fileMd5) {
        LambdaQueryWrapper<NetdiskFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NetdiskFile::getMd5Hash, fileMd5);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public boolean checkFileMD5Exist(String fileMd5) {
        NetdiskFile netdiskFile = this.getNetdiskFileByMd5(fileMd5);
        return !ObjectUtils.isEmpty(netdiskFile);
    }
}
