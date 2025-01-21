package com.example.netdisk.service;

import com.example.netdisk.entity.NetdiskFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
public interface INetdiskFileService extends IService<NetdiskFile> {


    NetdiskFile getNetdiskFileByMd5(String fileMd5);

    boolean checkFileMD5Exist(String fileMd5);

    ResponseEntity<String> processChunkUpload(String fileMd5, int chunkIndex, int totalChunks, MultipartFile file);

    void saveFileRecord(String fileId, String md5Hash, long fileSize);
}
