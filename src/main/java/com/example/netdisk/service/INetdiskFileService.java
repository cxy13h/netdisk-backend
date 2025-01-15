package com.example.netdisk.service;

import com.example.netdisk.entity.NetdiskFile;
import com.baomidou.mybatisplus.extension.service.IService;
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

    String uploadFile(MultipartFile file) throws IOException;
    Boolean checkFileExist(InputStream file) throws IOException;

    NetdiskFile getNetdiskFileByMd5(String fileMd5);

    boolean checkFileMD5Exist(String fileMd5);
}
