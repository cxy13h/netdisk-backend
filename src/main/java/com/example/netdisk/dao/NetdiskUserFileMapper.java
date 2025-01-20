package com.example.netdisk.dao;

import com.example.netdisk.dto.DirectoryTreeNode;
import com.example.netdisk.entity.NetdiskUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 刘潞
 * @since 2025-01-13
 */
@Mapper
public interface NetdiskUserFileMapper extends BaseMapper<NetdiskUserFile> {

    List<DirectoryTreeNode> getDirectoryTree(Long userId, Long parentDirectoryId);
}
