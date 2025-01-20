package com.example.netdisk.service;

import com.example.netdisk.dto.TreeNodeVO;
import com.example.netdisk.entity.NetdiskUserFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
public interface INetdiskUserFileService extends IService<NetdiskUserFile> {

    TreeNodeVO getDirectoryTree(Long userId, Long parentDirectoryId);
}
