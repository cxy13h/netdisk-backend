package com.example.netdisk.service.impl;

import com.example.netdisk.dto.DirectoryTreeNode;
import com.example.netdisk.dto.TreeNodeVO;
import com.example.netdisk.entity.NetdiskUserFile;
import com.example.netdisk.dao.NetdiskUserFileMapper;
import com.example.netdisk.service.INetdiskUserFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.netdisk.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
@Service
public class NetdiskUserFileServiceImpl extends ServiceImpl<NetdiskUserFileMapper, NetdiskUserFile> implements INetdiskUserFileService {


    @Override
    public TreeNodeVO getDirectoryTree(Long userId, Long parentDirectoryId) {
        List<DirectoryTreeNode> directoryTreeNodeList = baseMapper.getDirectoryTree(userId, parentDirectoryId);
        return FileUtils.buildTree(directoryTreeNodeList);

    }
}
