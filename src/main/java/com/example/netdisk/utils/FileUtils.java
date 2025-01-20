package com.example.netdisk.utils;

import com.example.netdisk.common.constant.FileKeyConstants;
import com.example.netdisk.dto.DirectoryTreeNode;
import com.example.netdisk.dto.TreeNodeVO;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileUtils {
    //计算文件MD5
    public static String getMD5(InputStream file) throws IOException {
        return DigestUtils.md5DigestAsHex(file);
    }
    //产生文件UID
    public static String generateUniqueId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    // List转为树形
    public static TreeNodeVO buildTree(List<DirectoryTreeNode> nodes) {
        // 分开处理目录和文件
        List<DirectoryTreeNode> directories = nodes.stream()
                .filter(node -> FileKeyConstants.DIRECTORY.equals(node.getType())).toList();

        List<DirectoryTreeNode> files = nodes.stream()
                .filter(node -> FileKeyConstants.FILE.equals(node.getType())).toList();

        // 创建映射表，用于存储目录的唯一键和 TreeNodeVO 的对应关系
        Map<Long, TreeNodeVO> directoryMap = new HashMap<>();

        // 第一次遍历：将所有目录节点放入映射表中
        for (DirectoryTreeNode directory : directories) {
            TreeNodeVO treeNode = new TreeNodeVO(directory.getId(), directory.getType(), directory.getName());
            directoryMap.put(directory.getId(), treeNode);
        }

        // 收集所有目录的 id 和 parentDirectoryId
        Set<Long> allIds = directories.stream()
                .map(DirectoryTreeNode::getId)
                .collect(Collectors.toSet());

        Set<Long> allParentIds = directories.stream()
                .map(DirectoryTreeNode::getParentDirectoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 找到根目录的 parentDirectoryId：这些 parentDirectoryId 从未出现在 id 列表中
        Long rootParentId = allParentIds.stream()
                .filter(parentId -> !allIds.contains(parentId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到根目录！"));

        // 找到根目录的 id：这些 id 对应的 parentDirectoryId 等于 rootParentId
        Long rootId = directories.stream()
                .filter(directory -> rootParentId.equals(directory.getParentDirectoryId()))
                .map(DirectoryTreeNode::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到根目录！"));

        // 创建根节点
        TreeNodeVO root = directoryMap.get(rootId);
        if (root == null) {
            throw new IllegalStateException("根节点不存在！");
        }

        // 第二次遍历：构建目录的树形结构
        for (DirectoryTreeNode directory : directories) {
            if (directory.getParentDirectoryId() != null && directoryMap.containsKey(directory.getParentDirectoryId())) {
                // 如果当前目录有父目录，则将其添加到父目录的 children 列表中
                TreeNodeVO parentDirectory = directoryMap.get(directory.getParentDirectoryId());
                parentDirectory.getChildren().add(directoryMap.get(directory.getId()));
            }
        }

        // 将文件插入到对应的目录下
        for (DirectoryTreeNode file : files) {
            if (directoryMap.containsKey(file.getParentDirectoryId())) {
                TreeNodeVO parentDirectory = directoryMap.get(file.getParentDirectoryId());
                TreeNodeVO fileNode = new TreeNodeVO(file.getId(), file.getType(), file.getName());
                parentDirectory.getChildren().add(fileNode);
            }
        }

        // 返回根节点
        return root;
    }
}